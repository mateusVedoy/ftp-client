import action.Authenticate;
import action.ActiveMode;
import action.UploadFile;

import java.io.*;
import java.net.Socket;

public class FTPClientApplication {

    private static Authenticate auth;
    private static ActiveMode activeMode;
    private static UploadFile uploadFile;

    public static void main(String args[]) {
        String server = "127.0.0.1";
        int port = 1025;
        String username = "server";
        String password = "server";
        String filePath = "./files/origin.txt";
        String remoteDirPath = "";

        try (Socket socket = new Socket(server, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            //INSTANCES
            auth = new Authenticate(reader, writer);
            activeMode = new ActiveMode(reader, writer, socket);
            uploadFile = new UploadFile(reader, writer, socket);

            //SERVER WELCOME
            String response = reader.readLine();
            System.out.println("Server Welcome" + response);

            auth.execute(username+","+password);

            String address = server.replace(".", ",");
            activeMode.execute(address);

            //aqui depois vai ser passado pelo usuario
            String pathToFile = "./files/origin.txt";
            uploadFile.execute(pathToFile);

            System.out.println("fim do programa");

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}