
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author aaron
 */
public class MathMagicServer {
    private static final int SERVER_PORT = 9862;

    private static final String FILES_PATH = "Solution Files/";

    private static boolean rootLogin = false;

    private static boolean loggedIn = false;
    
    private static Socket socket;
    
    private static DataInputStream inputFromClient;
    
    private static DataOutputStream outputToClient;
    
    private static List<String> userList;

    private static String userLogged;

    private static File solutionFile;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            userList = Files.readAllLines(Path.of("logins.txt"));

            userList.remove(" ");

            System.out.println("Server started at " + new Date());

            socket = serverSocket.accept();
            
            inputFromClient = new DataInputStream(socket.getInputStream());

            outputToClient = new DataOutputStream(socket.getOutputStream());

            while (true) {
                if (socket.isClosed()) {
                    socket = serverSocket.accept();
                    
                    inputFromClient = new DataInputStream(socket.getInputStream());

                    outputToClient = new DataOutputStream(socket.getOutputStream());
                }
                
                String message = inputFromClient.readUTF();

                if (message.contains(" ")) {
                    String command = message.substring(0, message.indexOf(" "));

                    if (command.equalsIgnoreCase("LOGIN")) {
                        String loginInfo = message.substring(message.indexOf(" ") + 1, message.length());

                        if (loginInfo.matches(".+ .+")) {
                            boolean found = false;
                            for (String user : userList) {
                                if (user.equals(loginInfo)) {
                                    userLogged = user.substring(0, message.indexOf(" "));

                                    userLogged = userLogged.trim();

                                    loggedIn = true;

                                    found = true;

                                    if (userLogged.equals("root")) {
                                        rootLogin = true;
                                    }

                                    handleUserFile(userLogged);

                                    outputToClient.writeUTF("SUCCESS \n");

                                    break;
                                }
                            }

                            if (!found) {
                                outputToClient.writeUTF("FAILURE: Please provide a correct username and password. Try again. \n");
                            }
                        } 
                        else {
                            outputToClient.writeUTF("301 message format error \n");
                        }
                    } 
                    else if (command.equalsIgnoreCase("SOLVE")) {
                        if (loggedIn) {
                            String problem = message.substring(message.indexOf(" ") + 1, message.length());

                            if (problem.matches("^-c.*")) {
                                problem = problem.substring(problem.indexOf("c") + 1, problem.length());

                                if (problem.matches(" \\d+\\s*")) {
                                    double val = Double.parseDouble(problem);
                                    double circumference = 2.0 * Math.PI * val;
                                    double area = Math.PI * (val * val);

                                    String results = "Circle’s circumference is " + String.format("%.2f", circumference)
                                            + " and area is " + String.format("%.2f", area);

                                    outputToClient.writeUTF(results + "\n");

                                    writeToFile(results);

                                } 
                                else if (problem.isEmpty() || problem.isBlank()) {
                                    outputToClient.writeUTF("Error: No radius found \n");

                                    writeToFile("Error: No radius found \n");
                                } 
                                else {
                                    outputToClient.writeUTF("301 message format error \n");
                                }
                            } 
                            else if (problem.matches("^-r.*")) {
                                problem = problem.substring(problem.indexOf("r") + 1, problem.length());

                                if (Pattern.matches(" \\d+ \\d+\\s*", problem)) {
                                    problem = problem.substring(1, problem.length());

                                    double val1 = Double.parseDouble(problem.substring(0, problem.indexOf(" ")));
                                    double val2 = Double.parseDouble(problem.substring(problem.indexOf(" "), problem.length()));
                                    double perimeter = 2.0 * (val1 + val2);
                                    double area = val1 * val2;

                                    String results = "Rectangle’s perimeter is " + String.format("%.2f", perimeter)
                                            + " and area is " + String.format("%.2f", area);

                                    outputToClient.writeUTF(results + "\n");

                                    writeToFile(results);

                                } 
                                else if (Pattern.matches(" \\d+\\s*", problem)) {
                                    double val = Double.parseDouble(problem);
                                    double perimeter = 4.0 * val;
                                    double area = val * val;

                                    String results = "Rectangle’s perimeter is " + String.format("%.2f", perimeter)
                                            + " and area is " + String.format("%.2f", area);

                                    outputToClient.writeUTF(results + "\n");

                                    writeToFile(results);

                                } 
                                else if (problem.isEmpty() || problem.isBlank()) {
                                    outputToClient.writeUTF("Error: No radius found \n");

                                    writeToFile("Error: No radius found \n");
                                } 
                                else {
                                    outputToClient.writeUTF("301 message format error \n");
                                }
                            } 
                            else {
                                outputToClient.writeUTF("301 message format error \n");
                            }
                        } 
                        else {
                            outputToClient.writeUTF("Error: You must login to use this command \n");
                        }
                    } 
                    else if (command.equalsIgnoreCase("LIST")) {
                        if (rootLogin) {
                            String flag = message.substring(message.indexOf(" "), message.length());

                            if (flag.matches("^ -all\\s*")) {
                                List<String> interactions;
                                String userName, list = "";
                                
                                for (String user: userList) {
                                    userName = user.substring(0, message.indexOf(" ") + 1);
                                    
                                    userName = userName.trim();
                                    
                                    handleUserFile(userName);
                                    
                                    interactions = GetFileLines(userName);
                                    
                                    list += listToString(interactions, userName);
                                    
                                    list += "\n";
                                }
                                
                                outputToClient.writeUTF(list);
                            } 
                            else {
                                outputToClient.writeUTF("301 message format error \n");
                            }
                        } 
                        else {
                            outputToClient.writeUTF("Error: You must be the root user to use this command \n");
                        }
                    } 
                    else {
                        outputToClient.writeUTF("300 invalid command \n");
                    }
                } 
                else {
                    if (message.equalsIgnoreCase("LIST")) {
                        if (loggedIn) {
                            List<String> interactions;
                            String list;

                            interactions = GetFileLines(userLogged);

                            list = listToString(interactions, userLogged);

                            list += "\n";

                            outputToClient.writeUTF(list);
                        }
                        else {
                            outputToClient.writeUTF("Error: You must login to use this command \n");
                        }
                    } 
                    else if (message.equalsIgnoreCase("SHUTDOWN")) {
                        if (loggedIn) {
                            outputToClient.writeUTF("200 OK \n");
                            
                            socket.close();

                            serverSocket.close();
                            
                            break;
                        }
                        else {
                            outputToClient.writeUTF("Error: You must login to use this command \n");
                        }
                    } 
                    else if (message.equalsIgnoreCase("LOGOUT")) {
                        if (loggedIn) {
                            loggedIn = false;
                            rootLogin = false;
                            userLogged = null;
                            
                            outputToClient.writeUTF("200 OK \n");
                            
                            socket.close();
                        }
                        else {
                            outputToClient.writeUTF("Error: You must login to use this command \n");
                        }
                    } 
                    else {
                        outputToClient.writeUTF("300 invalid command \n");
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void handleUserFile(String user) throws IOException {
        solutionFile = new File(FILES_PATH + user + "_solutions.txt");

        if (!Files.exists(Path.of(FILES_PATH))) {
            Files.createDirectory(Path.of(FILES_PATH));
        }
        
        solutionFile.createNewFile();
    }

    private static List<String> GetFileLines(String user) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(FILES_PATH + user + "_solutions.txt"));
        
        return lines;
    }

    private static String listToString(List<String> interactions, String user) {
        String temp;

        temp = user + ":\n";

        if (interactions.isEmpty()) {
            temp += "\t" + "No interactions yet";
        } 
        else {
            for (String interaction : interactions) {
                temp += "\t" + interaction + "\n";
            }

            temp = temp.substring(0, temp.length() - 2);
        }

        return temp;
    }

    private static void writeToFile(String str) throws IOException {
        try (FileWriter fileWriter = new FileWriter(FILES_PATH + userLogged + "_solutions.txt", true)) {
            fileWriter.write(str + "\n");
            
            fileWriter.close();
        }
    }
}
