import java.net.*;
import java.nio.charset.StandardCharsets;

public class Server {

    private static final int SERVER_PORT = 3000; // Port number on which the server listens

    public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(SERVER_PORT);

            System.out.println("Server started. Listening on port " + SERVER_PORT + "...");

            while (true) {
                // Receive request from client
                byte[] requestBuffer = new byte[1024]; // Adjust buffer size as needed
                DatagramPacket requestPacket = new DatagramPacket(requestBuffer, requestBuffer.length);
                socket.receive(requestPacket);

                // Unmarshall request message
                Request request = unmarshallRequest(requestPacket.getData());

                // Process request (read file, insertion...)
                // String responseMessage = processRequest(request);
                String responseMessage = "response from server";
                Response response = new Response(responseMessage);

                // Marshall and Send response to client
                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();
                //byte[] responseBytes = responseMessage.getBytes();
                byte[] responseBytes = marshallResponse(response);
                DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort);
                socket.send(responsePacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }

    }

    private static String processRequest(Request request) {
        // Example of processing the request (you need to implement actual logic)
        // For simplicity, just echoing back the request
        return "Server received request: " + request;
    }

    public static byte[] marshallResponse(Response response) {
        // Custom marshalling logic
        String marshalledData = response.getMessage();
        return marshalledData.getBytes(StandardCharsets.UTF_8);
    }

    public static Request unmarshallRequest(byte[] data) {
        // Custom unmarshalling logic
        String requestData = new String(data, StandardCharsets.UTF_8);
        System.out.println("Client request: "+ requestData.trim());
        return new Request(1, requestData, 1);
    }

}
