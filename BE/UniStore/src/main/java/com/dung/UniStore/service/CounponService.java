package com.dung.UniStore.service;

import com.dung.UniStore.dto.response.CounponResponse;
import com.dung.UniStore.entity.Counpons;
import com.dung.UniStore.entity.Status;
import com.dung.UniStore.entity.User;
import com.dung.UniStore.exception.ApiException;

import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.repository.CouponUserUsageRepository;
import com.dung.UniStore.repository.ICounponRepository;
import com.dung.UniStore.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounponService {
    private final ICounponRepository counponRepository;
    private final IUserRepository userRepository;
    private final CouponUserUsageRepository couponUserUsageRepository;
    public Counpons addCoupon(Long userId) throws ApiException {
        Counpons counpons = counponRepository.findByUserId(userId);
        if(counpons!=null)
        {
            throw new ApiException("Người dùng đã có counpon");
        }
        counpons.setCode("RECYCLE30");
        counpons.setDiscountType(Counpons.DiscountType.percentage);
        counpons.setDiscountValue(BigDecimal.valueOf(0.3));
        counpons.setStartDate(LocalDateTime.now());
        counpons.setEndDate(LocalDateTime.now().plusDays(15));
        counpons.setUser(userRepository.findById(Math.toIntExact(userId)).get());
        counpons.setStatus(Status.active);
        return counpons;
    }

    public Counpons createCoupon(String code,
                               String discountType,
                               BigDecimal discountValue,
                               LocalDateTime startDate,
                               LocalDateTime endDate,
                               String status,
                               int userLimit,
                               int usageLimit,
                               Long userId) {


        Counpons coupon = new Counpons();
        coupon.setCode(code);
        coupon.setDiscountType(Counpons.DiscountType.percentage);
        coupon.setDiscountValue(discountValue);
        coupon.setStartDate(startDate);
        coupon.setEndDate(endDate);
        coupon.setStatus(Status.active);
        coupon.setUserLimit(userLimit);
        coupon.setUsageLimit(usageLimit);
        coupon.setUsedCount(0); // ban đầu là 0
        if (userId != null) {
            // Lấy User từ UserRepository nếu userId có giá trị
            Optional<User> user = userRepository.findById(Math.toIntExact(userId));
            user.ifPresent(coupon::setUser);
        } else {
            // Nếu userId là null, không cần thiết lập user
            coupon.setUser(null);  // Coupon có thể là công khai hoặc không thuộc người dùng nào
        }

        return counponRepository.save(coupon);
    }
    public CounponResponse getCouponByCodeAndUser(String code, Long userId) throws ApiException {

        LocalDateTime now = LocalDateTime.now();
        Counpons coupon = counponRepository
                .findValidCouponForUserNative(code, Status.active.name(), now, userId)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_IS_ERROR));
        if (coupon.getUser() == null ) {
            boolean hasUsed = couponUserUsageRepository.existsByCouponIdAndUserId(coupon.getId(),
                    Math.toIntExact(userId));
            if (hasUsed) {
                throw new AppException(ErrorCode.COUPON_IS_USED);
            }
        }
        return mapToResponse(coupon);
    }
    public CounponResponse mapToResponse(Counpons coupon) {
        return CounponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType().name())
                .discountValue(coupon.getDiscountValue())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .status(coupon.getStatus().name())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .userLimit(coupon.getUserLimit())
                .userId(coupon.getUser() != null ? Long.valueOf(coupon.getUser().getId()) : null)
                .build();
    }


    public List<CounponResponse> getCoupons() {
        List<Counpons> coupons = counponRepository.findAll();

        return coupons.stream()
                .sorted(Comparator.comparing(Counpons::getStartDate).reversed()) // Sắp xếp theo startDate mới nhất
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CounponResponse deleteCoupon(Long id) throws ApiException {
        Counpons coupon = counponRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ApiException("Coupon không tồn tại"));
        CounponResponse response = mapToResponse(coupon);

        counponRepository.delete(coupon);

        return response;
    }
}
