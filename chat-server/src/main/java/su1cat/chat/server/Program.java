package su1cat.chat.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Program {
    public static void main(String[] args) {
        try {
            ServerSocket mainServerSocket = new ServerSocket(1400);
            ChatRoomManager chatRoomManager = new ChatRoomManager();

            ChatRoom mainChatRoom = new ChatRoom(mainServerSocket, "global", mainServerSocket.getLocalPort(), chatRoomManager, null);
            new Thread(mainChatRoom::runRoom).start(); // Start the main chat room

            System.out.println("Main chat room running on port 1400");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
