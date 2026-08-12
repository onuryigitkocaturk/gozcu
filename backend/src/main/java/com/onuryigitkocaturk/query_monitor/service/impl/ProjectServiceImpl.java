package com.onuryigitkocaturk.query_monitor.service.impl;

import com.onuryigitkocaturk.query_monitor.connector.ConnectionCredentialEncryptor;
import com.onuryigitkocaturk.query_monitor.connector.ConnectionDetails;
import com.onuryigitkocaturk.query_monitor.connector.TableMetadataService;
import com.onuryigitkocaturk.query_monitor.dto.ProjectDashboardStatsResponse;
import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.enums.LogStatus;
import com.onuryigitkocaturk.query_monitor.enums.ProjectRole;
import com.onuryigitkocaturk.query_monitor.enums.Role;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateProjectException;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateProjectMembershipException;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateProjectTableException;
import com.onuryigitkocaturk.query_monitor.exception.InsufficientProjectRoleException;
import com.onuryigitkocaturk.query_monitor.exception.ProjectNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.TableNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.UserNotFoundException;
import com.onuryigitkocaturk.query_monitor.model.AlertLog;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.ProjectMembership;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.repository.AlertLogRepository;
import com.onuryigitkocaturk.query_monitor.repository.AlertRepository;
import com.onuryigitkocaturk.query_monitor.repository.ProjectMembershipRepository;
import com.onuryigitkocaturk.query_monitor.repository.ProjectRepository;
import com.onuryigitkocaturk.query_monitor.repository.ProjectTableRepository;
import com.onuryigitkocaturk.query_monitor.repository.QueryRepository;
import com.onuryigitkocaturk.query_monitor.repository.UserRepository;
import com.onuryigitkocaturk.query_monitor.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMembershipRepository projectMembershipRepository;
    private final ProjectTableRepository projectTableRepository;
    private final QueryRepository queryRepository;
    private final AlertRepository alertRepository;
    private final AlertLogRepository alertLogRepository;
    private final TableMetadataService tableMetadataService;
    private final ConnectionCredentialEncryptor connectionCredentialEncryptor;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                               UserRepository userRepository,
                               ProjectMembershipRepository projectMembershipRepository,
                               ProjectTableRepository projectTableRepository,
                               QueryRepository queryRepository,
                               AlertRepository alertRepository,
                               AlertLogRepository alertLogRepository,
                               TableMetadataService tableMetadataService,
                               ConnectionCredentialEncryptor connectionCredentialEncryptor) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMembershipRepository = projectMembershipRepository;
        this.projectTableRepository = projectTableRepository;
        this.queryRepository = queryRepository;
        this.alertRepository = alertRepository;
        this.alertLogRepository = alertLogRepository;
        this.tableMetadataService = tableMetadataService;
        this.connectionCredentialEncryptor = connectionCredentialEncryptor;
    }

    @Override
    @Transactional
    public Project createProject(ProjectRequest request, UUID creatorUserId) {
        if (projectRepository.existsByName(request.getName())) {
            throw new DuplicateProjectException("Proje zaten mevcut: " + request.getName());
        }

        ConnectionDetails connection = new ConnectionDetails(
                request.getDbHost(), request.getDbPort(), request.getDbName(),
                request.getDbUsername(), request.getDbPassword());
        // baglanti gercekten calisiyor mu - calismiyorsa proje hic kaydedilmez.
        tableMetadataService.listTables(connection);

        Project project = new Project(request.getName(), request.getDescription());
        project.setDbHost(request.getDbHost());
        project.setDbPort(request.getDbPort());
        project.setDbName(request.getDbName());
        project.setDbUsername(request.getDbUsername());
        project.setDbPasswordEncrypted(connectionCredentialEncryptor.encrypt(request.getDbPassword()));
        Project savedProject = projectRepository.save(project);

        // proje olusturan kisi otomatik olarak o projenin Owner'i olur.
        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + creatorUserId));
        projectMembershipRepository.save(new ProjectMembership(creator, savedProject, ProjectRole.OWNER));

        return savedProject;
    }

    @Override
    public void deleteProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + id));
        // ProjectMembership ve ProjectTable'lar cascade+orphanRemoval sayesinde otomatik silinir.
        projectRepository.delete(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + id));
    }

    @Override
    public List<ProjectMembership> getProjectsForUser(UUID userId) {
        return projectMembershipRepository.findByUserId(userId);
    }

    @Override
    public void addUserToProject(UUID projectId, UUID userId, ProjectRole role, UUID actingUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + userId));

        assertCanAssignRole(projectId, actingUserId, role);

        if (projectMembershipRepository.findByProjectIdAndUserId(projectId, userId).isPresent()) {
            throw new DuplicateProjectMembershipException("Bu kullanıcı zaten bu projenin üyesi: " + userId);
        }

        projectMembershipRepository.save(new ProjectMembership(user, project, role));
    }

    @Override
    @Transactional
    public void removeUserFromProject(UUID projectId, UUID userId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Proje bulunamadı: " + projectId);
        }
        projectMembershipRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public void changeMemberRole(UUID projectId, UUID userId, ProjectRole newRole, UUID actingUserId) {
        ProjectMembership membership = projectMembershipRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Kullanıcı bu projenin üyesi değil: " + userId));

        assertCanAssignRole(projectId, actingUserId, newRole);

        membership.setRole(newRole);
        projectMembershipRepository.save(membership);
    }

    @Override
    public List<ProjectMembership> getProjectMembers(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Proje bulunamadı: " + projectId);
        }
        return projectMembershipRepository.findByProjectId(projectId);
    }

    @Override
    public void addTableToProject(UUID projectId, String tableName) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));

        if (!tableMetadataService.listTables(toConnectionDetails(project)).contains(tableName)) {
            throw new TableNotFoundException("İzlenen veritabanında tablo bulunamadı: " + tableName);
        }

        if (projectTableRepository.existsByProjectIdAndTableName(projectId, tableName)) {
            throw new DuplicateProjectTableException(
                    "Bu tablo zaten bu projeye eklenmiş: " + tableName);
        }

        ProjectTable projectTable = new ProjectTable(tableName, project);
        projectTableRepository.save(projectTable);
    }

    @Override
    public List<ProjectTable> getProjectTables(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Proje bulunamadı: " + projectId);
        }
        return projectTableRepository.findByProjectId(projectId);
    }

    @Override
    public List<Map<String, Object>> getTableData(UUID projectId, String tableName) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));

        // whitelist kontrolu: tableName SQL'e concat edilmeden once, bu tablonun
        // gercekten bu projeye bagli oldugu dogrulanmali (SQL injection riski).
        if (!projectTableRepository.existsByProjectIdAndTableName(projectId, tableName)) {
            throw new TableNotFoundException(
                    "Bu tablo bu projeye bağlı değil: " + tableName);
        }

        return tableMetadataService.getTableData(toConnectionDetails(project), tableName);
    }

    @Override
    public List<String> discoverTables(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));
        return tableMetadataService.listTables(toConnectionDetails(project));
    }

    @Override
    public List<String> getTableColumns(UUID projectId, String tableName) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));

        if (!projectTableRepository.existsByProjectIdAndTableName(projectId, tableName)) {
            throw new TableNotFoundException("Bu tablo bu projeye bağlı değil: " + tableName);
        }

        return tableMetadataService.listColumns(toConnectionDetails(project), tableName);
    }

    /**
     * Sadece OWNER rolu atanirken ekstra bir kontrol var: bunu yapan kisi
     * global ADMIN olmali ya da o projede zaten OWNER olmali - bir Maintainer
     * kendini/baskasini Owner yapamaz.
     */
    private void assertCanAssignRole(UUID projectId, UUID actingUserId, ProjectRole roleToAssign) {
        if (roleToAssign != ProjectRole.OWNER) {
            return;
        }

        User actingUser = userRepository.findById(actingUserId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + actingUserId));
        if (actingUser.getRole() == Role.ADMIN) {
            return;
        }

        boolean actingUserIsOwner = projectMembershipRepository.findByProjectIdAndUserId(projectId, actingUserId)
                .map(m -> m.getRole() == ProjectRole.OWNER)
                .orElse(false);
        if (!actingUserIsOwner) {
            throw new InsufficientProjectRoleException("Sadece proje Owner'ı başka birini Owner yapabilir");
        }
    }

    @Override
    public ProjectDashboardStatsResponse getDashboardStats(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Proje bulunamadı: " + projectId);
        }

        long tableCount = projectTableRepository.countByProjectId(projectId);
        long queryCount = queryRepository.countByProjectId(projectId);
        long activeQueryCount = queryRepository.countByProjectIdAndActiveTrue(projectId);
        long activeAlertCount = alertRepository.countByProjectIdAndActiveTrue(projectId);

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long triggeredLast7Days = alertLogRepository.countByAlert_ProjectIdAndStatusAndExecutedAtAfter(
                projectId, LogStatus.TRIGGERED, sevenDaysAgo);
        LocalDateTime lastTriggeredAt = alertLogRepository
                .findTopByAlert_ProjectIdAndStatusOrderByExecutedAtDesc(projectId, LogStatus.TRIGGERED)
                .map(AlertLog::getExecutedAt)
                .orElse(null);

        return new ProjectDashboardStatsResponse(
                tableCount, queryCount, activeQueryCount, activeAlertCount, triggeredLast7Days, lastTriggeredAt);
    }

    private ConnectionDetails toConnectionDetails(Project project) {
        return new ConnectionDetails(
                project.getDbHost(),
                project.getDbPort(),
                project.getDbName(),
                project.getDbUsername(),
                connectionCredentialEncryptor.decrypt(project.getDbPasswordEncrypted()));
    }
}
