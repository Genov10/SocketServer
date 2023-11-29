package edu.hillel;

import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

public class Client {
    private final Socket socket;
    private final String name;
    private final LocalDateTime time;
    private final PrintWriter printWriter;

    public Client(Socket socket, String name, LocalDateTime time, PrintWriter printWriter) {
        this.socket = socket;
        this.name = name;
        this.time = time;
        this.printWriter = printWriter;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }
}
