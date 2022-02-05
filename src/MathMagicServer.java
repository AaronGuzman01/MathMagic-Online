
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 *
 * @author aaron
 */
public class MathMagicServer {
    
    private static final int SERVER_PORT = 9862;
    
    private static boolean rootLogin = false;
    
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            
            System.out.println("Server started at " + new Date());
            
            Socket socket = serverSocket.accept();
            
            DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
            
            DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
            
            while (true) {
                String message = inputFromClient.readUTF();
                
                if (message.contains(" ")) {
                    String command = message.substring(0, message.indexOf(" "));
                    
                    if (command.equalsIgnoreCase("LOGIN")) {
                        outputToClient.writeUTF("LOGIN COMMAND");
                    }
                    else if (command.equalsIgnoreCase("SOLVE")) {
                        outputToClient.writeUTF("SOLVE COMMAND");
                    }
                    else if (command.equalsIgnoreCase("LIST")) {
                        outputToClient.writeUTF("LIST COMMAND");
                    }
                    else {
                        outputToClient.writeUTF("300 invalid command");
                    }
                }
                else {
                    if (message.equalsIgnoreCase("LIST")) {
                        outputToClient.writeUTF("LIST COMMAND");
                    }
                    else if (message.equalsIgnoreCase("SHUTDOWN")) {
                        outputToClient.writeUTF("SHUTDOWN COMMAND");
                    }
                    else if (message.equalsIgnoreCase("LOGOUT")) {
                        outputToClient.writeUTF("LOGOUT COMMAND");
                    }
                    else {
                        outputToClient.writeUTF("300 invalid command");
                    }
                }
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
