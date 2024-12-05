package su1cat.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatRoom {
    private final ServerSocket serverSocket;
    public final String name;
    public final Integer port;
    private final ChatRoomManager chatRoomManager;
    private final String pwdHash;


    public ChatRoom(ServerSocket serverSocket, String name, Integer port, ChatRoomManager chatRoomManager, String pwdHash) {
        this.serverSocket = serverSocket;
        this.name = name;
        this.port = port;
        this.chatRoomManager = chatRoomManager;
        this.pwdHash = pwdHash;
    }

    public void runRoom() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientManager clientManager = new ClientManager(socket, port, chatRoomManager);
                System.out.println("New client connected to room " + port);
                Thread thread = new Thread(clientManager);
                thread.start();

            }
        } catch (IOException e) {
            closeSocket();
        }
    }

    public void closeSocket() {
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Chat room on port " + port + " has been closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPwdHash() {
        return pwdHash;
    }


}
