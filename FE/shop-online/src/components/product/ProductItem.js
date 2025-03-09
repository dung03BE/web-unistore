import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { fetchCartSuccess } from "../../actions/cart";
import "./product.scss";
import { Button, Modal, notification, Space } from "antd";
import { addToCartAPI, getCart } from "../../services/cartService";

function ProductItem(props) {
    const { item } = props;
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [showColorModal, setShowColorModal] = useState(false);
    const [selectedColor, setSelectedColor] = useState(null);
    const API_URL = "http://localhost:8081";
    const DEFAULT_IMAGE = "https://png.pngtree.com/png-clipart/20220113/ourmid/pngtree-transparent-bubble-simple-bubble-png-image_4158141.png";

    const handleAddToCart = (e) => {
        e.stopPropagation(); // Ngăn chặn sự kiện lan truyền
        setShowColorModal(true);
    };

    const handleColorSelect = (color, e) => {
        e.stopPropagation();
        setSelectedColor(color.color);
    };

    console.log(item);
    const handleAddToCartWithColor = async (e) => {
        e.preventDefault(); // Ngăn chặn sự kiện mặc định
        if (selectedColor) {
            try {
                await addToCartAPI(item.id, 1, selectedColor);
                const updatedCart = await getCart();
                dispatch(fetchCartSuccess(updatedCart));

                notification.success({
                    message: 'Thành công',
                    description: 'Sản phẩm đã được thêm vào giỏ hàng.',
                });

                setShowColorModal(false);
                setSelectedColor(null);
            } catch (error) {
                console.error("Lỗi khi thêm sản phẩm vào giỏ hàng:", error);
                notification.error({
                    message: 'Lỗi',
                    description: 'Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng.',
                });
            }
        } else {
            notification.error({
                message: 'Lỗi',
                description: 'Vui lòng chọn màu sản phẩm.',
            });
        }
    };

    const handleViewDetails = () => {
        navigate(`/product/${item.id}`);
    };

    const getImageUrl = () => {
        if (item.thumbnails && item.thumbnails.length > 0) {
            return `${API_URL}/uploads/${item.thumbnails[0].imageUrl}`;
        }
        return DEFAULT_IMAGE;
    };

    return (
        <div className="product__item" onClick={handleViewDetails}>
            <img className="product__image" src={getImageUrl()} alt={item.name} onError={(e) => {
                e.target.onerror = null;
                e.target.src = DEFAULT_IMAGE;
            }} />
            <h3 className="product__title">{item.name}</h3>
            <div className="product__price">{item.price.toLocaleString()}₫</div>
            <div className="product__description">{item.description}</div>
            <div className="product__buttons">
                <button onClick={handleAddToCart}>Thêm vào giỏ 🛒</button>
                <button onClick={handleViewDetails}>Chi tiết</button>
            </div>
            <Modal title="Chọn màu sắc" visible={showColorModal} onOk={handleAddToCartWithColor} onCancel={() => {
                setShowColorModal(false);
                setSelectedColor(null);
            }} footer={[
                <Button key="cancel" onClick={() => {
                    setShowColorModal(false);
                    setSelectedColor(null);
                }}>Hủy</Button>,
                <Button key="confirm" type="primary" onClick={handleAddToCartWithColor}>Thêm vào giỏ</Button>,
            ]}>
                <Space size="middle">
                    {item.colors && item.colors.map((color) => (
                        <Button key={color.id} onClick={(e) => handleColorSelect(color, e)} style={{
                            backgroundColor: selectedColor && selectedColor.id === color.id ? 'lightblue' : 'white',
                        }}>{color.color}</Button>
                    ))}
                </Space>
            </Modal>
        </div>
    );
}

export default ProductItem;