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
    private String clientNickname;

    public Worker(int ID) {
        thread = new Thread(this);
        this.ID = ID;
    }

    @Override
    public void run() {

        System.out.println("New client at " + s.getInetAddress().getHostAddress() + " on port " + s.getPort());
        
        while (true) {
            try {
                String received = in.readUTF();
                System.out.println(received + " {From: " + s.getInetAddress().getHostAddress() + "}");
                //Put any command logic here, such as /Name or something like that
                
                Server.broadcastMessage(received);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        //Once client disconnects, return the thread to the pool
        try {
            Server.removeConnection(s);
            System.out.println("Joining thread");
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  
    }

    public void handleConnection(Socket s) {
        this.s = s;
        try {
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream()); 
            clientNickname = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        thread.start();
    }

    public int getID() {
        return ID;
    }

    public Thread getThread() {
        return thread;
    }

    public String getClientNickname() {
        return clientNickname;
    }

    public boolean isBusy() {
        return thread.isAlive();
    }

    public Socket getSocket() {
        return s;
    }
}
