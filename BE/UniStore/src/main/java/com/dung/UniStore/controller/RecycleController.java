package com.dung.UniStore.controller;

import com.dung.UniStore.dto.request.OrderCreationRequest;
import com.dung.UniStore.dto.request.RecycleRequest;
import com.dung.UniStore.dto.request.RoleRequest;
import com.dung.UniStore.dto.response.ApiResponse;
import com.dung.UniStore.dto.response.OrderResponse;
import com.dung.UniStore.dto.response.RecycleResponse;
import com.dung.UniStore.dto.response.RoleResponse;
//import com.dung.UniStore.entity.InventoryItem;
import com.dung.UniStore.entity.Recycle;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.service.RecycleService;
import com.dung.UniStore.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/recycle")
@RequiredArgsConstructor
public class RecycleController {
    private final RecycleService recycleService;
    private final AuthUtil authUtil;
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER','ROLE_EMPLOYEE')")
    @PostMapping
    ApiResponse<RecycleResponse> create(@RequestBody RecycleRequest request) {
        Integer userId = Math.toIntExact(authUtil.loggedInUserId());
        return ApiResponse.<RecycleResponse>builder()
                .result(recycleService.createRecycleRequest(request,userId))
                .build();
    }
    @GetMapping
    public ApiResponse<List<RecycleResponse>> getAllRecycleRequest()
    {
        return ApiResponse.<List<RecycleResponse>>builder()
                .result(recycleService.getAllRecycleRequest())
                .build();
    }
    @GetMapping("/userId")
    public ApiResponse<List<RecycleResponse>> getAllRecycleRequestByUserId()
    {
        Long userId = authUtil.loggedInUserId();
        return ApiResponse.<List<RecycleResponse>>builder()
                .result(recycleService.getAllRecycleRequest(userId))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<RecycleResponse> updateStatusRecycle(@PathVariable int id,
                                                            @RequestBody RecycleRequest
                                                                request) throws ApiException {
        return ApiResponse.<RecycleResponse>builder()
                .result(recycleService.updateStatusRecycle(id,request))
                .build();
    }

}
