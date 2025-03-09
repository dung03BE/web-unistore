import { useDispatch } from "react-redux";
import { deleteItem, updateQuantity } from "../../actions/cart";
import { useEffect, useRef, useState } from "react";
import { putCartApi } from "../../services/cartService";

function CartItem({ item }) {
    const inputRef = useRef();
    const dispatch = useDispatch();
    const [quantity, setQuantity] = useState(item.quantity);
    // Debounce timeout để gọi API
    const debounceRef = useRef(null);
    console.log("item", item);
    // Cập nhật API sau 500ms nếu có thay đổi số lượng
    useEffect(() => {
        if (quantity !== item.quantity) {
            clearTimeout(debounceRef.current);
            debounceRef.current = setTimeout(() => {
                putCartApi(item.info.id, quantity, item.info.color)
                    .then(() => console.log("Đã cập nhật giỏ hàng"))
                    .catch((error) => console.error("Lỗi khi cập nhật giỏ hàng:", error));
            }, 500);
        }
    }, [quantity, item.id, item.info.color]);

    const handleUp = () => {
        setQuantity((prev) => prev + 1);
    };

    const handleDown = () => {
        if (quantity > 1) {
            setQuantity((prev) => prev - 1);
            dispatch(updateQuantity(item.id, quantity - 1));  // Cập nhật Redux store
        }
    };

    const handleDelete = () => {
        dispatch(deleteItem(item.id));
    };

    const imageUrl = `http://localhost:8081/uploads/${item.info.image}`; // Xây dựng URL đầy đủ

    return (
        <div className="cart__item">
            <div className="cart__item-image">
                <img src={imageUrl} alt={item.info.name} />
            </div>
            <div className="cart__item-content">
                <h4 className="cart__item-title">{item.info.name}</h4>
                <div className="cart__item-color">Màu: {item.info.color}</div>
            </div>
            <div className="cart__item-price">
                <div className="cart__item-price-new">
                    {((item.info.price * (100 - (item.info.discountPercentage || 0))) / 100).toFixed(2)}$
                </div>
                <div className="cart__item-price-old">{item.info.price}$</div>
            </div>
            <div className="cart__item-quantity">
                <button onClick={handleDown}>-</button>
                <input ref={inputRef}
                    value={quantity}
                    readOnly />
                <button onClick={handleUp}>+</button>
            </div>
            <button className="cart__item-delete" onClick={handleDelete}>Xóa</button>
        </div>
    );
}

export default CartItem;
