import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class FTPClientApplication {

    private static final String server = "127.0.0.1";
    private static final int port = 1025;
    private static final String username = "server";
    private static final String password = "server";

    private static Socket serverSocket;
    private static final Scanner scanner = new Scanner(System.in);

    //diretorio local
    private static String root_local_dir = "./files";
    private static String current_local_dir = "./files";

    //diretorio do servidor
    private static String root_server_dir = "./files/";
    private static String current_server_dir = "./files/";

    //reader e writer
    private static BufferedReader buffReader;
    private static BufferedWriter buffWriter;
    private static PrintWriter printWriter;


    public static void main(String[] args) {
        try {
            menu();
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void menu() throws IOException {
        int option = 0;

        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxx");
        System.out.println("xxxxxx Cliente FTP xxxxx");
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxx\n");

        openConnection();
        authenticate();

        do {
            System.out.println("\nAções disponíveis abaixo:");
            System.out.println("[1] - Ver diretório local");
            System.out.println("[2] - Ver diretório servidor");
            System.out.println("[3] - Alterar diretório local");
            System.out.println("[4] - Alterar diretório servidor");
            System.out.println("[5] - Criar diretório local");
            System.out.println("[6] - Criar diretório servidor");
            System.out.println("[7] - Ver arquivos do diretório local");
            System.out.println("[8] - Ver arquivos do diretório servidor");
            System.out.println("[9] - Subir arquivo local");
            System.out.println("[10] - Baixar arquivo servidor");
            System.out.println("\n");
            System.out.print("Digite a opção desejada: ");

            option = scanner.nextInt();

            openConnection();
            authenticate();

            switch(option) {
                case 0:
                    quitServer();
                case 1:
                    getLocalDir();
                    break;
                case 2:
                    getServerDir();
                    break;
                case 3:
                    changeLocalDir();
                    break;
                case 4:
                    changeServerDir();
                    break;
                case 5:
                    makeLocalDir();
                    break;
                case 6:
                    makeServerDir();
                    break;
                case 7:
                    seeLocalFiles();
                    break;
                case 8:
                    seeServerFiles();
                    break;
                case 9:
                    uploadFile();
                    break;
                case 10:
                    downloadFile();
                    break;
            }
        }while(option != 0);

        closeConnection();
    }

    private static void quitServer() throws IOException {
        buffWriter.write("QUIT \r\n");
        buffWriter.flush();
    }

    private static void downloadFile() throws IOException {

        activeMode();

        System.out.print("Informe o nome do arquivo que deseja baixar do servidor: ");
        String fname = scanner.next();

        String relativePath = defineServerRelativePath(current_server_dir);

        buffWriter.write("RETR "+fname+" "+relativePath+"\r\n");
        buffWriter.flush();

        String response = buffReader.readLine();
        String code = response.split(" ")[0];

        if(code.equals("550"))
            throw new RuntimeException("Erro ao baixar arquivo. Razão: "+response);

        System.out.println("iniciando copia de arquivo");

        File newFile = new File(current_local_dir+"/"+fname);

        BufferedReader rin = null;
        PrintWriter rout = null;

        try {
            rin = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            rout = new PrintWriter(new FileOutputStream(newFile), true);

        } catch (IOException e) {
            System.out.println("Não foi possível criar stream de arquivo");
        }

        String s;

        try {
            while ((s = rin.readLine()) != null) {
                rout.println(s);
            }
        } catch (IOException e) {
            System.out.println("Não foi possível ler ou escrever junto ao servidor");
            e.printStackTrace();
        }

        try {
            rout.close();
            rin.close();
        } catch (IOException e) {
            System.out.println("Não foi possível fechar stream de arquivo");
            e.printStackTrace();
        }

        System.out.println("finalizando copia de arquivo");
        System.in.read();
    }

    //VALIDADO
    private static void authenticate() throws IOException {

        System.out.println(buffReader.readLine());

        String response;
        buffWriter.write("USER "+username+"\r\n");
        buffWriter.flush();
        response = buffReader.readLine();
        String userCode = response.split(" ")[0];

        if (!userCode.equals("331"))
            throw new RuntimeException("Erro ao autenticar usuário. Motivo: "+response);

        buffWriter.write("PASS "+password+"\r\n");
        buffWriter.flush();
        response = buffReader.readLine();
        String passCode = response.split(" ")[0];

        if (!Objects.equals(passCode, "230"))
            throw new RuntimeException("Erro ao autenticar. Motivo: "+response);

    }

    //VALIDADO
    private static void activeMode() throws IOException {
        String response;
        int localPort = serverSocket.getLocalPort();
        int p1 = localPort / 256;
        int p2 = localPort % 256;
        buffWriter.write("PORT "+server.replace(".", ",")+","+p1+","+p2+"\r\n");
        buffWriter.flush();
        response = buffReader.readLine();
        String code = response.split(" ")[0];

        if (!code.equals("200"))
            throw new RuntimeException("Erro ao definir porta de conexão ativa. Motivo: "+response);

    }

    //VALIDADO
    private static void openConnection() throws IOException {
        serverSocket = new Socket(server, port);
        buffReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        buffWriter = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
        printWriter = new PrintWriter(serverSocket.getOutputStream());
    }

    //VALIDADO
    private static void closeConnection() throws IOException {
        buffWriter.close();
        buffReader.close();
        printWriter.close();
        serverSocket.close();
    }

    private static String defineServerRelativePath(String path) {
        if(path.startsWith("./files/"))
            return path.substring("./files/".length());
        if (path.startsWith("./files"))
            return path.substring("./files".length());
        return path;
    }
    //VALIDADO
    private static void uploadFile() throws IOException {

        activeMode();

        System.out.print("Informe qual arquivo deseja subir: ");
        String filename = scanner.next();

        File f = new File(current_local_dir+"/"+filename);

            BufferedReader rin = null;
            PrintWriter rout = null;

            try {
                rin = new BufferedReader(new FileReader(f));
                rout = new PrintWriter(serverSocket.getOutputStream(), true);

            } catch (IOException e) {
                System.out.println("Não foi possível criar stream de arquivos");
            }

            String relativePath = defineServerRelativePath(current_server_dir);

            printWriter.write("STOR "+filename+" "+relativePath+"\r\n");
            printWriter.flush();

            String s;

            try {
                while ((s = rin.readLine()) != null) {
                    rout.println(s);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

//            System.out.println(buffReader.readLine());

            try {
                rout.close();
                rin.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("Arquivo enviado com sucesso para ser salvo no servidor");
            System.in.read();
    }

    //VALIDADO
    private static void getLocalDir() throws IOException {
        System.out.println("Diretório local atual: "+current_local_dir);
        System.in.read();
    }

    private static void seeServerFiles() throws IOException {

        String relativePath = defineServerRelativePath(current_server_dir);

        buffWriter.write("LIST "+relativePath+"\r\n");
        buffWriter.flush();

        String response = buffReader.readLine();
        String codeOne = response.split(" ")[0];

        if (!codeOne.equals("125"))
            throw new RuntimeException("Erro ao buscar arquivos do servidor. Razão: "+response);

        System.out.println("Arquivos disponíveis no servidor");

        String list_of_files = buffReader.readLine();
        String codeTwo = list_of_files.split(" ")[0];

        if(!codeTwo.equals("226"))
            System.out.println("Não há arquivos no servidor");

        else {
            String[] files = list_of_files.split(" ")[1].split("&");
            for(String f: files) {
                System.out.println(f);
            }
        }
        System.in.read();
    }

    //VALIDADO
    private static void seeLocalFiles() throws IOException {
        File file = new File(current_local_dir);

        if (file.exists() && file.isDirectory()) {
            for (String f : Objects.requireNonNull(file.list())) {
                if (f.contains(".txt"))
                    System.out.println(f);
            }
        } else if(file.exists() && file.isFile()) {
            System.out.println("arquivo: "+file.getName());
        } else {
            System.out.println("Não há arquivos no diretório atual");
        }
        System.in.read();
    }

    //VALIDADO
    private static void getServerDir() throws IOException {

        if(!current_server_dir.equals(root_server_dir)) {
            System.out.println(current_server_dir);
            return;
        }

        buffWriter.write("PWD\r\n");
        buffWriter.flush();

        String response = buffReader.readLine();
        String code = response.split(" ")[0];
        String path = response.split(" ")[1];

        current_server_dir = path;

        if (!code.equals("257"))
            throw new RuntimeException("Erro ao buscar diretório atual do servidor. Motivo: "+response);

        System.out.println("Diretório servidor atual: "+current_server_dir);

        System.in.read();
    }

    //VALIDADO
    private static void changeServerDir() throws IOException {

        System.out.print("Para onde deseja ir? ");
        String subDir = scanner.next();
        printWriter.write("CWD "+subDir+"\r\n");
        printWriter.flush();
        String response = buffReader.readLine();
        String code = response.split(" ")[0];
        String path = response.split(" ")[1];

        if (!code.equals("250"))
            throw new RuntimeException("Erro ao mudar de diretório no servidor. Razão: "+response);

        current_server_dir = path;

        System.out.println("Diretório do servidor alterado com sucesso");
        System.in.read();
    }

    //VALIDADO
    private static void makeLocalDir() throws IOException {
        System.out.print("Informe nome desejado: ");
        String dir = scanner.next();

        if (dir != null && dir.matches("^[a-zA-Z0-9]+$")) {

            File f = new File(current_local_dir + "/" + dir);

            boolean wasFolderCreated = f.mkdir();

            if (!wasFolderCreated) {
                System.out.println("Erro ao criar novo diretório local");
            } else {
                System.out.println("Diretório será criado em breve");
            }
        }else {
            System.out.println("Nome inválido para criação de diretório local");
        }
        System.in.read();
    }

    //VALIDADO
    private static void changeLocalDir() throws IOException {
        String filename = current_local_dir;

        System.out.print("Indique diretório desejado: ");
        String args = scanner.next();

        if (args.equals("..")) {
            int ind = filename.lastIndexOf("/");
            if (ind > 0) {
                filename = filename.substring(0, ind);
            }
        }

        else if (!args.equals(".")) {
            filename = filename + "/" + args;
        }

        File f = new File(filename);

        if (f.exists() && f.isDirectory() && (filename.length() >= root_local_dir.length())) {
            current_local_dir = filename;
            System.out.println("Diretório local atualizado com sucesso");
        }else {
            System.out.println("Não foi possível alterar diretório local");
        }

        System.in.read();
    }

    //VALIDADO
    private static void makeServerDir() throws IOException {


        System.out.print("Informe nome desejado: ");
        String dir = scanner.next();

        buffWriter.write("MKD "+dir+"\r\n");
        buffWriter.flush();

        String response = buffReader.readLine();
        String code = response.split(" ")[0];

        if(!code.equals("250"))
            throw new RuntimeException("Erro ao criar diretório remoto. Razão: "+response);

        System.out.println("Diretório remoto criado com sucesso");
        System.in.read();
    }
}
