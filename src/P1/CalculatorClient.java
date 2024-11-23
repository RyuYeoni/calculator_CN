package P1;

/* 
Command message format from client to server: <OPERATOR> <OPERAND1> <OPERAND2>
Format of response message from server to client: "Answer: <RESULT>" or "Incorrect: <ERROR_MESSAGE>" 
*/

import java.io.*;
import java.net.*;
import java.util.*;

public class CalculatorClient {
    public static void main(String[] args) {
        try {
            String serverIP = "localhost"; // Default server IP
            int port = 1234; // Default port number

            // Read server information from a file if available
            File serverinfoFile = new File("serverinfo.dat");
            if (serverinfoFile.exists()) {
                Scanner scanner = new Scanner(serverinfoFile);
                if (scanner.hasNextLine()) {
                    String[] serverInfo = scanner.nextLine().split(" ");
                    if (serverInfo.length == 2) {
                        serverIP = serverInfo[0];
                        port = Integer.parseInt(serverInfo[1]);
                    }
                }
                scanner.close();
            }

            // Establish a socket connection to the server
            Socket clientSocket = new Socket(serverIP, port);
            System.out.println("Connected to server! ServerIP: " + serverIP + ", port: " + port);

            try (
                // Create input and output streams for communication with the server
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                Scanner scanner = new Scanner(System.in) // Create a scanner for user input
            ) {
                while (true) {
                    System.out.print("-------------------------------------------------------------------------------\n");
                    System.out.print("Enter an expression. If you want to stop, type 'stop'.\n");
                    System.out.print("The input format is operator(ADD/SUB/MUL/DIV) number number. (e.g., ADD 10 20): ");

                    // Read user input from the console
                    String input = scanner.nextLine();

                    // Send user input to the server
                    out.write(input + "\n");
                    out.flush();

                    // Check if the user wants to stop
                    if (input.equalsIgnoreCase("stop")) {
                        break; // Exit the loop if the user wants to stop
                    }

                    // Read and display the server's response
                    String response = in.readLine();
                    System.out.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle any IO exceptions that might occur
            } finally {
                try {
                    clientSocket.close(); // Close the client socket
                } catch (IOException e) {
                    e.printStackTrace(); // Handle any socket closure exceptions
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle any initial setup or connection exceptions
        }
    }
}
