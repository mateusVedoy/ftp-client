package action;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Stream;

public class UploadFile implements Action {

    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;

    public UploadFile(
            BufferedReader reader,
            BufferedWriter writer,
            Socket socket
    ) {
        this.reader = reader;
        this.writer = writer;
        this.socket = socket;
    }

    @Override
    public void execute(String args) throws IOException {
        String filename = setFileName(args);
        FileInputStream fis = new FileInputStream(args);
        BufferedInputStream bis = new BufferedInputStream(fis);
        OutputStream outputStream = socket.getOutputStream();

        writer.write("STOR "+filename+"\r\n");
        writer.flush();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = bis.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        fis.close();
        bis.close();

        System.out.println("Upload to server executed successfully");
    }

    private String setFileName(String args) {
        String[] parts = args.split("/");
        int index = parts.length - 1;
        return parts[index];
    }
}
