import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 8080; // Change this to the server's port
    private static final int FRESHNESS_INTERVAL = 5;
    private static final int CACHE_SIZE = 10; // Adjust cache size as needed
    private static final Map<String, CacheEntry> cache = new HashMap<>();

    public static void main(String[] args) {
        DatagramSocket socket = null;
        Scanner scanner = new Scanner(System.in);
        Request request = null;
        Reply response = null;
        DatagramPacket requestPacket = null;
        DatagramPacket responsePacket = null;
        byte[] responseData = null;
        byte[] buffer = null;
        byte[] marshalledRequestData = null;
        int requestId = 1;
        int operation = 0;
        int interval = 0;

        try {
            while (true) {

                System.out.print("Enter request (or type 'quit' to exit): ");
                String userInput = scanner.nextLine();


                if (userInput.equalsIgnoreCase("quit")) {
                    break; // Exit loop if user types 'quit'
                }
                InetAddress serverAddress = InetAddress.getByName("10.91.187.225"); // Change this to the server's IP address
                socket = new DatagramSocket();


                if (userInput.equals("1")) {

                    operation = 1;
                    System.out.println("Enter read request filename: ");
                    String filename = scanner.nextLine();
                    System.out.println("Enter read request offset: ");
                    int offset = scanner.nextInt();
                    System.out.println("Enter number of bytes to read from: ");
                    int bytesToReadFrom = scanner.nextInt();
                    scanner.nextLine();

                    // if file is already in cache
                    if (cache.containsKey(filename)) {
                        // cache validity check
                        if (cache.get(filename).validityCheck()){
                            String content = cache.get(filename).getContent();
                            System.out.println(getCacheContent(content, offset, bytesToReadFrom));
                            continue;
                        }

                        // cacheEntry no longer valid, send getAttri for lastModified at Server
                        else{
                            operation = 6;
                            request = new Request(operation, requestId, filename); // Example request

                            marshalledRequestData = MarshallerCaller.marshallRequest(request);

                            // Create UDP packet
                            assert marshalledRequestData != null;
                            requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                            // Send request packet
                            socket.send(requestPacket);

                            buffer = new byte[1024];
                            responsePacket = new DatagramPacket(buffer, buffer.length);

                            // Loop until data is received or 30 seconds have passed
                            long startTime = System.currentTimeMillis();
                            while (true) {
                                // Calculate the remaining time until 30 seconds have passed
                                long currentTime = System.currentTimeMillis();
                                long elapsedTime = currentTime - startTime;
                                long remainingTime = 30000 - elapsedTime;

                                // If 30 seconds have passed, break out of the loop
                                if (elapsedTime >= 30000) {
                                    break;
                                }

                                // Set the timeout for the socket to the remaining time
                                socket.setSoTimeout((int) remainingTime);

                                // Wait for data with the remaining time as the timeout
                                socket.receive(responsePacket);
                                System.out.println("lastModified at server received!");
                                break; // Data received, exit the loop
                            }

                            // Unmarshal response object
                            response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                            // Process response
                            System.out.println("Response from server: " + response.getRequestId());
                            System.out.println("Response from server: " + response.getStatus());
                            System.out.println("Response from server: " + response.getModifiedTime());
                            System.out.println("Response from server: " + response.getContent());

                        }


                    }
                    else {
                        // Create request object
                        request = new Request(operation, filename, requestId, offset, bytesToReadFrom); // Example request

                    }

                    marshalledRequestData = MarshallerCaller.marshallRequest(request);

                    // Create UDP packet
                    assert marshalledRequestData != null;
                    requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                    buffer = new byte[1024];
                     responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    socket.receive(responsePacket);

                    System.out.println("Received response from Server");
                    // Unmarshal response object
                    response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                    // Process response
                    System.out.println("Response from server: " + response.getRequestId());
                    System.out.println("Response from server: " + response.getStatus());
                    System.out.println("Response from server: " + response.getModifiedTime());
                    System.out.println("Response from server: " + response.getContent());


                } else if (userInput.equals("2")) {

                    operation = 2;
                    System.out.println("Enter insert request filename: ");
                    String filename = scanner.nextLine();
                    System.out.println("Enter insert request offset: ");
                    int offset = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter content to write into file: ");
                    String bytesToWrite = scanner.nextLine();

                    // Create request object
                    request = new Request(operation, filename, requestId, offset, bytesToWrite); // Example request

                     marshalledRequestData = MarshallerCaller.marshallRequest(request);

                    // Create UDP packet
                    assert marshalledRequestData != null;
                    requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                     buffer = new byte[1024];
                     responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    socket.receive(responsePacket);

                    System.out.println("Received response from Server");
                    // Unmarshal response object
                    response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                    // Process response
                    System.out.println("Response from server: " + response.getRequestId());
                    System.out.println("Response from server: " + response.getStatus());
                    System.out.println("Response from server: " + response.getModifiedTime());
                    System.out.println("Response from server: " + response.getContent());


                } else if (userInput.equals("3")) {

                    operation = 3;
                    System.out.println("Enter monitor file request filename: ");
                    String filename = scanner.nextLine();
                    System.out.println("Enter monitor interval: ");
                    interval = scanner.nextInt();
                    scanner.nextLine();

                    // Create request object
                    request = new Request(operation, filename, requestId, interval); // Example request

                    marshalledRequestData = MarshallerCaller.marshallRequest(request);

                    // Create UDP packet
                    assert marshalledRequestData != null;
                    requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                     buffer = new byte[1024];
                     responsePacket = new DatagramPacket(buffer, buffer.length);

                    // Loop until data is received or 30 seconds have passed
                    long startTime = System.currentTimeMillis();
                    while (true) {
                        // Calculate the remaining time until 30 seconds have passed
                        long currentTime = System.currentTimeMillis();
                        long elapsedTime = currentTime - startTime;
                        long remainingTime = 30000 - elapsedTime;

                        // If 30 seconds have passed, break out of the loop
                        if (elapsedTime >= 30000) {
                            break;
                        }

                        // Set the timeout for the socket to the remaining time
                        socket.setSoTimeout((int) remainingTime);

                        try{
                            // Wait for data with the remaining time as the timeout
                            socket.receive(responsePacket);
                            System.out.println("lastModified at server received!");
                            break; // Data received, exit the loop
                        } catch (IOException e) {
                            System.err.println("Waiting for data...");
                        }
                    }

                    // if no updates, continue to next request
                    if (response == null){
                        continue;
                    }
                    // Unmarshal response object
                    response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                    // Process response
                    System.out.println("Response from server: " + response.getRequestId());
                    System.out.println("Response from server: " + response.getStatus());
                    System.out.println("Response from server: " + response.getModifiedTime());
                    System.out.println("Response from server: " + response.getContent());

                } else if (userInput.equals("4")) {

                    operation = 4;
                    System.out.println("Enter list directory request filename: ");
                    String filename = scanner.nextLine();

                    // Create request object
                    request = new Request(operation, filename, requestId); // Example request

                    // Marshal request object
                    //assert request != null;
                    marshalledRequestData = MarshallerCaller.marshallRequest(request);

                    // Create UDP packet
                    assert marshalledRequestData != null;
                    requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                     buffer = new byte[1024];
                     responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    socket.receive(responsePacket);

                    System.out.println("Received response from Server");
                    // Unmarshal response object
                    response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                    // Process response
                    System.out.println("Response from server: " + response.getRequestId());
                    System.out.println("Response from server: " + response.getStatus());
                    System.out.println("Response from server: " + response.getModifiedTime());
                    System.out.println("Response from server: " + response.getContent());

                } else if (userInput.equals("5")) {

                    operation = 5;
                    System.out.println("Enter delete request filename: ");
                    String filename = scanner.nextLine();
                    System.out.println("Enter delete request offset: ");
                    int offset = scanner.nextInt();
                    System.out.println("Enter number of bytes to delete from: ");
                    int bytesToDelete = scanner.nextInt();

                    // Create request object
                    request = new Request(operation, filename, requestId, offset, bytesToDelete, true); // Example request

                     marshalledRequestData = MarshallerCaller.marshallRequest(request);

                    // Create UDP packet
                    assert marshalledRequestData != null;
                    requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                     buffer = new byte[1024];
                     responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    socket.receive(responsePacket);

                    System.out.println("Received response from Server");
                    // Unmarshal response object
                    response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                    // Process response
                    System.out.println("Response from server: " + response.getRequestId());
                    System.out.println("Response from server: " + response.getStatus());
                    System.out.println("Response from server: " + response.getModifiedTime());
                    System.out.println("Response from server: " + response.getContent());
                }

                requestId++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

//    private static Response unmarshallResponse(byte[] data) {
//        // Custom unmarshalling logic
//        String responseData = new String(data, StandardCharsets.UTF_8).trim();
//        return new Response(responseData);
//    }

    private static String getCacheContent(String content, int offset, int bytesToRead) {
        // Check if the input indices are valid
        if (offset < 0 || bytesToRead >= content.length() || offset > bytesToRead) {
            return ""; // Return an empty string if indices are invalid
        }

        // Return the substring from position i to position j (inclusive)
        return content.substring(offset, bytesToRead + 1);
    }
}
