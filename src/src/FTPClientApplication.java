import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class FTPClientApplication {

    public static void main(String args[]) {
        String server = "localhost";
        int port = 1025;
        String username = "server";
        String password = "server";
        String filePath = "./files/origin.txt";
        String remoteDirPath = "";

        try (Socket socket = new Socket(server, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String response = reader.readLine();
            System.out.println("Response: " + response);

            writer.write("USER " + username + "\r\n");
            writer.flush();
            response = reader.readLine();
            System.out.println("Response: " + response);

            //ENVIA PORTA DO CLIENTE PARA GERAR CONEXÃO
            int localPort = socket.getLocalPort();
            String host = socket.getLocalAddress().getHostAddress();
            System.out.println(localPort);
            System.out.println(host);
            int p1 = localPort / 256;
            int p2 = localPort % 256;
            writer.write("PORT 127,0,0,1,"+p1+","+p2+"\r\n");
            writer.flush();
            response = reader.readLine();
            System.out.println("Response: " + response);


            send(socket, writer, reader);
            System.out.println(reader.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void send(Socket socket, BufferedWriter writer, BufferedReader reader) throws IOException {
        FileInputStream fis = new FileInputStream("./files/origin.txt");
        BufferedInputStream bis = new BufferedInputStream(fis);
        OutputStream outputStream = socket.getOutputStream();

        writer.write("STOR server.txt\r\n");
        writer.flush();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = bis.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        fis.close();
        bis.close();
    }
}