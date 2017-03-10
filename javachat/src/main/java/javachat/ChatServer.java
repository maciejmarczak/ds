package javachat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(30);
    private final ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<>();
    private final int port;

    private ChatServer(int port) {
        this.port = port;
    }

    private void startServer() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            startUdpService();

            while (true) {
                Client nextClient = new Client(serverSocket.accept());
                clients.put(nextClient.getUniqueName(), nextClient);
                threadPool.execute(nextClient);
            }

        } catch (IOException ioe) {
            raiseError("Fatal error: " + ioe.getMessage());
        } finally {
            if (serverSocket != null) {
                try { serverSocket.close(); }
                catch (IOException ignored) {}
            }
        }
    }

    private void startUdpService() {
        new Thread(() -> {
            DatagramSocket udpSocket = null;
            try {
                udpSocket = new DatagramSocket(port);
                byte[] receiveBuffer = new byte[1024];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    udpSocket.receive(packet);
                    String clientId = packet.getAddress().getHostAddress() + ":" + packet.getPort();
                    sendUdpToAllOtherClients(packet.getData(), clientId);
                }
            } catch (IOException ioe) {
                raiseError("Fatal error: " + ioe.getMessage());
            }

        }).start();
    }

    private void sendToAllOtherClients(String msg, String currentClient) {
        for (Map.Entry<String, Client> clientEntry : clients.entrySet()) {
            if (!clientEntry.getKey().equals(currentClient)) {
                clientEntry.getValue().sendMsg(msg);
            }
        }
    }

    private void sendUdpToAllOtherClients(byte[] msg, String currentClient) {
        for (Map.Entry<String, Client> clientEntry: clients.entrySet()) {
            if (!clientEntry.getKey().equals(currentClient)) {
                clientEntry.getValue().sendUdpMsg(msg);
            }
        }
    }

    private class Client implements Runnable {
        final Socket tcpSocket;
        ConcurrentLinkedQueue<String> toSend = new ConcurrentLinkedQueue<>();
        final String uniqueName;

        Client(Socket tcpSocket) {
            this.tcpSocket = tcpSocket;
            this.uniqueName = tcpSocket.getInetAddress().getHostAddress() + ":" +
                    tcpSocket.getPort();
        }

        String getUniqueName() {
            return uniqueName;
        }

        void sendMsg(String msg) {
            toSend.add(msg);
        }

        void sendUdpMsg(byte[] msg) {
            byte[] signedMsg = (uniqueName + ":\n" + new String(msg)).getBytes();
            DatagramSocket udpSocket = null;
            try {
                udpSocket = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(signedMsg, signedMsg.length,
                        tcpSocket.getInetAddress(), tcpSocket.getPort());

                udpSocket.send(packet);
            } catch (IOException ioe) {
                raiseError("Fatal error: " + ioe.getMessage());
            } finally {
                if (udpSocket != null) {
                    udpSocket.close();
                }
            }
        }

        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                tcpSocket.setSoTimeout(200);
                in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
                out = new PrintWriter(tcpSocket.getOutputStream(), true);

                StringBuilder inMsg = new StringBuilder(uniqueName + ":\t");
                int sign = 0;
                while (sign != -1) {
                    try {
                        sign = in.read();
                        inMsg.append((char)sign);
                    }
                    catch (SocketTimeoutException ignored) {}

                    if (inMsg.charAt(inMsg.length() - 1) == '\n') {
                        String finalMsg = inMsg.deleteCharAt(inMsg.length() - 1).toString();
                        sendToAllOtherClients(finalMsg, uniqueName);
                        inMsg = new StringBuilder(uniqueName + ":\t");
                    }

                    String outMsg = toSend.poll();
                    if (outMsg != null) {
                        out.println(outMsg);
                    }
                }
            } catch (IOException ioe) {
                raiseError("Client " + getUniqueName() + "failed: " + ioe.getMessage());
            } finally {
                clients.remove(uniqueName);
                if (in != null) {
                    try { in.close(); }
                    catch (IOException ignored) {}
                }
                if (out != null) {
                    out.close();
                }
                if (tcpSocket != null) {
                    try { tcpSocket.close(); }
                    catch (IOException ignored) {}
                }
            }
        }
    }

    public static void main(String[] args) {
        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException re) {
            raiseError("Usage: ./program [port_number]");
        }
        new ChatServer(port).startServer();
    }

    private static void raiseError(String errorMsg) {
        System.out.println(errorMsg);
        System.exit(-1);
    }
}
