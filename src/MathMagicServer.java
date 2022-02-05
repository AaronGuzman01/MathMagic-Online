
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aaron
 */
public class MathMagicServer {
    
    private static final int SERVER_PORT = 9862;
    
    private static boolean rootLogin = false;
    
    private static List<String> userList;
    
    private static String userLogged;
      
    public static void main(String[] args) {
        try {
            
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            
            userList = Files.readAllLines(Path.of("logins.txt"));
            
            userList.remove(" ");
                       
            System.out.println("Server started at " + new Date());
            
            Socket socket = serverSocket.accept();
            
            DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
            
            DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
            
            while (true) {
                String message = inputFromClient.readUTF();
                
                if (message.contains(" ")) {
                    String command = message.substring(0, message.indexOf(" "));
                    
                    if (command.equalsIgnoreCase("LOGIN")) {
                        String loginInfo = message.substring(message.indexOf(" ") + 1, message.length());
                        
                        if (Pattern.matches(".+ .+", loginInfo)) {
                            boolean found = false;
                            for (String user: userList) {
                                if (user.equals(loginInfo)) {
                                    userLogged = user;
                                    
                                    found = true;
                                    
                                    outputToClient.writeUTF("SUCCESS");
                                    
                                    break;
                                }
                            }
                            
                            if (!found) {
                                outputToClient.writeUTF("FAILURE: Please provide a correct username and password. Try again.");
                            }
                        }
                        else {
                            outputToClient.writeUTF("301 message format error");
                        }
                        
                        //outputToClient.writeUTF("LOGIN COMMAND");
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
