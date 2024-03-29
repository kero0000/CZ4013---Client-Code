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
        DatagramPacket requestPacket = null;
        DatagramPacket responsePacket = null;
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

                        // cacheEntry no longer valid,
                        else{
                            operation = 6;
                            request = new Request(operation, requestId, filename); // Example request

                        }


                    }
                    else {
                        // Create request object
                        request = new Request(operation, filename, requestId, offset, bytesToReadFrom); // Example request

                    }

                    marshalledRequestData = MarshallerCaller.marshallRequest(request);

                    if (marshalledRequestData == null) {
                        System.out.println(("JX fault"));
                    }
                    // Create UDP packet
                     requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                    buffer = new byte[1024];
                     responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    socket.receive(responsePacket);


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

                    if (marshalledRequestData == null) {
                        System.out.println(("JX fault"));
                    }
                    // Create UDP packet
                     requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                     buffer = new byte[1024];
                     responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    socket.receive(responsePacket);


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

                    if (marshalledRequestData == null) {
                        System.out.println(("JX fault"));
                    }
                    // Create UDP packet
                     requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                     buffer = new byte[1024];
                     responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    socket.receive(responsePacket);

                } else if (userInput.equals("4")) {

                    operation = 4;
                    System.out.println("Enter list directory request filename: ");
                    String filename = scanner.nextLine();

                    // Create request object
                    request = new Request(operation, filename, requestId); // Example request

                    // Marshal request object
                    //assert request != null;
                    marshalledRequestData = MarshallerCaller.marshallRequest(request);

                    if (marshalledRequestData == null) {
                        System.out.println(("JX fault"));
                    }
                    // Create UDP packet
                    requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                     buffer = new byte[1024];
                     responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    socket.receive(responsePacket);

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

                    if (marshalledRequestData == null) {
                        System.out.println(("JX fault"));
                    }
                    // Create UDP packet
                    requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                    // Send request packet
                    socket.send(requestPacket);

                     buffer = new byte[1024];
                     responsePacket = new DatagramPacket(buffer, buffer.length);

                    // timeout
                    socket.receive(responsePacket);
                }


                byte[] responseData = responsePacket.getData();
                for (byte b: responseData){
                    System.out.print(b + ",");
                };
                System.out.println(" ");


                // Unmarshal response object
                Reply response = UnmarshallerCaller.unmarshallReply(responsePacket.getData());

                //Response response = unmarshallResponse(responsePacket.getData());
                //System.out.println("waiting for response");

                // Process response
                System.out.println("Response from server: " + response.getRequestId());
                System.out.println("Response from server: " + response.getStatus());
                System.out.println("Response from server: " + response.getModifiedTime());
                System.out.println("Response from server: " + response.getContent());

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
