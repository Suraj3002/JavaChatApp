import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    ServerSocket server;
    Socket socket;

    BufferedReader br;
    PrintWriter out;


    // declare components
    private JLabel heading = new JLabel("Server Area");
    //    private JTextArea messageArea = new JTextArea();
    TextArea messageArea = new TextArea();  // using textArea istead of JTextArea for autoScrollBar at the end line of Text
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    public Server()
    {
        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connection");
            System.out.println("waiting...");
            socket = server.accept();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();

            startReading();
//            startWriting();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

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
//                System.out.println("Key released: "+e.getKeyCode());
                if(e.getKeyCode() == 10){
//                      System.out.println("You have pressed enter button");
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me :"+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    private void createGUI() {
        // creating GUI
        this.setTitle("Server Messager[End]");
        this.setSize(600,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("E:\\java program\\ChatApp\\src\\clogo.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        // setting frame layout
        this.setLayout(new BorderLayout());
        // adding the components to the frame
        this.add(heading,BorderLayout.NORTH);
        // JScrollPane used for  scroll bar but cursor/scrollbar not go on end line of Text s
//        JScrollPane jScrollPane = new JScrollPane(messageArea);
//        this.add(jScrollPane,BorderLayout.CENTER);

        this.add(messageArea,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);



        this.setVisible(true);
    }

    private void startReading() {
        // this thread will be used for reading the data
        Runnable r1 = ()-> {
            System.out.println("Reader started..");
            try {
            while (true){
                    String msg = br.readLine();
                    if(msg.equals("exit")){
                        System.out.println("client has terminated the chat");
                        JOptionPane.showMessageDialog(this,"Server Terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
//                    System.out.println("Client : "+msg);
                messageArea.append("Client :"+msg+"\n");
                }
            }
            catch (IOException e) {
//                e.printStackTrace();
                System.out.println("Connection is closed");
            }
        };
        new Thread(r1).start();
    }

    private void startWriting() {
        // this thread take the data from user and send to the client
        Runnable r2 = ()-> {
            System.out.println("Writer Started..");
            try {
           while (!socket.isClosed()){
                   BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                   String content = br1.readLine();
                   out.println(content);
                   out.flush();

                   if(content.equals("exit")){
                       socket.close();
                       break;
                   }
               }
           }catch (Exception e){
//                e.printStackTrace();
                System.out.println("Connection is closed");
            }
        };
        new Thread(r2).start();
    }


    public static void main(String[] args) {
        System.out.println("This is server....");
        new Server();
    }
}
