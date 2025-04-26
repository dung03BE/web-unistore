package com.dung.UniStore.controller;

import com.dung.UniStore.dto.request.OrderCreationRequest;
import com.dung.UniStore.dto.response.ApiResponse;
import com.dung.UniStore.dto.response.OrderResponse;
import com.dung.UniStore.entity.Order;
import com.dung.UniStore.form.OrderFilterForm;
import com.dung.UniStore.repository.IOrderRepository;
import com.dung.UniStore.service.IOrderService;
import com.dung.UniStore.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;
    private final IOrderRepository orderRepository;
    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders(OrderFilterForm form) {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAllOrders(form))
                .build();
    }

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreationRequest request) throws Exception {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();
    }

    //    @PutMapping("{id}")
//    public ResponseEntity<String> updateOrder(@PathVariable int id,  @RequestBody UpdateOrderForm form) throws DataNotFoundException {
//        form.setId(id);
//        Order existingOrder = orderService.updateOrder(form);
//        return new ResponseEntity<>("Update OK", HttpStatus.OK);
//    }
//    @DeleteMapping("{id}")
//    public ResponseEntity<String> deleteOrder(@PathVariable int id) throws DataNotFoundException {
//        orderService.deleteOrder(id);
//        return new ResponseEntity<>("OK", HttpStatus.OK);
//    }
    @GetMapping("{id}")
    public OrderResponse getOrderById(@PathVariable int id)  {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return modelMapper.map(order, OrderResponse.class);
    }
    @GetMapping("/user")
    public ApiResponse<List<OrderResponse>> getOrderByUserId() {
        Long userId = authUtil.loggedInUserId();
        List<OrderResponse> orders = orderService.getOrderByUserId(Math.toIntExact(userId));
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orders) //
                .build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/checkoutVnpay")
    public ApiResponse<Map<String, String>> checkoutVnpay(@RequestBody OrderCreationRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = authUtil.loggedInUserId();
            String paymentUrl = orderService.checkoutVnpay(request, userId, httpRequest);

            return ApiResponse.<Map<String, String>>builder()
                    .result(Map.of("paymentUrl", paymentUrl))
                    .message("Khởi tạo thanh toán thành công")
                    .code(1000)
                    .build();

        } catch (Exception e) {
            return ApiResponse.<Map<String, String>>builder()
                    .result(null)
                    .message(e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi")
                    .code(400) // hoặc code riêng biệt bạn quy định như 1016, 1017...
                    .build();
        }
    }
    @PostMapping("/checkout")
    public ApiResponse<OrderResponse> checkout(@RequestBody OrderCreationRequest request) throws Exception {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.checkout(request))
                .build();

    }

    @PutMapping("/checkout/{id}")
    public ApiResponse<OrderResponse> updateStatusOrder(@PathVariable int id,
                                                        @RequestBody OrderCreationRequest
                                                                request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateStatusOrder(id,request))
                .build();
    }
}
