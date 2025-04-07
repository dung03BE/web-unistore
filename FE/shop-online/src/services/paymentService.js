import { getAuth, postAuth } from "../utils/request";

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
export const payMentVnpay = async (orderData) => {
    const path = `orders/checkoutVnpay`;
    try {
        const result = await postAuth(path, orderData);
        return result;
    } catch (error) {
        console.error("Lỗi khi thanh toán:", error);
        throw error;
    }
};
export const getOrderByUserId = async () => {
    const path = `orders/user`;
    try {
        const result = await getAuth(path);
        return result.result;
    } catch (error) {
        console.error("Lỗi khi get Order", error);
        throw error;
    }
};