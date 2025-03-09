import { useEffect, useState } from "react";
import { getAdList, getProductList } from "../../services/productService";
import React from 'react';
import { Pagination } from 'antd';
import "./product.scss";
import ProductItem from "./ProductItem";
import ProductCarousel from "./ProductCarousel";
import { useSearchParams } from 'react-router-dom';

function Product({ navigate }) {
    const [product, setProduct] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [adImages, setAdImages] = useState([]);
    const [totalItems, setTotalItems] = useState(0);
    const pageSize = 8;
    const [loading, setLoading] = useState(false);
    const [searchParams] = useSearchParams();
    const search = searchParams.get('search') || '';
    const [minPriceFilter, setMinPriceFilter] = useState(''); // State cho min price
    const [maxPriceFilter, setMaxPriceFilter] = useState('');
    const minPriceParam = searchParams.get('minPrice');
    const maxPriceParam = searchParams.get('maxPrice');

    // Chuyển đổi minPrice và maxPrice sang số nếu có
    const minPrice = minPriceParam ? parseFloat(minPriceParam) : undefined;
    const maxPrice = maxPriceParam ? parseFloat(maxPriceParam) : undefined;

    useEffect(() => {
        const fetchAds = async () => {
            const ads = await getAdList();
            setAdImages(ads);
        };
        fetchAds();
    }, []);

    useEffect(() => {
        const fetchApi = async () => {
            setLoading(true);

            // Gọi getProductList với các tham số lọc (nếu có)
            const result = await getProductList(currentPage - 1, pageSize, minPrice, maxPrice, search);

            setProduct(result.content);
            setTotalItems(result.totalElements);
            setLoading(false);
        };
        fetchApi();
    }, [currentPage, search, minPrice, maxPrice]); // Thêm minPrice và maxPrice vào dependency array

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };
    const handlePriceFilterChange = (e) => {
        if (e.key === 'Enter') {
            const newParams = new URLSearchParams(searchParams);
            if (minPriceFilter) {
                newParams.set('minPrice', minPriceFilter);
            } else {
                newParams.delete('minPrice');
            }
            if (maxPriceFilter) {
                newParams.set('maxPrice', maxPriceFilter);
            } else {
                newParams.delete('maxPrice');
            }
            navigate(`/?${newParams.toString()}`); // Chuyển hướng đến URL mới
        }
    };
    return (
        <>
            <ProductCarousel images={adImages} />
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
                <div className="searchPrice">
                    <input
                        type="number"
                        placeholder="Giá min"
                        value={minPriceFilter}
                        onChange={(e) => setMinPriceFilter(e.target.value)}
                        onKeyDown={handlePriceFilterChange} // Xử lý sự kiện onKeyDown
                    />
                    <input
                        type="number"
                        placeholder="Giá max"
                        value={maxPriceFilter}
                        onChange={(e) => setMaxPriceFilter(e.target.value)}
                        onKeyDown={handlePriceFilterChange} // Xử lý sự kiện onKeyDown
                    />
                </div>
            </div>
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