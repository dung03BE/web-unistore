import ReactDOM from 'react-dom'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
    faCircleNodes,
    // Thêm các icons khác nếu cần
} from '@fortawesome/free-solid-svg-icons';

import { useEffect, useState } from "react";
import { getAdList, getProductList } from "../../services/productService";
import React from 'react';
import { Pagination } from 'antd';
import "./product.scss";


import ProductItem from "./ProductItem";
import ProductCarousel from "./ProductCarousel";
function Product() {
    const [product, setProduct] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [adImages, setAdImages] = useState([]);
    const [totalItems, setTotalItems] = useState(0);
    const pageSize = 8; // Số lượng sản phẩm mỗi trang
    const [carouselItems, setCarouselItems] = useState([]);
    const [loading, setLoading] = useState(false);

    // Lấy danh sách quảng cáo
    useEffect(() => {
        const fetchAds = async () => {
            const ads = await getAdList(); // API mock
            setAdImages(ads);
        };
        fetchAds();
    }, []);

    // Lấy danh sách sản phẩm
    useEffect(() => {
        const fetchApi = async () => {
            const result = await getProductList(currentPage - 1, pageSize); // Trừ 1 để phù hợp với API
            setProduct(result.content);
            setTotalItems(result.totalElements); // Tổng số sản phẩm
        };
        fetchApi();
    }, [currentPage]);

    // Hàm xử lý khi thay đổi trang
    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    return (
        <>
            {/* slider */}
            <ProductCarousel images={adImages} />
            {/* filter */}
            <div className="filter-group">
                <button className="filter-button">
                    <i className="icon-filter"></i>
                    Lọc
                </button>
                <button className="filter-option">
                    <i className="icon-filter2"></i>
                    Samsung
                </button>
                <button className="filter-option">
                    <i className="icon-filter3"></i>
                    iPhone
                </button>
                <button className="filter-option">
                    <i className="icon-filter4"></i>
                    Oppo
                </button>
            </div>
            {/* load sanpham */}
            {loading ? (
                <p>Đang tải...</p>
            ) : (
                <div className="product">
                    {product.length > 0 ? (
                        product.map(item => (
                            <ProductItem item={item} key={item.id} />
                        ))
                    ) : (
                        <p>Không có sản phẩm nào</p>
                    )}
                </div>
            )}
            <Pagination
                current={currentPage}
                pageSize={pageSize}
                total={totalItems}
                onChange={handlePageChange}
            />
        </>
    );
}

export default Product;
