import java.io.*;
import java.net.*;

public class Client {
    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String host, int port) {
        
        this.host = host;
        this.port = port;
       
        try {
           
            // open a socket to the server
            this.socket = new Socket(host, port);

            // input stream for reading server responses
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // output stream for sending messages
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            // fail if we cannot connect
            throw new RuntimeException("Could not connect client", e);
        }
    }
   
    // gives access to the underlying socket
    public Socket getSocket() {
        return socket;
    }

    // send handshake key to the server
    public void handshake() {
        out.println("12345");
    }

    // send a number to the server and read back the result
    public String request(String number) {
        try {
            out.println(number);
            return in.readLine();
        } catch (IOException e) {
            return "There was an exception on the server";
        }
    }

    // close the connection to the server
    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
            // ignore errors on close
    }
}
}