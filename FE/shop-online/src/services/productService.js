import { del, get, path, post } from "../utils/request";

export const getProductList = async (page = 0, size = 8, minPrice, maxPrice, search) => {
    let url = `products?page=${page}&size=${size}`;

    if (minPrice !== undefined && minPrice !== null) {
        url += `&minPrice=${minPrice}`;
    }

    if (maxPrice !== undefined && maxPrice !== null) {
        url += `&maxPrice=${maxPrice}`;
    }

    if (search && search.trim() !== "") {
        url += `&search=${encodeURIComponent(search)}`;
    }

    const result = await get(url);
    return result.result;
};

export const getProductById = async (id) => {
    const result = await get(`products/${id}`); // API để lấy chi tiết sản phẩm theo ID
    console.log("He", result.result);
    return result.result;
};

export const getAdList = async () => {
    // Mô phỏng gọi API trả về danh sách quảng cáo
    return [
        "https://cdnv2.tgdd.vn/mwg-static/tgdd/Banner/22/6f/226f42d420e8421f838fd1fd229ecd5a.png",
        "https://cdnv2.tgdd.vn/mwg-static/tgdd/Banner/0c/05/0c058f2a093564f1f324362c4626506d.png",
        "https://cdnv2.tgdd.vn/mwg-static/tgdd/Banner/16/b3/16b33991e1de9fb816b988ea2dde16d9.png"
    ];
};

/* nếu có api 
import axios from "axios";

export const getAdList = async () => {
    const response = await axios.get("https://your-api.com/ads"); // URL API thật
    return response.data;
};
*/