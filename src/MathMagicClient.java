
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author aaron
 */
public class MathMagicClient {
    
    private static final int SERVER_PORT = 9862;
    
    public static void main(String[] args) {
        DataOutputStream toServer;
        DataInputStream fromServer;
        String message;
        
        Scanner input = new Scanner(System.in);
        
        try {
            Socket socket = new Socket("localhost", SERVER_PORT);
            
            System.out.println("MathMagic connection established");
            
            fromServer = new DataInputStream(socket.getInputStream());
            
            toServer = new DataOutputStream(socket.getOutputStream());
            
            
            System.out.println("Start entering a command:\n");
            
            while (true) {
                message = input.nextLine();
                
                toServer.writeUTF(message);
                
                message = fromServer.readUTF();
                
                System.out.println(message);
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
