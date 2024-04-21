package action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class ActiveMode implements Action {

    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;

    public ActiveMode(
            BufferedReader reader,
            BufferedWriter writer,
            Socket socket
    ) {
        this.reader = reader;
        this.writer = writer;
        this.socket = socket;
    }

    public void execute(String args) throws IOException {
        String response;
        int localPort = socket.getLocalPort();
        int p1 = localPort / 256;
        int p2 = localPort % 256;
        writer.write("PORT "+args+","+p1+","+p2+"\r\n");
        writer.flush();
        response = reader.readLine();
        String code = response.split(" ")[0];

        if (!code.equals("200"))
            throw new RuntimeException("Error setting port connection in active mode. Reason: "+response);

        System.out.println("Active mode defined successfully on server");
    }
}
