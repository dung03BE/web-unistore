package com.dung.UniStore.controller;

import com.dung.UniStore.dto.request.OrderCreationRequest;
import com.dung.UniStore.dto.response.ApiResponse;
import com.dung.UniStore.dto.response.OrderResponse;
import com.dung.UniStore.form.OrderFilterForm;
import com.dung.UniStore.service.IOrderService;
import com.dung.UniStore.utils.AuthUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final AuthUtil authUtil;

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
//    @GetMapping("{id}")
//    public OrderDTO getOrderById(@PathVariable int id) throws DataNotFoundException {
//        return modelMapper.map(orderService.getOrderById(id),OrderDTO.class);
//    }
    @GetMapping("/user")
    public ApiResponse<List<OrderResponse>> getOrderByUserId() {
        Long userId = authUtil.loggedInUserId();
        List<OrderResponse> orders = orderService.getOrderByUserId(Math.toIntExact(userId));
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orders) //
                .build();
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
