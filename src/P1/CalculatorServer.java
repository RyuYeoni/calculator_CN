package P1;

/* 
Command message format from client to server: <OPERATOR> <OPERAND1> <OPERAND2>
Format of response message from server to client: "Answer: <RESULT>" or "Incorrect: <ERROR_MESSAGE>" 
*/

import java.io.*;
import java.net.*;
import java.util.*;

public class CalculatorServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null; // Declare and initialize ServerSocket

        try {
            int port = 1234; // Default port number

            // Read server information from a configuration file if available
            File serverinfoFile = new File("serverinfo.dat");
            if (serverinfoFile.exists()) {
                Scanner scanner = new Scanner(serverinfoFile);
                if (scanner.hasNextLine()) {
                    String[] serverInfo = scanner.nextLine().split(" ");
                    if (serverInfo.length == 2) {
                        String serverIP = serverInfo[0];
                        port = Integer.parseInt(serverInfo[1]);
                    }
                }
                scanner.close();
            }

            serverSocket = new ServerSocket(port); // Initialize ServerSocket
            System.out.println("Server listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept incoming client connections
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Create a new thread to handle the client
                Thread clientThread = new Thread(new MultiClientSupport(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close(); // Close the server socket
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
    * A class to support multiple clients by handling their requests in separate threads.
    */
   private static class MultiClientSupport implements Runnable {
       private Socket clientSocket;
   
       // Constructs a new MultiClientSupport instance for a given client socket.
       public MultiClientSupport(Socket clientSocket) {
           this.clientSocket = clientSocket;
       }
   
       // The run method that handles client requests.
       @Override
       public void run() {
           try (
               BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
               BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
           ) {
               while (true) {
                   String inputMessage = in.readLine(); // Read input from the client
                   if (inputMessage == null || inputMessage.equalsIgnoreCase("stop")) {
                       System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                       break; // Exit the loop if the client sends "stop" or disconnects
                   }
   
                   // Parse and evaluate the expression
                   String response = calculateExpression(inputMessage); // Calculate the result
                   out.write(response + "\n"); // Send the response back to the client
                   out.flush(); // Flush the output
               }
           } catch (IOException e) {
               e.printStackTrace();
           } finally {
               try {
                   clientSocket.close(); // Close the client socket
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
   }
   

/**
 * Calculate the result of a mathematical expression provided as a string.
 *
 * @param expression A string containing the mathematical expression in the format "Operator Operand1 Operand2".
 *                   - Operator: The mathematical operation to perform (ADD, SUB, MUL, DIV).
 *                   - Operand1: The first numerical operand for the operation.
 *                   - Operand2: The second numerical operand for the operation.
 * @return A string representing the result of the calculation or an error message.
 */
    private static String calculateExpression(String expression) {
        String[] tokens = expression.split(" "); // Split the input expression into tokens
        if (tokens.length < 3) {
            return "Incorrect: Invalid expression. There should be 3 arguments.(e.g., ADD 10 20)";
        }

        try {
            double operand1 = Double.parseDouble(tokens[1]); // Parse the first operand
            double operand2 = Double.parseDouble(tokens[2]); // Parse the second operand

            switch (tokens[0]) {
                case "ADD":
                    if (tokens.length > 3) {
                        return "Incorrect: Too many arguments";
                    }
                    return "Answer: "+ operand1 + " + " + operand2 + " = " + (operand1 + operand2); // Perform addition
                case "SUB":
                    if (tokens.length > 3) {
                        return "Incorrect: Too many arguments";
                    }
                    return "Answer: "+ operand1 + " - " + operand2 + " = " + (operand1 - operand2); // Perform subtraction
                case "MUL":
                    if (tokens.length > 3) {
                        return "Incorrect: Too many arguments";
                    }
                    return "Answer: "+ operand1 + " * " + operand2 + " = " + (operand1 * operand2); // Perform multiplication
                case "DIV":
                    if (tokens.length > 3) {
                        return "Incorrect: Too many arguments";
                    }
                    if (operand2 == 0) {
                        return "Incorrect: Division by zero";
                    }
                    return "Answer: "+ operand1 + " / " + operand2 + " = " + ((double) operand1 / operand2); // Perform division
                default:
                    return "Incorrect: Invalid operation. Operator must be ADD/SUB/MUL/DIV.";
            }
        } catch (NumberFormatException e) {
            return "Incorrect: Invalid operands. The input format is <OPERATOR> <OPERAND1> <OPERAND2>.(e.g., ADD 10 20)";
        }
    }
}
