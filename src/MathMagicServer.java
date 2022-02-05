
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author aaron
 */
public class MathMagicServer {

    private static final int SERVER_PORT = 9862;

    private static boolean rootLogin = false;

    private static boolean loggedIn = false;

    private static List<String> userList;

    private static String userLogged;

    private static File solFile;

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

                        if (loginInfo.matches(".+ .+")) {
                            boolean found = false;
                            for (String user : userList) {
                                if (user.equals(loginInfo)) {
                                    userLogged = user.substring(0, message.indexOf(" ") - 1);

                                    loggedIn = true;

                                    found = true;

                                    outputToClient.writeUTF("SUCCESS");

                                    break;
                                }
                            }

                            if (!found) {
                                outputToClient.writeUTF("FAILURE: Please provide a correct username and password. Try again.");
                            }
                        } else {
                            outputToClient.writeUTF("301 message format error");
                        }
                    } else if (command.equalsIgnoreCase("SOLVE")) {
                        if (loggedIn) {
                            String problem = message.substring(message.indexOf(" ") + 1, message.length());

                            if (problem.matches("^-c.*")) {
                                problem = problem.substring(problem.indexOf("c") + 1, problem.length());

                                if (problem.matches(" \\d*\\s*")) {
                                    double val = Double.parseDouble(problem);
                                    double circumference = 2.0 * Math.PI * val;
                                    double area = Math.PI * (val * val);

                                    String results = "Circle’s circumference is " + String.format("%.2f", circumference)
                                            + " and area is " + String.format("%.2f", area);

                                    outputToClient.writeUTF(results);

                                } else if (problem.isEmpty() || problem.matches("\\s*")) {
                                    outputToClient.writeUTF("Error: No radius found");
                                } else {
                                    outputToClient.writeUTF("301 message format error");
                                }
                            } else if (problem.matches("^-r.*")) {
                                problem = problem.substring(problem.indexOf("r") + 1, problem.length());

                                if (Pattern.matches(" \\d* \\d*\\s*", problem)) {
                                    problem = problem.substring(1, problem.length());

                                    double val1 = Double.parseDouble(problem.substring(0, problem.indexOf(" ")));
                                    double val2 = Double.parseDouble(problem.substring(problem.indexOf(" "), problem.length()));
                                    double perimeter = 2.0 * (val1 + val2);
                                    double area = val1 * val2;

                                    String results = "Rectangle’s perimeter is " + String.format("%.2f", perimeter)
                                            + " and area is " + String.format("%.2f", area);

                                    outputToClient.writeUTF(results);

                                } else if (Pattern.matches(" \\d*\\s*", problem)) {
                                    double val = Double.parseDouble(problem);
                                    double perimeter = 4.0 * val;
                                    double area = val * val;

                                    String results = "Rectangle’s perimeter is " + String.format("%.2f", perimeter)
                                            + " and area is " + String.format("%.2f", area);

                                    outputToClient.writeUTF(results);

                                } else if (problem.isEmpty() || problem.matches("\\s*")) {
                                    outputToClient.writeUTF("Error: No radius found");
                                } else {
                                    outputToClient.writeUTF("301 message format error");
                                }
                            } else {
                                outputToClient.writeUTF("301 message format error");
                            }

                            solFile = new File(userLogged + "_solutions.txt");

                            solFile.createNewFile();

                        } else {
                            outputToClient.writeUTF("Error: You must login to use this command");
                        }
                    } else if (command.equalsIgnoreCase("LIST")) {
                        outputToClient.writeUTF("LIST COMMAND");
                    } else {
                        outputToClient.writeUTF("300 invalid command");
                    }
                } else {
                    if (message.equalsIgnoreCase("LIST")) {
                        outputToClient.writeUTF("LIST COMMAND");
                    } else if (message.equalsIgnoreCase("SHUTDOWN")) {
                        outputToClient.writeUTF("SHUTDOWN COMMAND");
                    } else if (message.equalsIgnoreCase("LOGOUT")) {
                        outputToClient.writeUTF("LOGOUT COMMAND");
                    } else {
                        outputToClient.writeUTF("300 invalid command");
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
