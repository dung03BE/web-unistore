import CustomerManagement from "../admin/CustomerManagement";
import Dashboard from "../admin/Dashboard/Dashboard";

import LayoutAdmin from "../admin/LayoutAdmin";
import OrderManageMent from "../admin/OrderManagement";
import ProductCategories from "../admin/ProductCategories";
import ProductList from "../admin/ProductList";
import RecycleManagement from "../admin/RecycleManagement";
import StaffManagement from "../admin/StaffManagement";
import SystemSettings from "../admin/SystemSettings";
import VoucherManagement from "../admin/VoucherManagement";
import Announce from "../components/Announce/Announce";
import GreenPhone from "../components/GreenPhone/GreenPhone";
import Login from "../components/Login";
import Payment from "../components/Payment/Payment";
import ProductDetails from "../components/product/ProductDetail ";
import Profile from "../components/Profile/Profile";
import Register from "../components/Profile/Register";
import LayoutDefault from "../layout/LayoutDefault";
import Unauthorized from "../layout/LayoutDefault/Unauthorized";
import Cart from "../pages/Cart";
import Home from "../pages/Home";
import PromotionList from "../services/PromotionList";
import ProtectedRoute from "./ProtectedRoute";

export const adminRoutes = {
    path: "/admin",
    element: <ProtectedRoute allowedRoles={[1, 3]} />, // Chỉ cho phép admin vào
    children: [
        {
            path: "",
            element: <LayoutAdmin />,
            children: [
                {
                    path: "dashboard",
                    element: <Dashboard />,
                },
                {
                    path: "products/list",
                    element: <ProductList />,
                },
                {
                    path: "products/categories",
                    element: <ProductCategories />,
                },
                {
                    path: "orders",
                    element: <OrderManageMent />,
                },
                {
                    path: "customers",
                    element: <CustomerManagement />,
                },
                {
                    path: "staff",
                    element: <StaffManagement />,
                },
                {
                    path: "recycle",
                    element: <RecycleManagement />,
                },
                {
                    path: "promotions/list",
                    element: <PromotionList />,
                },
                {
                    path: "promotions/vouchers",
                    element: <VoucherManagement />,
                },
                {
                    path: "settings",
                    element: <SystemSettings />,
                },
                {
                    // Mặc định chuyển hướng đến dashboard
                    path: "",
                    element: <Dashboard />,
                },

            ]
        },

    ],
};
export const router = [
    {
        path: "/",
        element: <LayoutDefault />,
        children: [
            {
                path: "/",
                element: <Home />
            },
            {
                path: "cart",
                element: <Cart />
            },
            {
                path: "payment",
                element: <Payment />
            },
            {
                path: "product/:id", // Định tuyến cho trang chi tiết sản phẩm
                element: <ProductDetails />
            },
            {
                path: "/profile", // Định nghĩa /profile ở cấp độ root
                element: <Profile />
            },
            {
                path: "/register", // Thêm định tuyến cho register
                element: <Register />
            },
            {
                path: "/recycling", // Thêm định tuyến cho register
                element: <GreenPhone />
            },
            {
                path: "/announce", // Thêm định tuyến cho register
                element: <Announce />
            }
        ]
    },
    {
        path: "/login", // Thêm định tuyến cho Login
        element: <Login />
    },
    adminRoutes,
    {
        // Mặc định chuyển hướng đến dashboard
        path: "/unauthorized",
        element: <Unauthorized />,
    },
];
