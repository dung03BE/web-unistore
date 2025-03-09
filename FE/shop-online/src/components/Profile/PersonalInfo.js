import { Avatar, Button, DatePicker, Input, Modal, Row, Col } from "antd";
import "../Profile/PersonalInfo.scss";
import { UserOutlined } from "@ant-design/icons";
import { useState, useEffect } from "react"; // Import useEffect
import dayjs from "dayjs";

export const PersonalInfo = ({ userDetails }) => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [dob, setDob] = useState(userDetails?.dateOfBirth ? dayjs(userDetails.dateOfBirth) : null);
    const [fullName, setFullName] = useState(userDetails?.fullName || "");
    const [phoneNumber, setPhoneNumber] = useState(userDetails?.phoneNumber || "");
    const [address, setAddress] = useState(userDetails?.address || "");
    const [email, setEmail] = useState(userDetails?.email || "");

    useEffect(() => {
        if (userDetails) {
            setFullName(userDetails.fullName || "");
            setPhoneNumber(userDetails.phoneNumber || "");
            setAddress(userDetails.address || "");
            setEmail(userDetails.email || "");
            setDob(userDetails.dateOfBirth ? dayjs(userDetails.dateOfBirth) : null);
        }
    }, [userDetails]);

    if (!userDetails) return <div>Loading...</div>;

    const showModal = () => {
        setIsModalOpen(true);
    };

    const handleOk = () => {
        // Xử lý logic cập nhật thông tin người dùng ở đây
        setIsModalOpen(false);
    };

    const handleCancel = () => {
        setIsModalOpen(false);
    };

    return (
        <div className="personal">
            <Avatar size={64} icon={<UserOutlined />} />
            <h2>Thông tin cá nhân</h2>
            <p><strong>Họ và tên:</strong> {userDetails.fullName}</p>
            <p><strong>Số điện thoại:</strong> {userDetails.phoneNumber}</p>
            <p><strong>Địa chỉ:</strong> {userDetails.address}</p>
            <p><strong>Ngày sinh:</strong> {new Date(userDetails.dateOfBirth).toLocaleDateString()}</p>
            <p><strong>Email:</strong> {userDetails.email}</p>
            <div className="editPersonal">
                <Button type="primary" danger onClick={showModal} >
                    Chỉnh sửa thông tin
                </Button>
                <Modal
                    title={
                        <span style={{ fontSize: "27px" }}>
                            Chỉnh sửa thông tin cá nhân của bạn:
                        </span>
                    }
                    open={isModalOpen}
                    onOk={handleOk}
                    onCancel={handleCancel}
                    width={900}

                >
                    <Row gutter={16}>
                        <Col span={12}> {/* Cột trái */}
                            <p>Họ và tên:</p>
                            <Input
                                placeholder="Nhập tên của bạn..."
                                value={fullName}
                                onChange={(e) => setFullName(e.target.value)}
                                style={{ width: "400px", height: "40px", fontSize: "16px" }}
                            />
                            <p>Số điện thoại:</p>
                            <Input
                                placeholder="Sdt..."
                                value={phoneNumber}
                                onChange={(e) => setPhoneNumber(e.target.value)}
                                style={{ width: "200px", height: "40px", fontSize: "16px" }}
                            />
                            <p>Địa chỉ:</p>
                            <Input
                                placeholder=""
                                value={address}
                                onChange={(e) => setAddress(e.target.value)}
                                style={{ width: "400px", height: "40px", fontSize: "16px" }}
                            />
                            <p>Ngày sinh:</p>
                            <DatePicker
                                value={dob}
                                onChange={(date) => setDob(date)}
                                format="DD/MM/YYYY"
                                placeholder="Chọn ngày sinh"
                            />
                            <p>Email:</p>
                            <Input
                                placeholder=""
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                style={{ width: "300px", height: "40px", fontSize: "16px" }}
                            />
                        </Col>
                        <Col span={12}> {/* Cột phải */}
                            <p>Mật khẩu:</p>
                            <Input placeholder="" type="password"
                                style={{ width: "300px", height: "40px", fontSize: "16px" }} />
                            <p>Nhập lại mật khẩu:</p>
                            <Input placeholder="" type="password"
                                style={{ width: "300px", height: "40px", fontSize: "16px" }} />
                        </Col>
                    </Row>
                </Modal>
            </div>
        </div >
    );
};