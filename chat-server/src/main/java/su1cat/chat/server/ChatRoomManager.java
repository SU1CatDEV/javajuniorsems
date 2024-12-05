package su1cat.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomManager {
    private final Map<String, ChatRoom> chatRooms = new HashMap<>();

    public synchronized ChatRoom createChatRoom(String name, String pwdHash) throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        ChatRoom chatRoom = new ChatRoom(serverSocket, name, serverSocket.getLocalPort(), this, pwdHash);
        chatRooms.put(name, chatRoom);
        new Thread(chatRoom::runRoom).start();
        return chatRoom;
    }

    public synchronized void closeChatRoom(String name) {
        ChatRoom chatRoom = chatRooms.remove(name);
        if (chatRoom != null) {
            System.out.println("again");
            chatRoom.closeSocket();
        }
    }

    public Map<String, ChatRoom> getChatRooms() {
        return chatRooms;
    }
}

