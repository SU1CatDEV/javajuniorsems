package su1cat.chat.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Starting client... Enter your name: ");
            String name = scanner.nextLine();

            Socket socket = new Socket("localhost", 1400);
            Client client = new Client(socket, name);

            client.listenForMessage();
            client.sendMessage();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}