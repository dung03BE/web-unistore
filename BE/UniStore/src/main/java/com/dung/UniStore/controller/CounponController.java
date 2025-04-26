package com.dung.UniStore.controller;

import com.dung.UniStore.dto.request.CouponRequest;
import com.dung.UniStore.dto.response.CounponResponse;
import com.dung.UniStore.entity.Counpons;
import com.dung.UniStore.entity.User;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.service.CounponService;
import com.dung.UniStore.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/coupon")
@RequiredArgsConstructor
public class CounponController {
    private final AuthUtil authUtil;
    private final CounponService counponService;

    //chưa ddc sài , sài để tạo các counpon riêng thui
    @PostMapping
    public ResponseEntity<?> addCounpon() throws ApiException {
        Long userId = authUtil.loggedInUserId();
        Counpons newCoupon = counponService.addCoupon(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCoupon);
    }
    @PostMapping("/addNew")
    public ResponseEntity<Counpons> createCoupon(@RequestBody CouponRequest requestDTO) {
        Counpons createdCoupon = counponService.createCoupon(
                requestDTO.getCode(),
                requestDTO.getDiscountType(),
                requestDTO.getDiscountValue(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                requestDTO.getStatus(),
                requestDTO.getUserLimit(),
                requestDTO.getUsageLimit(),
                requestDTO.getUserId()
        );

        return new ResponseEntity<>(createdCoupon, HttpStatus.CREATED);
    }
    @GetMapping("/code/{code}")
    public ResponseEntity<CounponResponse> getCouponByCode(@PathVariable String code) throws ApiException {
        Long userId = authUtil.loggedInUserId();
        CounponResponse coupon = counponService.getCouponByCodeAndUser(code,userId);
        return ResponseEntity.ok(coupon);
    }

    @GetMapping()
    public ResponseEntity<List<CounponResponse>> getCoupons() throws ApiException {

        List<CounponResponse> coupons = counponService.getCoupons();
        return ResponseEntity.ok(coupons);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<CounponResponse> deleteCoupon(@PathVariable Long id) throws ApiException {

        CounponResponse counponResponse = counponService.deleteCoupon(id);
        return ResponseEntity.ok(counponResponse);
    }
}
