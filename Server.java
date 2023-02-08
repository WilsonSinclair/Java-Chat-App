import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static final int BUFF_SIZE = 32;
    private static final int MAX_WORKERS = 10;
    private static Worker[] workerPool = new Worker[MAX_WORKERS];
    private static ArrayList<Socket> connections = new ArrayList<Socket>();

    private static ServerSocket initializeServer(int port) throws IOException {
        if (port <= 1023 || port > 65535) {
            throw new IllegalArgumentException("Port must be greater than 1023 and less than 65535");
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
            throw new IllegalArgumentException("Argument Error... Format: <Port>");
        }
    }

    public static ArrayList<Socket> getConnections() {
        return connections;
    }

    public static void broadcastMessage(String message, Worker sender) {
        DataOutputStream out;
        for (Worker w : workerPool) {
            if (w.isBusy() && w != sender) {
                try {
                    out = new DataOutputStream(w.getSocket().getOutputStream());
                    out.writeUTF(message);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        checkArguments(args);

        int port = Integer.valueOf(args[0]);
        ServerSocket servSock = initializeServer(port);
        System.out.println("Server initialized on port: " + args[0]);
        
        //10 workers to handle 10 requests at a time
        //an unlimited number of workers is bad as an attacker could create as many as they wanted and starve the server
        for (int i = 0; i < MAX_WORKERS; i++) {
            workerPool[i] = new Worker(i);
        }

        //handle connections
        while (true) {  
            //accept a client
            for (Worker worker : workerPool) {
               if (!worker.isBusy()) {
                //blocks here and waits for a worker to accept an incomming connections
                worker.handleConnection(servSock.accept());

                connections.add(worker.getSocket());
                System.out.println("Worker " + worker.getID() +  " has picked up a client.");
               } 
            }
        }
    }
}