package com.dung.UniStore.service;


import com.dung.UniStore.dto.response.TotalInventoryResponse;
import com.dung.UniStore.entity.Product;
import com.dung.UniStore.entity.TotalInventory;
import com.dung.UniStore.repository.TotalInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TotalInventoryService {
    private final TotalInventoryRepository totalInventoryRepository;

    public TotalInventoryResponse getTotalInventoryByProductId(Integer productId) {
        TotalInventory totalInventory = totalInventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy total inventory cho productId: " + productId));

        return new TotalInventoryResponse(
                totalInventory.getProduct().getId(),
                totalInventory.getProduct().getName(),
                totalInventory.getQuantity()
        );
    }
    public void createTotalInventoryForProduct(Product product) {
        // Kiểm tra xem sản phẩm đã có kho tổng chưa
        totalInventoryRepository.findByProductId(product.getId()).ifPresentOrElse(
                totalInventory -> {
                    // Nếu có rồi thì không cần tạo mới
                    System.out.println("Total inventory already exists for product: " + product.getId());
                },
                () -> {
                    // Nếu chưa có kho, tạo kho mới cho sản phẩm
                    TotalInventory newTotalInventory = TotalInventory.builder()
                            .product(product)
                            .quantity(0) // ban đầu quantity là 0
                            .build();
                    totalInventoryRepository.save(newTotalInventory);
                    System.out.println("Total inventory created for product: " + product.getId());
                }
        );
    }

}