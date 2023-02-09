import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.awt.Dimension;

public class Client extends JFrame implements Runnable {

    private static final int DEFAULT_PORT = 8080;
    private static DataOutputStream out;
    private static DataInputStream in;
    private static Socket socket;
    private static Thread incomingMessageThread;
    private static String nickname;

    public Client() {
        setTitle("Chat App");
        setSize(new Dimension(600, 600));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setVisible(true);
        incomingMessageThread = new Thread(this);
        incomingMessageThread.start();
    }

    public static String getNickname() {
        return nickname;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = in.readUTF();
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Parameters: <Server> [<Port>]");
        }

        Scanner scanner = new Scanner(System.in);
        nickname = scanner.nextLine();

        String server = args[0];
        int servPort = (args.length == 2) ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        socket = new Socket(server, servPort);
        System.out.println("Connected to server...");

        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        out.writeUTF("(SERVER): " + nickname + " has joined the room!");

        new Client();

        while (!socket.isClosed()) {

            String message = scanner.nextLine();

            out.writeUTF(getNickname() + ": " + message);
        }
        scanner.close();
        
        try {
            incomingMessageThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
