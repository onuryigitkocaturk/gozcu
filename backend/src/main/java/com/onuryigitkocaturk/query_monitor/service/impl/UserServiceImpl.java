package com.onuryigitkocaturk.query_monitor.service.impl;

import com.onuryigitkocaturk.query_monitor.dto.ChangeRoleRequest;
import com.onuryigitkocaturk.query_monitor.dto.MyAccountResponse;
import com.onuryigitkocaturk.query_monitor.dto.RegisterRequest;
import com.onuryigitkocaturk.query_monitor.dto.TrustedDeviceResponse;
import com.onuryigitkocaturk.query_monitor.dto.UpdateUserRequest;
import com.onuryigitkocaturk.query_monitor.enums.Role;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateUserException;
import com.onuryigitkocaturk.query_monitor.exception.TrustedDeviceNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.UserNotFoundException;
import com.onuryigitkocaturk.query_monitor.model.TrustedDevice;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.repository.ProjectMembershipRepository;
import com.onuryigitkocaturk.query_monitor.repository.QueryRepository;
import com.onuryigitkocaturk.query_monitor.repository.TrustedDeviceRepository;
import com.onuryigitkocaturk.query_monitor.repository.UserRepository;
import com.onuryigitkocaturk.query_monitor.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProjectMembershipRepository projectMembershipRepository;
    private final QueryRepository queryRepository;
    private final TrustedDeviceRepository trustedDeviceRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                            ProjectMembershipRepository projectMembershipRepository,
                            QueryRepository queryRepository,
                            TrustedDeviceRepository trustedDeviceRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectMembershipRepository = projectMembershipRepository;
        this.queryRepository = queryRepository;
        this.trustedDeviceRepository = trustedDeviceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUserException("Kullanıcı adı zaten kullanılıyor: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("E-posta zaten kullanılıyor: " + request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), hashedPassword, request.getEmail(), Role.USER);

        return userRepository.save(user);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + username));
    }

    @Override
    public User updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + id));

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUserException("Kullanıcı adı zaten kullanılıyor: " + request.getUsername());
        }
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("E-posta zaten kullanılıyor: " + request.getEmail());
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + id));

        // owning side User uzerinden temizlemezsek, user_group junction
        // tablosundaki satirlar FK ihlaline yol acar.
        user.getGroups().clear();
        userRepository.save(user);

        // ProjectMembership artik ayri bir entity - User'in koleksiyonunu
        // temizlemek DB'deki satirlari silmez, elle silinmesi gerekiyor.
        projectMembershipRepository.deleteByUserId(id);

        userRepository.delete(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User changeRole(UUID id, ChangeRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + id));

        user.setRole(request.getRole());
        return userRepository.save(user);
    }

    @Override
    public MyAccountResponse getMyAccountOverview(UUID userId) {
        long totalQueriesWritten = queryRepository.countByCreatedById(userId);

        List<TrustedDeviceResponse> devices = trustedDeviceRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(device -> new TrustedDeviceResponse(
                        device.getId(), device.getBrowserLabel(), device.getLocationLabel(), device.getCreatedAt()))
                .toList();

        return new MyAccountResponse(totalQueriesWritten, devices);
    }

    @Override
    @Transactional
    public void removeTrustedDevice(UUID userId, UUID deviceId) {
        if (!trustedDeviceRepository.existsByIdAndUserId(deviceId, userId)) {
            throw new TrustedDeviceNotFoundException("Cihaz bulunamadı: " + deviceId);
        }
        trustedDeviceRepository.deleteByIdAndUserId(deviceId, userId);
    }
}
