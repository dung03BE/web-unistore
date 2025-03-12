import React from 'react';
import { Row, Col, Card, Statistic, Table, Typography, Space } from 'antd';
import { ArrowUpOutlined, ArrowDownOutlined, ShoppingCartOutlined, UserOutlined, DollarOutlined, TeamOutlined } from '@ant-design/icons';
import { Line, Pie } from '@ant-design/plots';

const { Title } = Typography;

const Dashboard = () => {
    // Dữ liệu mẫu cho biểu đồ doanh thu
    const revenueData = [
        { month: 'Tháng 1', revenue: 35000000 },
        { month: 'Tháng 2', revenue: 28000000 },
        { month: 'Tháng 3', revenue: 32000000 },
        { month: 'Tháng 4', revenue: 40000000 },
        { month: 'Tháng 5', revenue: 45000000 },
        { month: 'Tháng 6', revenue: 38000000 },
        { month: 'Tháng 7', revenue: 52000000 },
        { month: 'Tháng 8', revenue: 48000000 },
        { month: 'Tháng 9', revenue: 55000000 },
        { month: 'Tháng 10', revenue: 60000000 },
        { month: 'Tháng 11', revenue: 65000000 },
        { month: 'Tháng 12', revenue: 70000000 },
    ];

    // Dữ liệu mẫu cho biểu đồ sản phẩm bán chạy
    const bestSellerData = [
        { type: 'iPhone 15 Pro Max', value: 25 },
        { type: 'Samsung Galaxy S24', value: 20 },
        { type: 'Xiaomi 14 Pro', value: 15 },
        { type: 'OPPO Find X7 Ultra', value: 10 },
        { type: 'Khác', value: 30 },
    ];

    // Cấu hình cho biểu đồ doanh thu
    const revenueChartConfig = {
        data: revenueData,
        xField: 'month',
        yField: 'revenue',
        point: {
            size: 5,
            shape: 'diamond',
        },
        label: {
            formatter: (datum) => `${(datum.revenue / 1000000).toFixed(0)}tr`,
        },
    };

    // Cấu hình cho biểu đồ sản phẩm bán chạy
    const bestSellerChartConfig = {
        data: bestSellerData,
        angleField: 'value',
        colorField: 'type',
        radius: 0.8,
        label: {
            type: 'outer',
            content: '{name} {percentage}',
        },
        interactions: [{ type: 'pie-legend-active' }, { type: 'element-active' }],
    };

    // Dữ liệu mẫu cho bảng sản phẩm bán chạy
    const topProductsData = [
        {
            key: '1',
            name: 'iPhone 15 Pro Max',
            category: 'Apple',
            sold: 142,
            revenue: 7100000000,
        },
        {
            key: '2',
            name: 'Samsung Galaxy S24 Ultra',
            category: 'Samsung',
            sold: 98,
            revenue: 3920000000,
        },
        {
            key: '3',
            name: 'Xiaomi 14 Pro',
            category: 'Xiaomi',
            sold: 76,
            revenue: 2280000000,
        },
        {
            key: '4',
            name: 'OPPO Find X7 Ultra',
            category: 'OPPO',
            sold: 65,
            revenue: 1950000000,
        },
        {
            key: '5',
            name: 'Vivo X100 Pro',
            category: 'Vivo',
            sold: 54,
            revenue: 1620000000,
        },
    ];

    const columns = [
        {
            title: 'Tên sản phẩm',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: 'Danh mục',
            dataIndex: 'category',
            key: 'category',
        },
        {
            title: 'Đã bán',
            dataIndex: 'sold',
            key: 'sold',
            sorter: (a, b) => a.sold - b.sold,
        },
        {
            title: 'Doanh thu',
            dataIndex: 'revenue',
            key: 'revenue',
            render: (text) => `${(text / 1000000).toFixed(0)} triệu VNĐ`,
            sorter: (a, b) => a.revenue - b.revenue,
        },
    ];

    return (
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
            <Title level={2}>Dashboard</Title>

            {/* Thống kê tổng quan */}
            <Row gutter={16}>
                <Col span={6}>
                    <Card>
                        <Statistic
                            title="Doanh thu tháng"
                            value={433000000}
                            precision={0}
                            valueStyle={{ color: '#3f8600' }}
                            prefix={<DollarOutlined />}
                            suffix="VNĐ"
                            formatter={(value) => `${(value / 1000000).toFixed(0)} triệu`}
                        />
                    </Card>
                </Col>
                <Col span={6}>
                    <Card>
                        <Statistic
                            title="Đơn hàng tháng"
                            value={458}
                            valueStyle={{ color: '#3f8600' }}
                            prefix={<ShoppingCartOutlined />}
                            suffix={<span style={{ fontSize: 14, color: 'rgba(0,0,0,0.45)' }}><ArrowUpOutlined /> 12.3%</span>}
                        />
                    </Card>
                </Col>
                <Col span={6}>
                    <Card>
                        <Statistic
                            title="Khách hàng mới"
                            value={87}
                            valueStyle={{ color: '#3f8600' }}
                            prefix={<UserOutlined />}
                            suffix={<span style={{ fontSize: 14, color: 'rgba(0,0,0,0.45)' }}><ArrowUpOutlined /> 8.1%</span>}
                        />
                    </Card>
                </Col>
                <Col span={6}>
                    <Card>
                        <Statistic
                            title="Nhân viên"
                            value={24}
                            valueStyle={{ color: '#1677ff' }}
                            prefix={<TeamOutlined />}
                        />
                    </Card>
                </Col>
            </Row>

            {/* Biểu đồ */}
            <Row gutter={16}>
                <Col span={16}>
                    <Card title="Doanh thu theo tháng">
                        <Line {...revenueChartConfig} height={300} />
                    </Card>
                </Col>
                <Col span={8}>
                    <Card title="Sản phẩm bán chạy">
                        <Pie {...bestSellerChartConfig} height={300} />
                    </Card>
                </Col>
            </Row>

            {/* Bảng sản phẩm bán chạy */}
            <Card title="Top 5 sản phẩm bán chạy">
                <Table columns={columns} dataSource={topProductsData} pagination={false} />
            </Card>
        </Space>
    );
};

export default Dashboard;