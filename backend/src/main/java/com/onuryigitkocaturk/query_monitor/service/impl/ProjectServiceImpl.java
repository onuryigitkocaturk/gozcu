package com.onuryigitkocaturk.query_monitor.service.impl;

import com.onuryigitkocaturk.query_monitor.connector.TableMetadataService;
import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateProjectException;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateProjectTableException;
import com.onuryigitkocaturk.query_monitor.exception.ProjectNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.TableNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.UserNotFoundException;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.repository.ProjectRepository;
import com.onuryigitkocaturk.query_monitor.repository.ProjectTableRepository;
import com.onuryigitkocaturk.query_monitor.repository.UserRepository;
import com.onuryigitkocaturk.query_monitor.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectTableRepository projectTableRepository;
    private final TableMetadataService tableMetadataService;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                               UserRepository userRepository,
                               ProjectTableRepository projectTableRepository,
                               TableMetadataService tableMetadataService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectTableRepository = projectTableRepository;
        this.tableMetadataService = tableMetadataService;
    }

    @Override
    public Project createProject(ProjectRequest request) {
        if (projectRepository.existsByName(request.getName())) {
            throw new DuplicateProjectException("Proje zaten mevcut: " + request.getName());
        }

        Project project = new Project(request.getName(), request.getDescription());
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + id));

        // owning side User; projeyi direkt silmeden önce üyelerin
        // koleksiyonundan çıkarmazsak user_project'te FK ihlali olur.
        for (User user : new HashSet<>(project.getUsers())) {
            user.getProjects().remove(project);
            userRepository.save(user);
        }

        // ProjectTable'lar cascade+orphanRemoval sayesinde otomatik silinir.
        projectRepository.delete(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public List<Project> getProjectsForUser(UUID userId) {
        return projectRepository.findByUsers_Id(userId);
    }

    @Override
    public void addUserToProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + userId));

        user.getProjects().add(project);
        userRepository.save(user);
    }

    @Override
    public void removeUserFromProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + userId));

        user.getProjects().remove(project);
        userRepository.save(user);
    }

    @Override
    public List<User> getProjectUsers(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));
        return new ArrayList<>(project.getUsers());
    }

    @Override
    public void addTableToProject(UUID projectId, String tableName) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));

        if (!tableMetadataService.listTables().contains(tableName)) {
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
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Proje bulunamadı: " + projectId);
        }

        // whitelist kontrolu: tableName SQL'e concat edilmeden once, bu tablonun
        // gercekten bu projeye bagli oldugu dogrulanmali (SQL injection riski).
        if (!projectTableRepository.existsByProjectIdAndTableName(projectId, tableName)) {
            throw new TableNotFoundException(
                    "Bu tablo bu projeye bağlı değil: " + tableName);
        }

        return tableMetadataService.getTableData(tableName);
    }
}
