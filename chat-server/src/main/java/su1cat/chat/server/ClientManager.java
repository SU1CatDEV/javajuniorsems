package su1cat.chat.server;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ClientManager implements Runnable {
    private final Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private String name;
    private Integer port;
    private final ChatRoomManager chatRoomManager;
    public final static HashMap<Integer, ArrayList<ClientManager>> clients = new HashMap<>();

    public ClientManager(Socket socket, Integer port, ChatRoomManager chatRoomManager) {
        this.socket = socket;
        this.port = port;
        this.chatRoomManager = chatRoomManager;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = in.readLine();
            ArrayList<ClientManager> currentClients = clients.get(port);
            if (currentClients != null) {
                clients.get(port).add(this);
            } else {
                clients.put(port, new ArrayList<>(List.of(this)));
            }
            System.out.println(name + " connected to the chat");
            broadcastMessage("Server: " + name + " connected to the chat.");
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, in, out);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        boolean joinSuccess = false;

        while (socket.isConnected() && !socket.isClosed()) {
            try {
                messageFromClient = in.readLine();
                if (messageFromClient != null) {
                    if (messageFromClient.startsWith(name+": /create")) {
                        createRoom(messageFromClient);
                    } else if (messageFromClient.startsWith(name + ": /join") && messageFromClient.split(" ").length > 2) {
                        String[] parseArray = messageFromClient.split(" ");
                        String roomName = parseArray[2];
                        if (chatRoomManager.getChatRooms().containsKey(roomName)) {
                            ChatRoom joining = chatRoomManager.getChatRooms().get(roomName);
                            if (joining.getPwdHash() != null) {
                                joinSuccess = joinPwdRoom(parseArray, joining);
                            } else {
                                out.write("/room " + joining.port);
                                joinSuccess = true;
                            }
                        } else {
                            out.write("Room not found");
                            joinSuccess = false;
                        }
                        out.newLine();
                        out.flush();

                        if (joinSuccess) {
                            closeEverything(socket, in, out);
                        }
                    } else if (messageFromClient.startsWith(name + ": /exit")) {
                        if (port != 1400) {
                            out.write("/room 1400");
                        } else {
                            out.write("/term");
                        }
                        out.newLine();

                        if (clients.get(port).size() == 1) {
                            chatRoomManager.closeChatRoom(findRoomByPort(chatRoomManager.getChatRooms(), port));
                        }
                        closeEverything(socket, in, out);
                        return;
                    }
                    else {
                        broadcastMessage(messageFromClient);
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                closeEverything(socket, in, out);
                break;
            }
        }
    }

    private String findRoomByPort(Map<String, ChatRoom> chatRooms, int findPort) {
        for (Map.Entry<String, ChatRoom> entry : chatRooms.entrySet()) {
            if (entry.getValue().port == findPort) {
                return entry.getKey();
            }
        }
        return null;
    }

    private boolean joinPwdRoom(String[] parseArray, ChatRoom chatRoom) throws IOException {
        try {
            if (parseArray.length <= 3 || !Objects.equals(chatRoom.getPwdHash(), hashToString(parseArray[3]))) {
                out.write("Incorrect password.");
                return false;
            }
            else {
                out.write("/room " + chatRoom.port);
                return true;
            }
        } catch (IndexOutOfBoundsException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            out.write("Could not join room.");
            return false;
        }
    }

    private String hashToString(String stringToHash) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(stringToHash.getBytes());
        byte[] hashBytes = messageDigest.digest();

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

    private void broadcastMessage(String message) {
        for (ClientManager client : clients.get(this.port)) {
            try {
                if (!client.name.equals(name)) {
                    client.out.write(message);
                    client.out.newLine();
                    client.out.flush();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                closeEverything(socket, in, out);
            }
        }
    }

    private void createRoom(String command) {
        String[] parseArray = command.split(" ");
        ChatRoom newChatRoom;
        try {
            if (parseArray.length < 1) {
                out.write("Enter room name: ");
                out.newLine();
                out.flush();
                String roomName = in.readLine();
                newChatRoom = chatRoomManager.createChatRoom(roomName, null);
            }
            else if (3 < parseArray.length) {
                try {
                    String stringHash = hashToString(parseArray[3]);
                    newChatRoom = chatRoomManager.createChatRoom(parseArray[2], stringHash);
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace(); // this is just a formality. since the algorithm is hard-coded, this error will never occur.
                    newChatRoom = chatRoomManager.createChatRoom(parseArray[2], null);
                    out.write("Could not hash password, creating room without password.");
                    out.newLine();
                    out.flush();
                }
            } else {
                newChatRoom = chatRoomManager.createChatRoom(parseArray[2],null);
            }


            System.out.println("Room " + parseArray[1] + " created");
            out.write("/room " + newChatRoom.port);
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        closeEverything(socket, in, out);
    }

    public void removeClient() {
        clients.get(port).remove(this);
        System.out.println(name + " left the chat.");
        broadcastMessage("Server: " + name + " left the chat.");
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e){
            System.out.println("Catastrophic failure, you're screwed lolw");
            e.printStackTrace();
        }

    }
}

