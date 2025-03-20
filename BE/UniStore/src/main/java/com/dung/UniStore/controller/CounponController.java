package com.dung.UniStore.controller;

import com.dung.UniStore.entity.Counpons;
import com.dung.UniStore.entity.User;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.service.CounponService;
import com.dung.UniStore.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/counpon")
@RequiredArgsConstructor
public class CounponController {
    private final AuthUtil authUtil;
    private final CounponService counponService;
    @PostMapping
    public ResponseEntity<?> addCounpon() throws ApiException {
        Long userId = authUtil.loggedInUserId();
        Counpons newCoupon = counponService.addCoupon(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCoupon);
    }
}
