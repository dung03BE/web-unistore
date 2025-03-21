package com.dung.UniStore.service;

import com.dung.UniStore.entity.Counpons;
import com.dung.UniStore.entity.Status;
import com.dung.UniStore.exception.ApiException;

import com.dung.UniStore.repository.ICounponRepository;
import com.dung.UniStore.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CounponService {
    private final ICounponRepository counponRepository;
    private final IUserRepository userRepository;
    public Counpons addCoupon(Long userId) throws ApiException {
        Counpons counpons = counponRepository.findByUserId(userId);
        if(counpons==null)
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
}
