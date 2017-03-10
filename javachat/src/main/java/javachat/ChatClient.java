package javachat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {

    private final InetAddress host;
    private final int port;
    private boolean connected;

    private ChatClient(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    private void startClient() {
        Socket socket = null;

        try {
            socket = new Socket(host, port);
            connected = true;

            Thread reader = new Thread(new SocketReader(socket));
            Thread writer = new Thread(new SocketWriter(socket));

            reader.start();
            writer.start();

            reader.join();
            writer.join();

        } catch (IOException ioe) {
            raiseError("Fatal error: " + ioe.getMessage());
        } catch (InterruptedException ie) {
            System.out.println("InterruptedException raised");
        } finally {
            if (socket != null) {
                try { socket.close(); }
                catch (IOException ignored) {}
            }
        }
    }

    private class SocketReader implements Runnable {
        final Socket socket;

        SocketReader(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            BufferedReader in = null;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    String line;
                    if ((line = in.readLine()) == null) {
                        connected = false; break;
                    }
                    System.out.println("Received message: " + line);
                }
            } catch (IOException ioe) {
                raiseError("Fatal error: " + ioe.getMessage());
            } finally {
                if (in != null) {
                    try { in.close(); }
                    catch (IOException ignored) {}
                }
            }
        }
    }

    private class SocketWriter implements Runnable {
        final Socket socket;

        SocketWriter(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            PrintWriter out = null;
            Scanner sc = null;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                sc = new Scanner(System.in);

                while (connected) {
                    out.println(sc.nextLine());
                }
                System.out.println("connection closed");
            } catch (IOException ioe) {
                raiseError("Fatal error: " + ioe.getMessage());
            } finally {
                if (out != null) {
                    out.close();
                }
                if (sc != null) {
                    sc.close();
                }
            }
        }
    }

    public static void main(String[] args) {
        InetAddress host = null;
        int port = 0;
        try {
            host = InetAddress.getByName(args[0]);
            port = Integer.parseInt(args[1]);
        } catch (UnknownHostException | RuntimeException ex) {
            raiseError("Incorrect host or port argument. Usage: ./program " +
                    "[host] [port_number]");
        }
        new ChatClient(host, port).startClient();
    }

    private static void raiseError(String errorMsg) {
        System.out.println(errorMsg);
        System.exit(-1);
    }
}
