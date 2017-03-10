package javachat;

import com.github.lalyos.jfiglet.FigletFont;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
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
        Socket tcpSocket = null;
        DatagramSocket udpSocket = null;

        try {
            tcpSocket = new Socket(host, port);
            udpSocket = new DatagramSocket(tcpSocket.getLocalPort());
            connected = true;

            Thread reader = new Thread(new SocketReader(tcpSocket));
            Thread writer = new Thread(new SocketWriter(tcpSocket, udpSocket));
            Thread udpReader = new Thread(new UdpSocketReader(udpSocket));

            reader.start();
            writer.start();
            udpReader.start();

            reader.join();
            writer.join();
            udpReader.join();

        } catch (IOException ioe) {
            raiseError("Fatal error: " + ioe.getMessage());
        } catch (InterruptedException ie) {
            System.out.println("InterruptedException raised");
        } finally {
            if (tcpSocket != null) {
                try { tcpSocket.close(); }
                catch (IOException ignored) {}
            }
            if (udpSocket != null) {
                udpSocket.close();
            }
        }
    }

    private class SocketReader implements Runnable {
        final Socket tcpSocket;

        SocketReader(Socket tcpSocket) {
            this.tcpSocket = tcpSocket;
        }

        public void run() {
            BufferedReader in = null;

            try {
                in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

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
        final Socket tcpSocket;
        final DatagramSocket udpSocket;

        SocketWriter(Socket tcpSocket, DatagramSocket udpSocket) {
            this.tcpSocket = tcpSocket;
            this.udpSocket = udpSocket;
        }

        public void run() {
            PrintWriter out = null;
            Scanner sc = null;
            try {
                InetAddress host = tcpSocket.getInetAddress();
                int port = tcpSocket.getPort();

                out = new PrintWriter(tcpSocket.getOutputStream(), true);
                sc = new Scanner(System.in);

                while (connected) {
                    String nextLine = sc.nextLine();
                    if (nextLine.startsWith("'M'")) {
                        sendUdpPacket(host, port, getAsciiArtText(nextLine.substring(3)).getBytes());
                    }
                    out.println(nextLine);
                }
                System.out.println("connection closed");
                System.exit(1);
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

        void sendUdpPacket(InetAddress host, int port, byte[] msg) throws IOException {
            DatagramPacket packet = new DatagramPacket(msg, msg.length, host, port);
            udpSocket.send(packet);
        }
    }

    private class UdpSocketReader implements Runnable {
        final DatagramSocket udpSocket;

        UdpSocketReader(DatagramSocket udpSocket) {
            this.udpSocket = udpSocket;
        }

        public void run() {
            byte[] receiveBuffer = new byte[1024];

            try {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    udpSocket.receive(packet);
                    System.out.println(new String(packet.getData()));
                }
            } catch (IOException ioe) {
                raiseError("Fatal error: " + ioe.getMessage());
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

    private static String getAsciiArtText(String text) {
        return FigletFont.convertOneLine(text);
    }
}
