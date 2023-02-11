import java.net.Socket;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.awt.Dimension;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.Color;

public class Client extends JFrame implements Runnable, KeyListener {

    private static final int DEFAULT_PORT = 8080;
    private static DataOutputStream out;
    private static DataInputStream in;
    private static Socket socket;
    private static Thread incomingMessageThread;
    private static String nickname;

    private static final int WIDTH = 1200;
    private static final int HEIGHT = (WIDTH / 16) * 9;

    private JList<String> messagePane;
    private static JTextArea textField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private DefaultListModel<String> messagesModel;
    private JScrollPane messageScrollPane;

    public Client() {
        setTitle("Chat App");
        setSize(new Dimension(WIDTH, HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);

        userListModel = new DefaultListModel<String>();
        messagesModel = new DefaultListModel<String>();

        setLayout(new BorderLayout());

        textField = new JTextArea();
        textField.setRows(7);
        textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        textField.setLineWrap(true);
        textField.addKeyListener(this);

        messagePane = new JList<String>();
        messagePane.setRequestFocusEnabled(false);
        messagePane.setAutoscrolls(true);
        messagePane.setModel(messagesModel);
        messagePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Messages"));
        messageScrollPane = new JScrollPane(messagePane);
        messageScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
            public void adjustmentValueChanged(AdjustmentEvent e) {  
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
            }
        });

        userList = new JList<>();
        userList.setModel(userListModel);
        userListModel.addElement(nickname);
        userList.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Users"));

        add(textField, BorderLayout.SOUTH);
        add(messageScrollPane);
        add(userList, BorderLayout.EAST);

        setVisible(true);

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
                messagesModel.addElement(message);
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int key = event.getKeyCode();

        if (key == KeyEvent.VK_ENTER) {
            try {
                out.writeUTF(getNickname() + ": " + textField.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
            textField.setCaretPosition(0);
            textField.setText("");
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {

    }

    @Override
    public void keyTyped(KeyEvent event) {

    }

    public static void main(String[] args) throws IOException {
        
        if (args.length < 1) {
            throw new IllegalArgumentException("Parameters: <Server> [<Port>]");
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a Nickname: ");
        nickname = scanner.nextLine();

        String server = args[0];
        int servPort = (args.length == 2) ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        socket = new Socket(server, servPort);
        System.out.println("Connected to server...");

        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        out.writeUTF(nickname);

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
