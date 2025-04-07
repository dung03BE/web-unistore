package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.OrderCreationRequest;
import com.dung.UniStore.dto.response.OrderResponse;
import com.dung.UniStore.form.OrderFilterForm;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IOrderService {
    List<OrderResponse> getAllOrders(OrderFilterForm form);

    OrderResponse createOrder(OrderCreationRequest request) throws Exception;

   

    List<OrderResponse> getOrderByUserId(int userId);

    OrderResponse updateStatusOrder(int id, OrderCreationRequest request);

    String checkoutVnpay(OrderCreationRequest request,Long userId, HttpServletRequest httpRequest) throws Exception;

    OrderResponse checkout(OrderCreationRequest request) throws Exception;
}
