import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 8080; // Change this to the server's port
    private static final int CACHE_SIZE = 10; // Adjust cache size as needed
    private static final Map<String, CacheEntry> cache = new HashMap<>();

    public static void main(String[] args) {
        DatagramSocket socket = null;
        Scanner scanner = new Scanner(System.in);
        Request request = null;
        Reply response = null;
        Random random = new Random();
        boolean randomBoolean = false;
        DatagramPacket requestPacket = null;
        DatagramPacket responsePacket = null;
        int t1 = (int) System.currentTimeMillis();
        //CacheEntry example = new CacheEntry("helloyyyyyyy", t1, t1-20000);
        //cache.put("file.txt", example);
        byte[] responseData = null;
        byte[] buffer = null;
        byte[] marshalledRequestData = null;
        int requestId = 1;
        int operation = 0;
        int interval = 0;
        String userInput = String.valueOf('0');
        int currentTime;
        String filename = null;
        int offset = 0;
        int bytesToReadFrom = 0;


        try {
            while (true) {

                // duplicate previous request
                currentTime = (int) System.currentTimeMillis();

                if (requestId > 1 && randomBoolean && request.getOperation() >= 1 && request.getOperation() <= 5){
                    System.out.println("Request timeout, sending the request again.");
                    // Send same request packet as before
                    socket.send(requestPacket);

                    buffer = new byte[1024];
                    responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    long startTime = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - startTime) < 5000) {
                        socket.receive(responsePacket);

                        System.out.println("Received response from Server");
                        response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                        // Read the file content
                        if (request.getOperation() == 1) {
                            System.out.println("Content: " + readFileContent(response.getContent(), offset, bytesToReadFrom));
                        }
                        else{
                            System.out.println("Content: " + response.getContent());

                        }
                        // cache read content
                        CacheEntry entry = new CacheEntry(response.getContent(), response.getModifiedTime(), currentTime);

                        cache.put(filename, entry);

                        System.out.println("Successfully cached file!");
                        break;
                    }
//                    System.out.print("Successfully executed duplicate requests !");
                }
                randomBoolean = random.nextBoolean();

                System.out.print("Enter request (or type 'quit' to exit): ");
                userInput = scanner.nextLine();


                if (userInput.equalsIgnoreCase("quit")) {
                    break; // Exit loop if user types 'quit'
                }
                InetAddress serverAddress = InetAddress.getByName("10.91.230.147"); // Change this to the server's IP address
                socket = new DatagramSocket();




                if (userInput.equals("1")) {

                    operation = 1;
                    System.out.print("Enter read request filename: ");
                    filename = scanner.nextLine();
                    System.out.print("Enter read request offset: ");
                    offset = scanner.nextInt();
                    System.out.print("Enter number of bytes to read from: ");
                    bytesToReadFrom = scanner.nextInt();
                    scanner.nextLine();

                    // if file is already in cache
                    if (cache.containsKey(filename)) {
                        System.out.print("Checking if file already cached...");
                        // cache validity check
                        if (cache.get(filename).validityCheck()){
                            System.out.println("Cached file is still valid!");
                            String content = cache.get(filename).getContent();
                            System.out.println("Requested read content: " +readFileContent(content, offset, bytesToReadFrom));
                            continue;
                        }

                        // cacheEntry no longer valid, send getAttri for lastModified at Server
                        else{
                            System.out.println("Cached file invalid, requesting for last modified from server...");
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

                            // timeout
                            long startTime = System.currentTimeMillis();
                            while ((System.currentTimeMillis() - startTime) < 5000) {
                                if (!randomBoolean) {

                                    socket.receive(responsePacket);

                                    System.out.println("Received response from Server");
                                    // Unmarshal response object
                                    response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                                    int lastModifiedServer = response.getModifiedTime();

                                    // if lastModified matches
                                    if (cache.get(filename).validityModifiedCheck(lastModifiedServer)) {
                                        String content = cache.get(filename).getContent();
                                        System.out.print(readFileContent(content, offset, bytesToReadFrom));
                                        continue;
                                    }
                                    cache.remove(filename);
                                    // Invaldiate cache entry and make a new request
                                    System.out.println("Cache entry is invalid. Sending new request for file content.");
                                    break;
                                }
                            }

                        }

                    }

                    // Create request object
                    request = new Request(operation, filename, requestId, offset, bytesToReadFrom); // Example request

                    marshalledRequestData = MarshallerCaller.marshallRequest(request);

                    // Create UDP packet
                    assert marshalledRequestData != null;
                    requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                    buffer = new byte[1024];
                    responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    long startTime = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - startTime) < 5000){
                        if (!randomBoolean) {
                            socket.receive(responsePacket);

                            System.out.println("Received response from Server");
                            // Unmarshal response object
                            response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                            // Read the file content
                            System.out.println("Requested Read content: " + readFileContent(response.getContent(), offset, bytesToReadFrom));

                            // cache read content
                            CacheEntry entry = new CacheEntry(response.getContent(), response.getModifiedTime(), currentTime);

                            cache.put(filename, entry);

                            System.out.println("Successfully cached file!");
                            break;
                        }

                    }

                } else if (userInput.equals("2")) {

                    operation = 2;
                    System.out.print("Enter insert request filename: ");
                    filename = scanner.nextLine();
                    System.out.print("Enter insert request offset: ");
                    offset = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter content to write into file: ");
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
                    long startTime = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - startTime) < 5000) {
                        if (!randomBoolean) {
                            socket.receive(responsePacket);

                            System.out.println("Received response from Server");
                            // Unmarshal response object
                            response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                            // Process response
                            //                    System.out.print("RequestId: " + response.getRequestId());
                            //                    System.out.print("Status" + response.getStatus());
                            //                    System.out.print("Modified time: " + response.getModifiedTime());
                            System.out.println("Content: " + response.getContent());
                            break;
                        }
                    }


                } else if (userInput.equals("3")) {

                    operation = 3;
                    System.out.print("Enter monitor file request filename: ");
                    filename = scanner.nextLine();
                    System.out.print("Enter monitor interval: ");
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

                    // Loop until data is received or interval have passed
                    long startTime = System.currentTimeMillis();
                    while (true) {
                        // Calculate the remaining time until interval have passed
                        currentTime = (int)System.currentTimeMillis();
                        long elapsedTime = currentTime - startTime;
                        long remainingTime = interval - elapsedTime;

                        // If 30 seconds have passed, break out of the loop
                        if (elapsedTime >= interval) {
                            break;
                        }

                        // Set the timeout for the socket to the remaining time
                        socket.setSoTimeout((int) remainingTime);

                        try{
                            // Wait for data with the remaining time as the timeout
                            System.out.println("Monitoring Starts!");
                            socket.receive(responsePacket);
                            System.out.println("Received updates from Server!");
                            // Unmarshal response object
                            response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                            // Process response
//                            System.out.println("RequestId: " + response.getRequestId());
//                            System.out.println("Status" + response.getStatus());
//                            System.out.println("Modified time: " + response.getModifiedTime());
                            System.out.println("Content: " + response.getContent());

                            // Assume that the updated file is already in cache, Update cache
                            cache.remove(filename);
                            CacheEntry entry = new CacheEntry(response.getContent(), response.getModifiedTime(), currentTime);

                            cache.put(filename, entry);

                            System.out.println("Successfully updated "+ filename);

                        } catch (SocketTimeoutException e) {
                            System.err.println("Monitoring ends!");
                            randomBoolean = false;
                            break;
                        }

                    }
                    // if no updates, continue to next request
                    if (response == null){
                        System.out.println("Did not receive updates from server! Proceeding to next request.");
                        randomBoolean = false;
                        continue;
                    }


                } else if (userInput.equals("4")) {

                    operation = 4;
                    System.out.print("Enter list directory request filename: ");
                    filename = scanner.nextLine();

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
                    long startTime = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - startTime) < 5000) {
                        if (!randomBoolean) {
                            socket.receive(responsePacket);

                            System.out.println("Received response from Server");
                            // Unmarshal response object
                            response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                            // Process response
//                    System.out.print("RequestId: " + response.getRequestId());
//                    System.out.print("Status" + response.getStatus());
//                    System.out.print("Modified time: " + response.getModifiedTime());
                            System.out.println("Content: " + response.getContent());
                            break;
                        }
                    }
                } else if (userInput.equals("5")) {

                    operation = 5;
                    System.out.print("Enter delete request filename: ");
                    filename = scanner.nextLine();
                    System.out.print("Enter delete request offset: ");
                    offset = scanner.nextInt();
                    System.out.print("Enter number of bytes to delete from: ");
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
                    long startTime = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - startTime) < 5000) {
                        if (!randomBoolean) {
                            socket.receive(responsePacket);

                            System.out.println("Received response from Server");
                            // Unmarshal response object
                            response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                            // Process response
//                    System.out.print("requestId: " + response.getRequestId());
//                    System.out.print("Status: " + response.getStatus());
//                    System.out.print("Modified time: " + response.getModifiedTime());
                            System.out.println("Content: " + response.getContent());
                            break;
                        }
                    }
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

    private static String readFileContent(String content, int offset, int bytesToRead) {
        // Check if the input indices are valid
        if (offset < 0 || bytesToRead >= content.length() || offset > bytesToRead) {
            return ""; // Return an empty string if indices are invalid
        }

        // Return the substring from position i to position j (inclusive)
        return content.substring(offset, offset + bytesToRead);
    }
}
