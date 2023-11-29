import edu.hillel.ServerSc;
import org.junit.Assert;
import org.junit.Test;
import edu.hillel.Client;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mockito.Mockito.*;
public class TestServerSC {
    @Test
    public void testReceiveClietMsg() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader("exit"));
        Client newClient = mock(Client.class);
        String newClientName = "Client-1";
        Socket client = mock(Socket.class);

        List<Client> clients = new CopyOnWriteArrayList<>();
        clients.add(newClient);

        ServerSc socketServer = new ServerSc();
        socketServer.receiveClientMessage(clients, bufferedReader, newClient, newClientName, client);

        Assert.assertEquals(0, clients.size());
    }

    @Test
    public void testReceiveFile() throws IOException {
        String msg = "file file.txt";
        String clientName = "Client-1";
        InputStream inputStream = new ByteArrayInputStream("Hello".getBytes());

        File file = new File(clientName + "_file.txt");
        file.deleteOnExit();

        ServerSc socketServer = new ServerSc();
        socketServer.receiveFile(msg, clientName, inputStream);

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file)))
        {
            Assert.assertEquals("Hello", bufferedReader.readLine());
        }

        file.deleteOnExit();
    }

    @Test
    public void testNotifyAllClients()
    {
        List<Client> clients = new CopyOnWriteArrayList<>();
        Socket socket = mock(Socket.class);

        PrintWriter printWriter1 = mock(PrintWriter.class);
        PrintWriter printWriter2 = mock(PrintWriter.class);

        Client client1 = new Client(socket, "Client-1", LocalDateTime.now(), printWriter1);
        Client client2 = new Client(socket, "Client-2", LocalDateTime.now(), printWriter2);
        clients.add(client1);
        clients.add(client2);

        ServerSc serverSocket = new ServerSc();
        serverSocket.sendMessageToAllClients(clients, "Hello", client2.getName());

        verify(printWriter1).println("Hello");
        verify(printWriter2, never()).println("Hello");
    }
}