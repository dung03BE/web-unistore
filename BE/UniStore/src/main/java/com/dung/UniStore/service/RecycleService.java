package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.RecycleRequest;
import com.dung.UniStore.dto.response.RecycleResponse;
import com.dung.UniStore.entity.*;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.repository.ICounponRepository;
import com.dung.UniStore.repository.IRecycleRepository;
import com.dung.UniStore.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleService {
    private final IRecycleRepository recycleRepository;
    private final IUserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ICounponRepository counponRepository;
    private final EmailService emailService;
    private final UserAnnouncementService userAnnouncementService;
    public RecycleResponse createRecycleRequest(RecycleRequest request, Integer userId) {
        User user = userRepository.findById(Math.toIntExact(userId)).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
       Recycle recycle = new Recycle();
       recycle.setDeviceType(request.getDeviceType());
       recycle.setDeviceCondition(request.getDeviceCondition());
       recycle.setCreatedAt(LocalDateTime.now());
       recycle.setPickupMethod(request.getPickupMethod());
       recycle.setStatus("Chờ xác nhận");
       recycle.setUser(user);
        recycle.setImageUrl(request.getImageUrl());
       recycleRepository.save(recycle);
       RecycleResponse response = modelMapper.map(recycle, RecycleResponse.class);
       response.setFullName(recycle.getUser().getFullName());
       response.setPhoneNumber(recycle.getUser().getPhoneNumber());
       return response;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public List<RecycleResponse> getAllRecycleRequest() {
        List<Recycle> recycles = recycleRepository.findAll();

        // Sắp xếp theo createdAt giảm dần
        recycles.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

        return recycles.stream().map(recycle -> {
            RecycleResponse response = modelMapper.map(recycle, RecycleResponse.class);
            if (recycle.getUser() != null) {
                response.setFullName(recycle.getUser().getFullName());
                response.setPhoneNumber(recycle.getUser().getPhoneNumber());
            }
            return response;
        }).toList();
    }

    public List<RecycleResponse> getAllRecycleRequest(Long userId) {
        List<Recycle> recycles = recycleRepository.findAllByUserId(userId);

        // Sắp xếp theo createdAt giảm dần
        recycles.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

        Type listType = new TypeToken<List<RecycleResponse>>() {}.getType();
        return modelMapper.map(recycles, listType);
    }



    public RecycleResponse updateStatusRecycle(int id, RecycleRequest request) throws ApiException {
        Recycle recycle = recycleRepository.findById(id).orElseThrow(
                ()-> new ApiException("Dont have recycle request with id: "+id)
        );
        boolean becameCompleted = !recycle.getStatus().equals("Hoàn thành") &&
                request.getStatus().equals("Hoàn thành");
        User user = userRepository.findById(recycle.getUser().getId())
                .orElseThrow(
                        ()-> new ApiException("Kh tồn tại user")
                );
        recycle.setStatus(request.getStatus());
        recycleRepository.save(recycle);
        if (becameCompleted && !recycle.getCouponGenerated()) {
            recycle.setCouponGenerated(true);
            recycle = recycleRepository.save(recycle);
            userAnnouncementService.assignAnnouncementToUser(user.getId(),1);
            generateCoupon((long) recycle.getUser().getId());
        }
        RecycleResponse response = modelMapper.map(recycle, RecycleResponse.class);
        response.setFullName(recycle.getUser().getFullName());
        response.setPhoneNumber(recycle.getUser().getPhoneNumber());
        return response;
    }

    private void generateCoupon(Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId)).orElse(null);
        if (user == null) return;

        boolean hasCoupon = counponRepository.existsByUserAndCode(user, "DISCOUNT10");
        if (!hasCoupon) {
            Counpons coupon = Counpons.builder()
                    .code("DISCOUNT10")
                    .discountType(Counpons.DiscountType.percentage)
                    .discountValue(new BigDecimal("0.10"))
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusMonths(1))
                    .status(Status.active)
                    .user(user)
                    .build();

            counponRepository.save(coupon);
            emailService.sendCouponNotification(user.getEmail(), coupon.getCode());
        }
    }
}
