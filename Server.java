import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class Server {
    private final int port;
    private ServerSocket serverSocket;


    // Stores connection timestamps
    private final List<LocalDateTime> connectedTimes = new ArrayList<>();
    
    // Simple handshake key
    private static final String KEY = "12345";

    public Server(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("Could not open server socket", e);
        }
    }

    // Accept a fixed number of clients
    public void serve(int numClients) {
        for (int i = 0; i < numClients; i++) {
            try {
                // Wait for connection
                final Socket client = serverSocket.accept();
                
                // Handle client in a new thread
                new Thread(() -> handleClient(client)).start();
            } catch (IOException e) {
                break;
            }
        }
    }
    
    private void handleClient(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String first = in.readLine();
            if (!KEY.equals(first)) {
                out.println("couldn't handshake");
                return;
            }

            synchronized (connectedTimes) {
                connectedTimes.add(LocalDateTime.now());
            }

            String line = in.readLine();
            if (line == null) return;

            int n;
            try {
                n = Integer.parseInt(line.trim());
            } catch (Exception ex) {
                out.println("There was an exception on the server");
                return;
            }

            int factors = countDivisors(n);
            out.println("The number " + n + " has " + factors + " factors");

        } catch (IOException e) {
            System.err.println("Client disconnected or I/O error: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    // Counts the number of positive divisors using a basic loop
    private int countDivisors(int n) {
        if (n == 0) return 0;
        n = Math.abs(n);

        int count = 0;
        for (int i = 1; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                count += (i == n / i) ? 1 : 2;
            }
        }
        return count;
    }
    
    // Returns sorted copy of connection timestamps
    public ArrayList<LocalDateTime> getConnectedTimes() { 
        synchronized (connectedTimes) {
            ArrayList<LocalDateTime> copy = new ArrayList<>(connectedTimes);
            Collections.sort(copy);
            return copy;
        }
    }

    // Close the server socket
    public void disconnect() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close(); 
        } catch (IOException ignored) {}
    }
}
