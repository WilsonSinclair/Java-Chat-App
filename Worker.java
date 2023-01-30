import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Worker implements Runnable {

    private int ID;
    private Thread thread;
    private Socket s;
    private InputStream in;
    private OutputStream out;

    public Worker(int ID) {
        thread = new Thread(this);
        this.ID = ID;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[Server.BUFF_SIZE];
        int receivedMessageSize;

        System.out.println("New client at " + s.getInetAddress().getHostAddress() + " on port " + s.getPort());
        
        try {
             in = s.getInputStream();
             out = s.getOutputStream();  
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        while (true) {
            try {
                receivedMessageSize = in.read(buffer);
                if (receivedMessageSize == -1) break; // if the client disconnects
            } catch (IOException e) {
                e.printStackTrace();
            }
            String received = new String(buffer, StandardCharsets.UTF_8);
            System.out.println(received + " {From: " + s.getInetAddress().getHostAddress() + "}");    
        }
        
        //Once client disconnects, return the thread to the pool
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  
    }

    public void handleConnection(Socket s) {
        this.s = s;
        thread.start();
    }

    public int getID() {
        return ID;
    }

    public Thread getThread() {
        return thread;
    }

    public boolean isBusy() {
        return thread.isAlive();
    }
}