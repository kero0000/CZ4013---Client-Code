import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

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
        boolean isResend = false;
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
            InetAddress ip = InetAddress.getLocalHost();
            String ipAddress = ip.getHostAddress();
            System.out.println("Your current IP address : " + ipAddress);
            String[] octets = ipAddress.split("\\.");
            int lastThreeOctets = Integer.parseInt(octets[1] + octets[2] + octets[3]);
            requestId += lastThreeOctets;
            System.out.println("Your intial Request ID : " + requestId);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        try {
            while (true) {

                // duplicate previous request
                currentTime = (int) System.currentTimeMillis();

                if (requestId > 1 && isResend){
                    System.out.println("Request timeout, sending the request again.");
                    // Send same request packet as before
                    socket.send(requestPacket);

                    buffer = new byte[1024];
                    responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    // Set the timeout for the socket to the remaining time
                    socket.setSoTimeout((int) 5000);

                    try {
                        socket.receive(responsePacket);

                        System.out.println("Received response from Server");
                        response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                        // Read the file content
                        if (request.getOperation() == 1) {
                            if (response.getStatus()==1){
                                System.out.println("Content: " +  readFileContent(response.getContent(), offset, bytesToReadFrom));
                                // cache read content
                                // cache.remove(filename);
                                CacheEntry entry = new CacheEntry(response.getContent(), response.getModifiedTime(), currentTime);

                                cache.put(filename, entry);

                                System.out.println("Successfully cached file!");
                            }
                            else{
                                System.out.println(response.getContent());
                            }
                        }
                        else if (request.getOperation() == 4){
                            if (response.getStatus()==1){
                                System.out.println("Content: " + response.getContent());
                            }
                            else{
                                System.out.println(response.getContent());
                            }



                        }
                        else{
                            if (response.getStatus()==1){
                                // cache read content
                                cache.remove(filename);
                                CacheEntry entry = new CacheEntry(response.getContent(), response.getModifiedTime(), currentTime);

                                cache.put(filename, entry);

                                System.out.println("Successfully cached file!");
                            }
                            else{
                                System.out.println(response.getContent());
                            }

                        }

                        isResend = false;

                    }
                    catch (Exception e){
                        continue;
                    }
//                    System.out.print("Successfully executed duplicate requests !");
                }

                System.out.print("Enter request (or type 'quit' to exit): ");
                userInput = scanner.nextLine();


                if (userInput.equalsIgnoreCase("quit")) {
                    break; // Exit loop if user types 'quit'
                }
                InetAddress serverAddress = InetAddress.getByName("127.0.0.1"); // Change this to the server's IP address
                socket = new DatagramSocket();


                if (userInput.equals("1") || userInput.equals("2") || userInput.equals("3") || userInput.equals("4") || userInput.equals("5")){
                    isResend = true;
                }
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
                            System.out.println("Content: " + readFileContent(content, offset, bytesToReadFrom));
                            isResend = false;
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



                            socket.receive(responsePacket);

                            System.out.println("Received response from Server");
                            // Unmarshal response object
                            response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                            int lastModifiedServer = response.getModifiedTime();

                            // if lastModified matches
                            if (cache.get(filename).validityModifiedCheck(lastModifiedServer)) {
                                String content = cache.get(filename).getContent();
                                System.out.println("Content from cache: " + readFileContent(content, offset, bytesToReadFrom));
                                isResend = false;
                                continue;
                            }
                            cache.remove(filename);
                            // Invaldiate cache entry and make a new request
                            System.out.println("Cache entry is invalid. Sending new request for file content.");
                            operation = 1;



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

                    socket.setSoTimeout((int) 5000);

                    while(true){
                        try {
                            socket.receive(responsePacket);
    
                            if (!isResend) {
    
                                System.out.println("Received response from Server");
                                // Unmarshal response object
                                response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());
                                if (response.getStatus() == 1) {
                                    // Read the file content
                                    System.out.println("Requested Read content: " + readFileContent(response.getContent(), offset, bytesToReadFrom));
    
                                    // cache read content
                                    CacheEntry entry = new CacheEntry(response.getContent(), response.getModifiedTime(), currentTime);
    
                                    cache.put(filename, entry);
    
                                    System.out.println("Successfully cached file!");
                                } else {
                                    System.out.println(response.getContent());
                                }
    
                                break;
                            }
                        }catch(Exception e){
                            isResend = true;
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

                    socket.setSoTimeout((int) 5000);

                    while(true){
                        try {
                            socket.receive(responsePacket);
    
                            if (!isResend) {
    
                                System.out.println("Received response from Server");
                                // Unmarshal response object
                                response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());
    
                                if (response.getStatus() == 1) {

                                    // cache read content
                                    cache.remove(filename);
                                    CacheEntry entry = new CacheEntry(response.getContent(), response.getModifiedTime(), currentTime);
    
                                    cache.put(filename, entry);
    
                                    System.out.println("Successfully cached file!");
                                } else {
                                    System.out.println(response.getContent());
                                }
                                break;
                            }
                        }catch(Exception e){
                            isResend = true;
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
                            System.out.println("Receiving Updates...");
                            socket.receive(responsePacket);
                            System.out.println("Received updates from Server!");
                            // Unmarshal response object
                            response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                            // Process response
//                            System.out.println("RequestId: " + response.getRequestId());
//                            System.out.println("Status" + response.getStatus());
//                            System.out.println("Modified time: " + response.getModifiedTime());

                            if (response.getStatus()==1){
                                // Read the file content
                                System.out.println("Content: " + response.getContent());

                                // cache read content
                                cache.remove(filename);
                                CacheEntry entry = new CacheEntry(response.getContent(), response.getModifiedTime(), currentTime);

                                cache.put(filename, entry);

                                System.out.println("Successfully updated "+ filename);                            }
                            else{
                                System.out.println(response.getContent());
                                isResend = false;
                                break;
                            }



                        } catch (SocketTimeoutException e) {
                            System.err.println("Monitoring Ended!");
                            isResend = false;
                            break;
                        }

                    }
                    // if no updates, continue to next request
                    if (response == null){
                        System.out.println("Did not receive updates from server! Proceeding to next request.");
                        isResend = false;
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


                    // Set the timeout for the socket to the remaining time
                    socket.setSoTimeout((int) 5000);
                    while(true){
                        try {
                            socket.receive(responsePacket);
    
                            if (!isResend) {
    
                                System.out.println("Received response from Server");
                                // Unmarshal response object
                                response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());
    
    
                                if (response.getStatus() == 1) {
                                    // Read the file content
                                    System.out.println("Content: " + response.getContent());
    
                                } else {
                                    System.out.println(response.getContent());
                                }
                                break;
                            }
                        } catch (Exception e){
                            isResend = true;
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
                    scanner.nextLine();

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
                    socket.setSoTimeout((int) 5000);

                    while(true){
                        try {
                            socket.receive(responsePacket);
    
                            if (!isResend) {
    
                                System.out.println("Received response from Server");
                                // Unmarshal response object
                                response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());
    
                                if (response.getStatus() == 1) {
    
                                    // cache read content
                                    cache.remove(filename);
                                    CacheEntry entry = new CacheEntry(response.getContent(), response.getModifiedTime(), currentTime);
    
                                    cache.put(filename, entry);
    
                                    System.out.println("Successfully cached file!");
                                } else {
                                    System.out.println(response.getContent());
                                }
                                break;
                            }
                        }catch(Exception e){
                            isResend = true;
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
