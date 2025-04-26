package com.dung.UniStore.controller;


import com.dung.UniStore.dto.response.TotalInventoryResponse;
import com.dung.UniStore.service.TotalInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/total-inventory")
@RequiredArgsConstructor
public class TotalInventoryController {
    private final TotalInventoryService totalInventoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<TotalInventoryResponse> getTotalInventoryByProductId(@PathVariable Integer productId) {
        return ResponseEntity.ok(totalInventoryService.getTotalInventoryByProductId(productId));
    }

}