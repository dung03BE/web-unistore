import { postAuth } from "../utils/request";

export const payMent = async (orderData) => {
    const path = `orders/checkout`;
    try {
        const result = await postAuth(path, orderData);
        return result;
    } catch (error) {
        console.error("Lỗi khi thanh toán:", error);
        throw error;
    }
};