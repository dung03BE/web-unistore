package com.dung.UniStore.controller;

import com.dung.UniStore.dto.request.InventoryCreationRequest;
import com.dung.UniStore.dto.response.InventoryResponse;
import com.dung.UniStore.entity.Inventory;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.service.InventoryyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/inventory")
@RequiredArgsConstructor
public class InventoryyController {

    private final InventoryyService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody InventoryCreationRequest request) throws ApiException {
        Inventory inventory = inventoryService.createInventory(request);
        InventoryResponse response = new InventoryResponse(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getName(), // Thêm tên sản phẩm
                inventory.getProductColor().getId(),
                inventory.getProductColor().getColor(), // Thêm màu
                inventory.getQuantity()
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponse>> getInventoriesByProductId(@PathVariable Integer productId) {
        return ResponseEntity.ok(inventoryService.getInventoriesByProductId(productId));
    }
    @PutMapping("{id}")
    public ResponseEntity<InventoryResponse> updateInventory(@PathVariable int id, @RequestBody InventoryCreationRequest request) throws ApiException {
        Inventory inventory = inventoryService.updateInventory(id,request);
        InventoryResponse response = new InventoryResponse(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getName(), // Thêm tên sản phẩm
                inventory.getProductColor().getId(),
                inventory.getProductColor().getColor(), // Thêm màu
                inventory.getQuantity()
        );
        return ResponseEntity.ok(response);
    }
}