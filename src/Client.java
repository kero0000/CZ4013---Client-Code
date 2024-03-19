import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 3000; // Change this to the server's port
    private static final int FRESHNESS_INTERVAL = 5;
    private static final int CACHE_SIZE = 10; // Adjust cache size as needed
    private static Map<CacheKey, CacheEntry> cache = new HashMap<>();

    public static void main(String[] args) {
        DatagramSocket socket = null;
        Scanner scanner = new Scanner(System.in);
        Request request = null;
        int requestId = 1;

        try {
            while (true) {

                System.out.print("Enter request (or type 'quit' to exit): ");
                String userInput = scanner.nextLine();

                if (userInput.equalsIgnoreCase("quit")) {
                    break; // Exit loop if user types 'quit'
                }
                InetAddress serverAddress = InetAddress.getByName("localhost"); // Change this to the server's IP address
                socket = new DatagramSocket();


                if (userInput.equals("1")){

                    int operation = 1;
                    System.out.println("Enter read request filename: ");
                    String filename = scanner.nextLine();
                    System.out.println("Enter read request offset: ");
                    int offset = scanner.nextInt();
                    System.out.println("Enter number of bytes to read from: ");
                    int bytesToReadFrom = scanner.nextInt();

                    // Create request object
                    request = new Request(operation, filename, requestId, offset, bytesToReadFrom); // Example request
                }

                else if (userInput.equals("2")){

                    int operation = 2;
                    System.out.println("Enter insert request filename: ");
                    String filename = scanner.nextLine();
                    System.out.println("Enter insert request offset: ");
                    int offset = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter content to write into file: ");
                    String bytesToWrite = scanner.nextLine();

                    // Create request object
                    request = new Request(operation, filename, requestId, offset, bytesToWrite); // Example request
                }

                else if (userInput.equals("3")){

                    int operation = 3;
                    System.out.println("Enter monitor file request filename: ");
                    String filename = scanner.nextLine();
                    System.out.println("Enter monitor interval: ");
                    int interval = scanner.nextInt();
                    scanner.nextLine();

                    // Create request object
                    request = new Request(operation, filename, requestId, interval); // Example request
                }

                else if (userInput.equals("4")){

                    int operation = 4;
                    System.out.println("Enter list directory request filename: ");
                    String filename = scanner.nextLine();

                    // Create request object
                    request = new Request(operation, filename, requestId); // Example request
                }

                else if (userInput.equals("5")){

                    int operation = 5;
                    System.out.println("Enter delete request filename: ");
                    String filename = scanner.nextLine();
                    System.out.println("Enter delete request offset: ");
                    int offset = scanner.nextInt();
                    System.out.println("Enter number of bytes to delete from: ");
                    int bytesToDelete = scanner.nextInt();

                    // Create request object
                    request = new Request(operation, filename, requestId, offset, bytesToDelete, true); // Example request
                }

                else if (userInput.equals("6")){

                    int operation = 3;
                    System.out.println("Enter getAttri request filename: ");
                    String filename = scanner.nextLine();
                    System.out.println("Enter monitor interval: ");
                    int interval = scanner.nextInt();
                    scanner.nextLine();

                    // Create request object
                    request = new Request(operation, filename, requestId); // Example request
                }

                // Marshal request object
                //assert request != null;
                byte[] marshalledRequestData = MarshallerCaller.marshallRequest(request);

                if (marshalledRequestData==null){
                    System.out.println(("JX fault"));
                }
                // Create UDP packet
                DatagramPacket requestPacket = new DatagramPacket(marshalledRequestData, marshalledRequestData.length, serverAddress, SERVER_PORT);

                // Send request packet
                socket.send(requestPacket);

                // Receive response from server
                byte[] responseData = new byte[1024]; // Adjust buffer size as needed
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
                socket.receive(responsePacket);

                // Unmarshal response object
                Response response = unmarshallResponse(responsePacket.getData());

                // Process response
                System.out.println("Response from server: " + response.getMessage());

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

    private static byte[] marshallRequest(Request request) {
        String marshalledData = null;
        // Custom marshalling logic
        try {
            if (request.getOperation() == 1) {
                marshalledData = request.getOperation() + ";" + request.getFilename() + ";" + request.getrequestId() + request.getOffset() + ";" + request.getBytesToReadFrom();
            } else if (request.getOperation() == 2) {
                marshalledData = request.getOperation() + ";" + request.getFilename() + ";" + request.getrequestId() + request.getOffset() + ";" + request.getBytesToWrite();
            } else if (request.getOperation() == 3) {
                marshalledData = request.getOperation() + ";" + request.getFilename() + ";" + request.getrequestId() + request.getInterval();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        byte[] c = marshalledData.getBytes();
        return marshalledData.getBytes();
    }

    private static Response unmarshallResponse(byte[] data) {
        // Custom unmarshalling logic
        String responseData = new String(data, StandardCharsets.UTF_8).trim();
        return new Response(responseData);
    }

//    private Boolean cacheCheck(CacheEntry cacheEntry){
//
//    }
}
