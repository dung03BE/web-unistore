package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.RoleRequest;
import com.dung.UniStore.dto.response.UserResponse;
import com.dung.UniStore.entity.Role;
import com.dung.UniStore.entity.User;
import com.dung.UniStore.dto.request.UserCreationRequest;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.mapper.UserMapper;
import com.dung.UniStore.repository.IRoleRepository;
import com.dung.UniStore.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;

    public UserResponse createUser(UserCreationRequest form) throws Exception {

        String phoneNumber = form.getPhoneNumber();
        // Kiểm tra số điện thoại đã tồn tại chưa
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        Role defaultRole = (Role) roleRepository.findByName("USER")
                .orElseThrow(() -> new Exception("Default role not found"));
        // Sử dụng MapStruct để ánh xạ DTO sang Entity
        log.debug("UserCreationRequest before mapping: {}", form);
        User user = userMapper.toUser(form);
        log.info("Mapped User: {}", user);

        user.setRole(defaultRole);
        // Kiểm tra nếu không có accountId, bắt buộc phải có password
        if (form.getFacebookAccountId() == 0 && form.getGoogleAccountId() == 0) {
            String password = form.getPassword();
            String encodedPassword = passwordEncoder.encode(password); // Mã hóa mật khẩu
            user.setPassword(encodedPassword);
        }

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Trả về phản hồi sử dụng mapper
        return userMapper.toUserResponse(user);
    }


    @PreAuthorize("hasRole('ADMIN')") // ktra admin trc khi vaof method
    //@PostAuthorize("hasRole('ADMIN')") //goi method r moi kiem tra admin
    /// sẽ k hoạt động vì hasRole chỉ hd với Role k permission nen phải sd hasAuthority @PreAuthorize("hasAuthority('APPROVE_POST')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    //post hoox tro them laays đúng user dang login
    @PostAuthorize("returnObject.phoneNumber==authentication.name")
    @Cacheable(value = "users", key = "#id")
    public User getUserById(int id) {
        log.info("In method get info");
        return userRepository.findById(id).get();
    }

    public UserResponse getMyInfo() throws Exception {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByPhoneNumber(name)
                .orElseThrow(() -> new Exception("Không có user"));

        return UserResponse.fromEntity(user); // Chuyển từ User sang UserResponse
    }

    public UserResponse updateUser(UserCreationRequest request, Long userId) throws ApiException {
        User user = userRepository.findById(Math.toIntExact(userId)).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        user.setFullName(request.getFullName());
        //user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        if (!user.getPhoneNumber().equals(request.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new ApiException("Số điện thoại đã tồn tại trong hệ thống!");
            } else {
                user.setPhoneNumber(request.getPhoneNumber());
            }
        }

        user.setDateOfBirth(request.getDateOfBirth());

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUserPassword(UserCreationRequest request, Long userId) throws Exception {
        User user = userRepository.findById(Math.toIntExact(userId)).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.valueOf("Mật khẩu không chính xác")); // Hoặc lỗi tương tự
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
        }
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public UserResponse createAdminUser(UserCreationRequest form, Long userId) throws Exception {
        String phoneNumber = form.getPhoneNumber();
        // Kiểm tra số điện thoại đã tồn tại chưa
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        // Kiểm tra Role tồn tại không
        Role role = roleRepository.findById(form.getRoleId()).orElseThrow(() ->
                new Exception("Role not found"));
        // Sử dụng MapStruct để ánh xạ DTO sang Entity
        log.debug("UserCreationRequest before mapping: {}", form);
        User user = userMapper.toUser(form);
        log.info("Mapped User: {}", user);
        user.setRole(role);
        // Kiểm tra nếu không có accountId, bắt buộc phải có password
        if (form.getFacebookAccountId() == 0 && form.getGoogleAccountId() == 0) {
            String password = form.getPassword();
            String encodedPassword = passwordEncoder.encode(password); // Mã hóa mật khẩu
            user.setPassword(encodedPassword);
        }
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        // Trả về phản hồi sử dụng mapper
        return userMapper.toUserResponse(user);
    }

    public void updateRoleforUser(int id, int roleId) throws ApiException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ApiException("Role not exists"));
        user.setRole(role);
        userRepository.save(user);

    }

    public void deleteUser(int id) throws ApiException {
        User existingUser = userRepository.findById(id).orElseThrow(
                () -> new ApiException("Dont find User with id:" + id));
        userRepository.deleteById(id);
    }

    public UserResponse updateUserByAdmin(UserCreationRequest request, int id) throws ApiException {
        User user = userRepository.findById(Math.toIntExact(id)).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        user.setFullName(request.getFullName());
        //user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        if (!user.getPhoneNumber().equals(request.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new ApiException("Số điện thoại đã tồn tại trong hệ thống!");
            } else {
                user.setPhoneNumber(request.getPhoneNumber());
            }
        }

        user.setDateOfBirth(request.getDateOfBirth());

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public boolean verifyCaptcha(String token) {

        String url = "https://www.google.com/recaptcha/api/siteverify";


        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secret);
        params.add("response", token);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);
        return (Boolean) response.getBody().get("success");
    }
}
