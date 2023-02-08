import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Worker implements Runnable {

    private int ID;
    private Thread thread;
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;

    public Worker(int ID) {
        thread = new Thread(this);
        this.ID = ID;
    }

    @Override
    public void run() {

        System.out.println("New client at " + s.getInetAddress().getHostAddress() + " on port " + s.getPort());
        
        try {
             in = new DataInputStream(s.getInputStream());
             out = new DataOutputStream(s.getOutputStream());  
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        while (true) {
            try {
                String received = in.readUTF();
                System.out.println(received + " {From: " + s.getInetAddress().getHostAddress() + "}");
                Server.broadcastMessage(received, this);
            } catch (IOException e) {
                e.printStackTrace();
                Server.getConnections().remove(s);
                break;
            }
        }

        //Once client disconnects, return the thread to the pool
        try {
            System.out.println("Joining thread");
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

    public Socket getSocket() {
        return s;
    }
}