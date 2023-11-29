package edu.hillel;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerSc {
        private final int PORT = 8080;
        private List<Client> clients = new CopyOnWriteArrayList<>();
        private static int newClientIndex = 1;

        public void start(){
            try(ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server is run");
                while (true) {
                    Socket client = serverSocket.accept();
                    String generatedClientName = "Client " + newClientIndex;

                     new Thread(() -> {
                         try (BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(client.getInputStream())));
                              PrintWriter printWriter = new PrintWriter(client.getOutputStream(), true)) {
                            Client newClient = new Client(client, generatedClientName, LocalDateTime.now(), printWriter);
                            clients.add(newClient);
                             System.out.println("New client connected: " + generatedClientName);
                             newClientIndex++;
                             sendMessageToAllClients(clients, generatedClientName + " was connected", generatedClientName);
                             receiveClientMessage(clients, bufferedReader, newClient, generatedClientName, client);

                         } catch (IOException e) {
                             if (clients.removeIf(c -> generatedClientName.equals(c.getName())))
                                 System.out.println(generatedClientName + " disconnected");
                         }
                     }).start();
                }
            }
            catch (IOException e) {
                System.out.println(" " + e);
            }
        }

        public void sendMessageToAllClients(List<Client> clients, String message, String fromClient) {
            clients.stream().filter(c -> !fromClient.equals(c.getName())).forEach(c -> c.getPrintWriter().println(message));
        }

    public void receiveClientMessage(List<Client> clients, BufferedReader bufferedReader, Client newClient, String newClientName, Socket client) throws IOException {
        String msg;
        while ((msg = bufferedReader.readLine()) != null) {
            if ("exit".equals(msg)) {
                clients.remove(newClient);
                sendMessageToAllClients(clients, newClientName + ": disconnected.", newClientName);
                break;
            } else if (msg.startsWith("file ")) {
                receiveFile(msg, newClient.getName(), client.getInputStream());
            }
            else {
                System.out.println(newClientName + ": " + msg);
                sendMessageToAllClients(clients, newClientName + ": " + msg, newClientName);
            }
        }
    }

    public void receiveFile(String msg, String clientName, InputStream inputStream) throws IOException {
        String fileName = clientName + "_" + (msg.substring(msg.lastIndexOf(' ') + 1)).trim();
        File file = new File(fileName);
        int length;
        byte[] bufferBytes = new byte[8192];
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            while ((length = inputStream.read(bufferBytes)) >= 0) {
                bufferedOutputStream.write(bufferBytes, 0, length);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSc serverSc = new ServerSc();
        serverSc.start();
    }
}
