import Login from "../components/Login";
import Payment from "../components/Payment/Payment";
import ProductDetails from "../components/product/ProductDetail ";
import Profile from "../components/Profile/Profile";
import LayoutDefault from "../layout/LayoutDefault";
import Cart from "../pages/Cart";
import Home from "../pages/Home";

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
                path: "/profile", // Thêm định tuyến cho Login
                element: <Profile />
            }
        ]
    },
    {
        path: "/login", // Thêm định tuyến cho Login
        element: <Login />
    },

];
