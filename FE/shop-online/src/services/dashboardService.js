import { getAuth } from "../utils/request";

export const getOverview = async () => {
    const result = await getAuth(`dashboard/overview`);
    return result;
};
export const getMonthlyRevenue = async () => {
    const result = await getAuth(`dashboard/monthly-revenue`);
    return result;
};
export const getTopProductType = async () => {
    const result = await getAuth(`dashboard/top-product-types`);
    return result;
};
export const getTopSellingProducts = async () => {
    const result = await getAuth(`dashboard/top-selling-products`);
    return result;
};
