import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.OutputStream;

public class Server {

    private static final int BUFF_SIZE = 32;

    private static ServerSocket initializeServer(int port) throws IOException {
        if (port <= 1023 || port > 65535) {
            System.out.println("Port must be greater than 1023 and less than 65535");
            System.exit(1);
        }
        
        try {
          ServerSocket serverSocket = new ServerSocket(port);
          return serverSocket;  
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void checkArguments(String[] args) {
        if (args.length < 1) {
            System.out.println("Argument Error... Format: <Port>");
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        checkArguments(args);

        int port = Integer.valueOf(args[0]);
        ServerSocket servSock = initializeServer(port);
        System.out.println("Server initialized on port: " + args[0]);

        byte[] buffer = new byte[BUFF_SIZE];
        int receivedMessageSize;

        //handle connections
        while (true) {  
            //accept a client
            Socket s = servSock.accept();
            System.out.println("New client at " + s.getInetAddress().getHostAddress() + " on port " + s.getPort());
            
            InputStream in = s.getInputStream();
            OutputStream out = s.getOutputStream();
            
            while (true) {
                receivedMessageSize = in.read(buffer);
                if (receivedMessageSize == -1) break; // if the client disconnects
                String received = new String(buffer, StandardCharsets.UTF_8);
                System.out.println(received + " {From: " + s.getInetAddress().getHostAddress() + "}");    
            }
        }
    }
}