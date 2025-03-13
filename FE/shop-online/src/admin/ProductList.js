import React, { useState, useEffect } from "react";
import { getProductList } from "../services/productService";
import {
    Table,
    Carousel,
    Button,
    Modal,
    Pagination,
    notification,
} from "antd";
import "../admin/ProductList.scss";
import Search from "antd/es/transfer/search";
import { AntDesignOutlined, AudioOutlined } from "@ant-design/icons";
import AddProductModal from "./AddProductModal"; // Đảm bảo đường dẫn import đúng

const DEFAULT_IMAGE =
    "https://png.pngtree.com/png-clipart/20220113/ourmid/pngtree-transparent-bubble-simple-bubble-png-image_4158141.png";

function ProductList() {
    const [products, setProducts] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [currentPage, setCurrentPage] = useState(1);
    const [loading, setLoading] = useState(true);
    const [editModalVisible, setEditModalVisible] = useState(false);
    const [deleteModalVisible, setDeleteModalVisible] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [addModalVisible, setAddModalVisible] = useState(false);
    const [categories, setCategories] = useState([]);

    const suffix = (
        <AudioOutlined
            style={{
                fontSize: 16,
                color: "#1677ff",
            }}
        />
    );

    const onSearch = (value, _e, info) => console.log(info?.source, value);

    const fetchData = async () => {
        setLoading(true);
        const data = await getProductList(currentPage - 1);
        if (data) {
            setProducts(data.content);
            setTotalPages(data.totalPages);
            const fetchedCategories = [
                { id: 1, name: "SamSung" },
                { id: 2, name: "Apple" },
                { id: 3, name: "Oppo" },
            ];
            setCategories(fetchedCategories);
        }
        setLoading(false);
    };

    useEffect(() => {
        fetchData();
    }, [currentPage]);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const getImageUrl = (product) => {
        if (product.thumbnails && product.thumbnails.length > 0) {
            return product.thumbnails.map(
                (thumbnail) => `http://localhost:8081/uploads/${thumbnail.imageUrl}`
            );
        }
        return [DEFAULT_IMAGE];
    };

    const showEditModal = (product) => {
        setSelectedProduct(product);
        setEditModalVisible(true);
    };

    const showDeleteModal = (product) => {
        setSelectedProduct(product);
        setDeleteModalVisible(true);
    };

    const handleEditCancel = () => {
        setEditModalVisible(false);
    };

    const handleDeleteCancel = () => {
        setDeleteModalVisible(false);
    };

    const handleEditConfirm = () => {
        notification.success({ message: "Sản phẩm đã được chỉnh sửa" });
        setEditModalVisible(false);
    };

    const handleDeleteConfirm = () => {
        notification.success({ message: "Sản phẩm đã được xóa" });
        setDeleteModalVisible(false);
    };

    const handleAddSuccess = () => {
        fetchData();
    };

    const columns = [
        { title: "ID", dataIndex: "id", key: "id" },
        { title: "Tên", dataIndex: "name", key: "name" },
        { title: "Giá (VNĐ)", dataIndex: "price", key: "price" },
        {
            title: "Hình ảnh",
            dataIndex: "thumbnails",
            key: "thumbnails",
            render: (_, product) => (
                <div style={{ width: "100px", height: "100px" }}>
                    <Carousel>
                        {getImageUrl(product).map((imageUrl, index) => (
                            <img
                                key={index}
                                src={imageUrl}
                                alt={product.name}
                                style={{
                                    width: "100%",
                                    height: "100%",
                                    objectFit: "cover",
                                }}
                            />
                        ))}
                    </Carousel>
                </div>
            ),
        },
        {
            title: "Màu sắc",
            dataIndex: "colors",
            key: "colors",
            render: (colors) =>
                colors && colors.length > 0
                    ? colors.map((color) => color.color || color).join(" / ")
                    : "Không có màu",
        },
        {
            title: "Thao tác",
            key: "action",
            render: (_, product) => (
                <>
                    <Button type="primary" onClick={() => showEditModal(product)}>
                        Chỉnh sửa
                    </Button>
                    <Button type="danger" onClick={() => showDeleteModal(product)}>
                        Xóa
                    </Button>
                </>
            ),
        },
    ];

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <div className="productList">
            <div className="headerPro">
                <Button
                    style={{ width: "150px" }}
                    type="primary"
                    size="large"
                    icon={<AntDesignOutlined />}
                    onClick={() => setAddModalVisible(true)}
                >
                    Thêm mới
                </Button>
                <div style={{ marginLeft: "auto", maxWidth: "300px" }}>
                    <Search
                        placeholder="Tìm kiếm sản phẩm"
                        allowClear
                        enterButton="Tìm kiếm"
                        size="large"
                        onSearch={onSearch}
                    />
                </div>
            </div>

            <h2>Quản lý Sản phẩm</h2>
            <Table
                dataSource={products}
                columns={columns}
                pagination={false}
                rowKey="id"
            />

            <Pagination
                current={currentPage}
                total={totalPages * 10}
                onChange={handlePageChange}
                style={{ marginTop: "20px", textAlign: "center" }}
            />
            <Modal
                title="Chỉnh sửa sản phẩm"
                visible={editModalVisible}
                onOk={handleEditConfirm}
                onCancel={handleEditCancel}
            >
            </Modal>

            <Modal
                title="Xóa sản phẩm"
                visible={deleteModalVisible}
                onOk={handleDeleteConfirm}
                onCancel={handleDeleteCancel}
            >
                <p>Bạn có chắc chắn muốn xóa sản phẩm này?</p>
            </Modal>

            <AddProductModal
                visible={addModalVisible}
                onCancel={() => setAddModalVisible(false)}
                onSuccess={handleAddSuccess}
                categories={categories}
            />
        </div>
    );
}

export default ProductList;