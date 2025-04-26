package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.ProductCreationRequest;
import com.dung.UniStore.dto.request.ProductUpdateRequest;
import com.dung.UniStore.dto.response.ProductColorResponse;
import com.dung.UniStore.dto.response.ProductDetailsResponse;
import com.dung.UniStore.dto.response.ProductImageResponse;
import com.dung.UniStore.dto.response.ProductResponse;
import com.dung.UniStore.entity.*;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.form.ProductFilterForm;
import com.dung.UniStore.mapper.ProductMapper;
import com.dung.UniStore.repository.*;
import com.dung.UniStore.specification.ProductSpecification;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService implements IProductService {
    final IProductRepository productRepository;
    final IProductDetailsRepository productDetailsRepository;
    final ProductMapper productMapper;
    final ICategoryRepository categoryRepository;
    final IProductImageRepository productImageRepository;
//    final InventoryRepository inventoryRepository;
    final IProductColorRepo productColorRepo;
    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable, ProductFilterForm form) {
        Specification<Product> where = ProductSpecification.builtWhere(form);

        // Lấy danh sách `Product` từ repository theo điều kiện lọc
        Page<Product> productPage = productRepository.findAll(where, pageable);

        // Ánh xạ từng `Product` sang `ProductResponse` và bao gồm `ProductImageResponse` nếu có
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(product -> {
                    ProductDetails productDetails = productDetailsRepository.findByProductId(product.getId());
                    ProductDetailsResponse productDetailsResponse = productMapper.toProductDetailsResponse(productDetails);
                    ProductResponse productResponse = productMapper.toProductResponse(product);
                    productResponse.setDetails(productDetailsResponse);
                    // Gắn danh sách `ProductImageResponse` vào `ProductResponse` nếu tồn tại
                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        productResponse.setThumbnails(productMapper.toProductImageResponses(product.getImages()));
                    }

                    return productResponse;
                })
                .collect(Collectors.toList());

        // Tạo một đối tượng `Page<ProductResponse>` mới từ danh sách `ProductResponse`
        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }

    @Override
//    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(int id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );
        System.out.println("Product:" + product);
        ProductResponse productResponse = productMapper.toProductResponse(product);
        ProductDetails productDetails = productDetailsRepository.findByProductId(id);
        ProductDetailsResponse productDetailsResponse = productMapper.toProductDetailsResponse(productDetails);
        productResponse.setDetails(productDetailsResponse);
        // Lấy `quantity` từ InventoryItem và gán vào ProductResponse
//        InventoryItem inventoryItem = inventoryRepository.findByProductId(id);
//        int quantity = (inventoryItem != null) ? inventoryItem.getQuantity() : 0;
//        productResponse.setQuantity(quantity);

        return productResponse;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Override
    @Transactional
    @CachePut(value = "products", key = "#id")
    public ProductResponse updateProduct(int id, ProductUpdateRequest request) {
        // Kiểm tra sản phẩm tồn tại
        Product existingProduct = productRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        // Kiểm tra danh mục tồn tại
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)
        );

        try {
            // 1. Xóa tất cả các màu hiện có của sản phẩm trước
            if (request.getColors() != null) {
                productColorRepo.deleteByProduct_Id(id);
            }

            // 2. Cập nhật thông tin cơ bản của sản phẩm
            productMapper.updateProduct(existingProduct, request);
            existingProduct.setCategory(category);

            // 3. Lưu sản phẩm để cập nhật thông tin cơ bản
            productRepository.save(existingProduct);

            // 4. Thêm các màu mới sau khi đã lưu sản phẩm
            if (request.getColors() != null && !request.getColors().isEmpty()) {
                for (String color : request.getColors()) {
                    // Tạo và lưu từng màu riêng biệt
                    ProductColor productColor = new ProductColor();
                    productColor.setColor(color);
                    productColor.setProduct(existingProduct);
                    productColorRepo.save(productColor);
                }
            }

            // 5. Cập nhật chi tiết sản phẩm
            if (request.getDetails() != null) {
                // Tìm chi tiết sản phẩm hiện có hoặc tạo mới nếu chưa có
                ProductDetails existingDetails = productDetailsRepository.findByProductId(id);
                if (existingDetails != null) {
                    // Cập nhật thông tin chi tiết hiện có
                    productMapper.updateProductDetails(existingDetails, request.getDetails());
                    productDetailsRepository.save(existingDetails);
                } else {
                    // Tạo mới chi tiết sản phẩm nếu chưa có
                    ProductDetails details = productMapper.toProductDetails(request.getDetails());
                    details.setProduct(existingProduct);
                    productDetailsRepository.save(details);
                }
            }

            // 6. Tải lại thông tin sản phẩm đã cập nhật
            Product refreshedProduct = productRepository.findById(id).orElseThrow(
                    () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
            );

            return productMapper.toProductResponse(refreshedProduct);
        } catch (Exception e) {
            // Log lỗi và ném lại ngoại lệ
            System.err.println("Error updating product: " + e.getMessage());
            throw e;
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(int id) {
        Product existingProduct = productRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );
        productRepository.delete(existingProduct);
    }

    @Override
    public ProductResponse getProductByName(String name) {
        Product existingProduct = productRepository.findByName(name).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );
        return productMapper.toProductResponse(existingProduct);
    }

    //    @Override
//    public ProductResponse createProduct(ProductCreationRequest request) {
//        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
//                ()->new AppException(ErrorCode.CATEGORY_NOT_EXISTED)
//        );
//        Product product= productMapper.toProduct(request);
//
//
//        productRepository.save(product);
//        return productMapper.toProductResponse(product);
//    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPLOYEE','CREATE_DATA')")
    @Override
    public ProductResponse createProduct(ProductCreationRequest request) {
        // Kiểm tra danh mục tồn tại
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Tạo đối tượng Product từ request
        Product product = productMapper.toProduct(request);
        product.setCategory(category);
        product.setColors(new ArrayList<>());
        // Lưu sản phẩm vào database
        product = productRepository.saveAndFlush(product);
        // Lưu danh sách hình ảnh sản phẩm
//        if (request.getImageUrls() != null) {
//            Product finalProduct = product;
//            List<ProductImage> images = request.getImageUrls().stream()
//                    .map(url -> new ProductImage(finalProduct, url))
//                    .collect(Collectors.toList());
//            productImageRepository.saveAll(images);
//        }
        // Lưu danh sách màu sắc sản phẩm
        if (request.getColors() != null) {
            List<ProductColor> colors = new ArrayList<>();
            for (String color : request.getColors()) {
                ProductColor productColor = new ProductColor();
                productColor.setColor(color);
                productColor.setProduct(product);  // Đảm bảo reference đến product
                colors.add(productColor);
            }
            productColorRepo.saveAll(colors);
        }
        // Lưu chi tiết sản phẩm
        if (request.getDetails() != null) {
            ProductDetails details = productMapper.toProductDetails(request.getDetails());
            details.setProduct(product);
            productDetailsRepository.save(details);
        }

        // Chuyển đổi sản phẩm sang ProductResponse và trả về
        return productMapper.toProductResponse(product);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPLOYEE','CREATE_DATA')")
    @Override
    public ProductImage createProductImage(int id, ProductImageResponse build) throws Exception {
        Product existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));


        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(build.getImageUrl())
                .build();
        //k cho insert qua 5 anh
        int size = productImageRepository.findByProductId(id).size();
        if (size >= ProductImage.Maximum_Images_Per_Product) {
            throw new Exception("Number of images must be <=" +
                    ProductImage.Maximum_Images_Per_Product);
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<ProductResponse> getAll() {
        List<Product> productList = productRepository.findAll();
        List<ProductResponse> productResponses = productList.stream()
                .map(product -> {

                    ProductResponse productResponse = productMapper.toProductResponse(product);
                    // Ánh xạ thumbnails từ ProductImage sang ProductImageResponse
                    List<ProductResponse.ProductImageResponse> imageResponses = productMapper.toProductImageResponses(product.getImages());
                    productResponse.setThumbnails(imageResponses);
                    return productResponse;
                })
                .collect(Collectors.toList());
        return productResponses;
    }

    @Override
    public Page<ProductResponse> getAllProductsByCategory(Pageable pageable,int categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)
        );


        // Lấy danh sách `Product` từ repository theo điều kiện lọc
        Page<Product> productPage = productRepository.findAllByCategoryId(categoryId, pageable);

        // Ánh xạ từng `Product` sang `ProductResponse` và bao gồm `ProductImageResponse` nếu có
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(product -> {
                    ProductDetails productDetails = productDetailsRepository.findByProductId(product.getId());
                    ProductDetailsResponse productDetailsResponse = productMapper.toProductDetailsResponse(productDetails);
                    ProductResponse productResponse = productMapper.toProductResponse(product);
                    productResponse.setDetails(productDetailsResponse);

                    // Gắn danh sách `ProductImageResponse` vào `ProductResponse` nếu tồn tại
                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        productResponse.setThumbnails(productMapper.toProductImageResponses(product.getImages()));
                    }

                    return productResponse;
                })
                .collect(Collectors.toList());

        // Tạo một đối tượng `Page<ProductResponse>` mới từ danh sách `ProductResponse`
        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }

    @Override
    public List<ProductResponse> getProductsByIds(List<Integer> ids) {
        List<Product> products = productRepository.findAllById(ids);

        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }

        List<ProductResponse> productResponses = products.stream().map(product -> {
            ProductResponse productResponse = productMapper.toProductResponse(product);

            // Lấy chi tiết sản phẩm
            ProductDetails productDetails = productDetailsRepository.findByProductId(product.getId());
            ProductDetailsResponse productDetailsResponse = productMapper.toProductDetailsResponse(productDetails);
            productResponse.setDetails(productDetailsResponse);

//            // Lấy số lượng tồn kho
//            InventoryItem inventoryItem = inventoryRepository.findByProductId(product.getId());
//            int quantity = (inventoryItem != null) ? inventoryItem.getQuantity() : 0;
//            productResponse.setQuantity(quantity);

            return productResponse;
        }).collect(Collectors.toList());

        return productResponses;
    }

}
