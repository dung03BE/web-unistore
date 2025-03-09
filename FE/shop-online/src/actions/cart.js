import { getCart } from "../services/cartService";

// export const addToCart = (info) => {
//     const uniqueId = `${info.id}-${info.color}`; // Tạo id duy nhất
//     console.log("🛒 Dispatching ADD_TO_CART:", { uniqueId, info });
//     return {
//         type: "ADD_TO_CART",
//         id: uniqueId, // Sử dụng id duy nhất
//         info: info
//     }
// }
export const updateQuantity = (id, quantity = 1) => {
    return {
        type: "UPDATE_QUANTITY",
        id: id,
        quantity: quantity
    }
}
export const deleteItem = (id) => {
    return {
        type: "DELETE_ITEM",
        id: id,

    }
}
export const deleteAll = () => {
    return {
        type: "DELETE_ALL",

    }
}
// actions/cart.js

export const FETCH_CART_SUCCESS = "FETCH_CART_SUCCESS";

// ... các action khác

export const fetchCartSuccess = (cartData) => ({
    type: FETCH_CART_SUCCESS,
    payload: cartData,
});

export const fetchCart = () => {
    return async (dispatch) => {
        try {
            const cartData = await getCart(); // Gọi API từ cartService
            // Chuyển đổi dữ liệu API về định dạng reducer
            const formattedCartData = cartData.products.map(product => ({
                id: `${product.id}-${product.colors[0].color}`, // Tạo id duy nhất
                info: {
                    id: product.id,
                    name: product.name,
                    price: product.price,
                    image: product.thumbnails[0].imageUrl,
                    color: product.colors[0].color,
                    discountPercentage: product.discount,
                },
                quantity: product.quantity,
            }));
            dispatch(fetchCartSuccess(formattedCartData));
        } catch (error) {
            console.error("Lỗi khi fetch giỏ hàng:", error);
            // Xử lý lỗi nếu cần
        }
    };
};