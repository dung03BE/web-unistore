package com.dung.UniStore.controller;



import com.dung.UniStore.dto.request.UserCreationRequest;
import com.dung.UniStore.dto.response.ApiResponse;
import com.dung.UniStore.dto.response.UserResponse;
import com.dung.UniStore.entity.User;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.service.UserService;
import com.dung.UniStore.utils.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final AuthUtil authUtil;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        List<UserResponse> users = userService.getAllUsers();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return users;
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable int id) {
        User users =userService.getUserById(id);
        return users;
    }

    @GetMapping("/myInfo")
    public UserResponse getMyInfo() throws Exception {
        return userService.getMyInfo(); // Trả về DTO từ Service
    }


    @PostMapping("/register")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) throws Exception {
        String recaptchaToken = request.getRecaptcha();

        boolean captchaVerified = userService.verifyCaptcha(recaptchaToken);
        if (!captchaVerified) {
            return ApiResponse.<UserResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Captcha không hợp lệ")
                    .build();
        }
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @PutMapping("")
    ApiResponse<UserResponse> updateUser(@RequestBody @Valid UserCreationRequest request) throws Exception {
        Long userId = authUtil.loggedInUserId();
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(request, userId))
                .build();
    }
    @PutMapping("/updateByAdmin/{id}")
    ApiResponse<UserResponse> updateUserByAdmin(@RequestBody @Valid UserCreationRequest request,@PathVariable int id) throws Exception {

        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserByAdmin(request,id))
                .build();
    }
    @PutMapping("/changePW")
    ApiResponse<UserResponse> updateUserPassword(@RequestBody @Valid UserCreationRequest request ) throws Exception {
        Long userId = authUtil.loggedInUserId();
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserPassword(request, userId))
                .build();
    }

    @PostMapping("/admin/users")
    public ApiResponse<UserResponse> createAdminUser(@RequestBody UserCreationRequest request, Authentication authentication) throws Exception {
        Long userId = authUtil.loggedInUserId();

        // Kiểm tra quyền admin
        if (!isAdmin(authentication)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        // ... (logic tạo user với role được chọn)
        return ApiResponse.<UserResponse>builder()
                .result(userService.createAdminUser(request, userId))
                .build();

}
    @DeleteMapping("{id}")
    ResponseEntity<UserResponse> deleteUser(@PathVariable int id) throws Exception {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    private boolean isAdmin(Authentication authentication) {
        // Kiểm tra xem người dùng có role admin hay không
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
    @PutMapping("/id/{id}/{roleId}")
    ResponseEntity<UserResponse> updateRoleforUser(@PathVariable int id,@PathVariable int roleId) throws Exception {
        userService.updateRoleforUser(id,roleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
