package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.OrderDetailCreationRequest;
import com.dung.UniStore.dto.response.OrderDetailsResponse;
import com.dung.UniStore.dto.response.OrderResponse;

import java.math.BigDecimal;

public interface IOrderDetailService {
    BigDecimal createOrderDetails(int id, Long cartId);
    // OrderDetailsResponse createOrderDetail(OrderDetailCreationRequest request) throws Exception;
}
