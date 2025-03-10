import { post, putAuth } from "../utils/request";

export const putUserApi = async (userData) => {
    const path = `users`;
    try {
        const result = await putAuth(path, userData);
        console.log("H1:", result);
        return result.result;
    } catch (error) {
        console.error("Lỗi khi update User", error);
        throw error; // Re-throw lỗi để component có thể xử lý
    }
};
export const postUserApi = async (userData) => {
    const path = `users/register`;
    try {
        const result = await post(path, userData);
        return result;
    } catch (error) {
        console.error("Lỗi khi create User", error);
        throw error; // Re-throw lỗi để component có thể xử lý
    }
};
export const changePasswordApi = async (userData) => {
    const path = `users/changePW`;
    try {
        const result = await putAuth(path, userData);
        return result;
    } catch (error) {
        console.error("Lỗi khi thay đổi password", error);
        if (error.response && error.response.data) {
            throw {
                status: error.response.status,
                data: error.response.data,
            };
        } else {
            throw error;
        }
    }
};