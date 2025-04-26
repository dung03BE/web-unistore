package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.OrderDetailCreationRequest;
import com.dung.UniStore.dto.response.OrderDetailsResponse;
import com.dung.UniStore.dto.response.OrderResponse;
import com.dung.UniStore.exception.ApiException;

import java.math.BigDecimal;

public interface IOrderDetailService {
    BigDecimal createOrderDetails(int id, Long cartId) throws ApiException;
    // OrderDetailsResponse createOrderDetail(OrderDetailCreationRequest request) throws Exception;
}
