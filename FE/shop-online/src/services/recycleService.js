import { postAuth } from "../utils/request";

export const createRecycleRequest = async (data) => {
    const path = `recycle`;
    try {
        const result = await postAuth(path, data);
        return result;
    } catch (error) {
        console.error("Lỗi khi khi tạo mới request:", error);
        throw error;
    }
};