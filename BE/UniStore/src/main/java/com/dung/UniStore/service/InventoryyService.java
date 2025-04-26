package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.InventoryCreationRequest;
import com.dung.UniStore.dto.response.InventoryResponse;
import com.dung.UniStore.entity.Inventory;
import com.dung.UniStore.entity.Product;
import com.dung.UniStore.entity.ProductColor;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.repository.IInventoryRepository;
import com.dung.UniStore.repository.IProductColorRepo;
import com.dung.UniStore.repository.IProductRepository;
import com.dung.UniStore.repository.TotalInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryyService {
    private final IProductRepository productRepository;
    private final IProductColorRepo productColorRepo;
    private final IInventoryRepository inventoryRepository;
    private final TotalInventoryService totalInventoryService;
    private final TotalInventoryRepository totalInventoryRepository;
    public Inventory createInventory(InventoryCreationRequest request) throws ApiException {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
// Tạo kho tổng cho sản phẩm nếu chưa có
        totalInventoryService.createTotalInventoryForProduct(product);
        ProductColor color = productColorRepo.findById(request.getProductColorId())
                .orElseThrow(() -> new ApiException("Color not existed"));

        Inventory inventory = Inventory.builder()
                .product(product)
                .productColor(color)
                .quantity(request.getQuantity())
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);

        // Cập nhật lại kho tổng sau khi thêm inventory
        totalInventoryRepository.findByProductId(product.getId()).ifPresent(totalInventory -> {
            totalInventory.setQuantity(totalInventory.getQuantity() + request.getQuantity());
            totalInventoryRepository.save(totalInventory);
        });

        return savedInventory;
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public List<InventoryResponse> getAllInventories() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream().map(this::toInventoryResponse).toList();
    }



    public List<InventoryResponse> getInventoriesByProductId(Integer productId) {
        List<Inventory> inventories = inventoryRepository.findByProductId(productId);
        return inventories.stream().map(this::toInventoryResponse).toList();
    }

    private InventoryResponse toInventoryResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getName(),
                inventory.getProductColor() != null ? inventory.getProductColor().getId() : null,
                inventory.getProductColor() != null ? inventory.getProductColor().getColor() : null,
                inventory.getQuantity()
        );
    }

    public Inventory updateInventory(int id, InventoryCreationRequest request) throws ApiException {
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_ITEM_NOT_EXISTS));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        ProductColor color = productColorRepo.findById(request.getProductColorId())
                .orElseThrow(() -> new ApiException("Color not existed"));

        int oldQuantity = existingInventory.getQuantity(); // lấy số lượng trước khi cập nhật

        existingInventory.setProduct(product);
        existingInventory.setProductColor(color);
        existingInventory.setQuantity(request.getQuantity());

        Inventory inventory = inventoryRepository.save(existingInventory);

        // Cập nhật kho tổng
        totalInventoryRepository.findByProductId(product.getId()).ifPresent(totalInventory -> {
            int delta = request.getQuantity() - oldQuantity;
            totalInventory.setQuantity(totalInventory.getQuantity() + delta);
            totalInventoryRepository.save(totalInventory);
        });

        return inventory;
    }

}
