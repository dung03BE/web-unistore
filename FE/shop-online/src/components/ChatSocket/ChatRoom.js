import React, { useState, useEffect } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { Input } from "../ChatSocket/input";
import { Button } from "../ChatSocket/button";
import { useSelector } from 'react-redux';
import "../ChatSocket/ChatRoom.scss";
import GuiComment from './GuiComment';
const ChatRoom = ({ initialComments }) => {
    const userDetails = useSelector(state => state.userReducer.userDetails); // Lấy userDetails từ Redux store
    const fullName = userDetails?.fullName || 'Guest'; // Lấy fullName hoặc 'Guest' nếu không có
    const [messages, setMessages] = useState(initialComments || []);
    const [newMessage, setNewMessage] = useState('');
    const [stompClient, setStompClient] = useState(null);
    const [connected, setConnected] = useState(false);

    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8081/ws-chat'),
            onConnect: () => {
                setConnected(true);  // Kết nối thành công
                client.subscribe('/topic/public', (message) => {
                    const newMessage = JSON.parse(message.body);
                    setMessages(prev => [...prev, newMessage]);  // Nhận tin nhắn mới
                });
            },
            onDisconnect: () => {
                setConnected(false);  // Kết nối bị ngắt
            },
            onStompError: (error) => {
                console.error('STOMP error:', error);
                setConnected(false);  // Nếu có lỗi
            }
        });

        client.activate();
        setStompClient(client);

        return () => {
            client.deactivate();  // Tắt kết nối khi component unmount
        };
    }, []);

    const sendMessage = (e) => {
        e.preventDefault();
        if (!newMessage.trim() || !connected) return;

        stompClient.publish({
            destination: '/app/chat.send',
            body: JSON.stringify({
                content: newMessage,
                sender: fullName, // Sử dụng fullName từ Redux store
                type: 'CHAT'
            })
        });

        setNewMessage('');
    };

    return (

        <div className="chat-room p-4">
            <div className="messages h-96 overflow-auto mb-4 border rounded p-2">
                {messages.map((msg, idx) => (
                    <div key={idx} className="mb-2 p-2 border-bottom">
                        <div style={{ display: 'flex', alignItems: 'center' }}> {/* Sử dụng inline style */}
                            <img
                                src={`https://i.pravatar.cc/50?u=${msg.username}`}
                                alt="Avatar"
                                className="rounded-circle mr-2"
                                width="40"
                                height="40"
                            />
                            <div>
                                <div className="font-weight-bold">
                                    {msg.username}
                                </div>
                                <div className="text-muted small">
                                    {new Date(msg.timestamp).toLocaleString()}
                                </div>
                                <div>{msg.content}</div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            <form onSubmit={sendMessage} className="flex gap-4 items-center">
                <h2> <GuiComment /></h2>

                <Input
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    placeholder="Type message..."
                    className="flex-1"
                />

                <Button type="submit" disabled={!connected} >Send</Button>
            </form>
        </div>
    );
};

export default ChatRoom;
