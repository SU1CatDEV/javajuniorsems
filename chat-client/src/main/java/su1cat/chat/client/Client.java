package su1cat.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private final String name;

    private BufferedWriter out;
    private BufferedReader in;

    public Client(Socket socket, String userName) {
        this.socket = socket;
        this.name = userName;

        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String message;
            while (socket.isConnected()) {
                try {
                    message = in.readLine();
                    if (message != null) {
                        if (message.startsWith("/room")) {
                            int newPort = Integer.parseInt(message.split(" ")[1]);
                            reconnectToRoom(newPort);
                        } else if (message.equals("/term")) {
                            System.exit(0);
                        } else {
                            System.out.println(message);
                        }
                    }
                } catch (IOException ex) {
                    closeEverything(socket, in, out);
                }
            }
        }).start();
    }

    public void sendMessage() {
        try {
            out.write(name);
            out.newLine();
            out.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                out.write(name + ": " + message);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    private void reconnectToRoom(int newPort) {
        try {
            System.out.println("Reconnecting to new chat room on port: " + newPort);

            // Close current resources
            closeEverything(socket, in, out);

            // Create new socket and connect to the new room
            socket = new Socket("localhost", newPort);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Resend username to the new room
            out.write(name);
            out.newLine();
            out.flush();

            System.out.println("Successfully connected to the new room on port: " + newPort);
        } catch (IOException e) {
            System.out.println("Failed to reconnect to new room.");
            e.printStackTrace();
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
