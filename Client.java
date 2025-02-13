import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Client extends JFrame {
    
    Socket socket;
    BufferedReader br;
    PrintWriter out;

            /**************** DECLARING COMPONENTS ****************/
    private JLabel heading = new JLabel("Client Text Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Arial", Font.BOLD | Font.ITALIC, 20);

            /**************** CONSTRUCTOR ****************/
    public Client() {
        try {
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1", 8888); //Specifying port, host address.
            System.out.println("Connection established with server.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));//Getting Input Stream.
            out = new PrintWriter(socket.getOutputStream());//Getting output Stream.

            createGUI(); //Creating GUI
            handleEvents(); //Handling events
            startReading(); //Reading text
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        /**************** HANDLING EVENTS ****************/
    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { //Checking the currently used key is ENTER KEY or not.
                    String contentToSend = messageInput.getText(); //Getting Text.
                    messageArea.append("Me: " + contentToSend + "\n"); //Displaying message in message area.
                    out.println(contentToSend); //Sending message.
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

            /**************** CREATING GUI ****************/
    private void createGUI() {
        //GUI Code
        this.setTitle("Client Messenger Window");
        this.setSize(700, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //For Closing button i.e, 'x'

        //Coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        //Setting ICON.
        heading.setIcon(new ImageIcon("Chat_Icon.jpg"));

        //Aligning Image.
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);

        //Aligning setTitle to center.
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageArea.setEditable(false);

        //Working on message Sending Tab..
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        //Setting Frame Layout....
        this.setLayout(new BorderLayout());

        //Adding Components to Frame..
        this.add(heading, BorderLayout.NORTH);

        //adding Scroll bar.
        JScrollPane jScrollPane = new JScrollPane(messageArea);

        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
    }

            /**************** READ FUNCTION ****************/

    private void startReading() {

        //Creating Thread
        //This thread is responsible for reading data continously from client.

        Runnable r1 = () -> {
            System.out.println("Reader started...");
            while (true) {
                try {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Client terminated the chat");
                        SwingUtilities.invokeLater(() -> {
                            messageArea.append("Client terminated the chat\n");
                            messageInput.setEnabled(false);
                        });
                        socket.close();
                        break;
                    }
                    SwingUtilities.invokeLater(() -> {
                        messageArea.append("Client: " + msg + "\n");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        };
        new Thread(r1).start();
    }


            /**************** MAIN FUNCTION ****************/
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client());
    }
}
                
            
        
                   

