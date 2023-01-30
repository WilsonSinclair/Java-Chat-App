import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;

public class Client {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Parameters: <Server> [<Port>]");
        }
        String server = args[0];
        int servPort = (args.length == 2) ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        byte[] buffer;

        Socket socket = new Socket(server, servPort);
        System.out.println("Connected to server...");

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String message = scanner.nextLine();

            buffer = message.getBytes();
            out.write(buffer); 
        }
    }
}