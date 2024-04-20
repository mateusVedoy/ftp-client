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
            System.exit(0);
//            File file = new File(filePath);
//            long fileSize = file.length();
//            writer.write("PASV\r\n");
//            writer.flush();
//            response = reader.readLine();
//            //pegar esse valor para passar junto do PORT
//            System.out.println("Response: " + response);

            //PRECISO MANDAR DE QUAL PORTA ESTOU FALANDO 127.0.0.1:porta

//            String[] parts = response.split(",");
//            String ip = parts[0].substring(parts[0].lastIndexOf("(") + 1) + "." + parts[1] + "." + parts[2] + "." + parts[3];
//            int portData = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5].substring(0, parts[5].indexOf(")")));
//            System.out.println("Data Connection IP: " + ip);
//            System.out.println("Data Connection Port: " + portData);
//
//            try (Socket dataSocket = new Socket(ip, portData);
//                 BufferedOutputStream dataOutput = new BufferedOutputStream(dataSocket.getOutputStream());
//                 FileInputStream fileInput = new FileInputStream(file)) {
//
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                while ((bytesRead = fileInput.read(buffer)) != -1) {
//                    dataOutput.write(buffer, 0, bytesRead);
//                }
//            }
//
//            writer.write("STOR " + remoteDirPath + "/" + file.getName() + "\r\n");
//            writer.flush();
//            response = reader.readLine();
//            System.out.println("Response: " + response);
            System.out.println(reader.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void send(Socket socket, BufferedWriter writer, BufferedReader reader) throws IOException {
        FileInputStream fis = new FileInputStream("./files/origin.txt");
        BufferedInputStream bis = new BufferedInputStream(fis);

        writer.write("STOR server.txt\r\n");
        writer.flush();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = bis.read(buffer)) != -1) {
            socket.getOutputStream().write(buffer, 0, bytesRead);
        }

        String response = reader.readLine();
        System.out.println(response);

        bis.close();
        fis.close();
    }
}