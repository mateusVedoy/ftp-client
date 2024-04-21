package action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

public class Authenticate implements Action {

    private BufferedReader reader;
    private BufferedWriter writer;

    public Authenticate(
            BufferedReader reader,
            BufferedWriter writer
    ) {
        this.reader = reader;
        this.writer = writer;
    }

    public void execute(String args) throws IOException {
        String response;
        String[] credentials = args.split(",");
        writer.write("USER "+credentials[0]+"\r\n");
        writer.flush();
        response = reader.readLine();
        String userCode = response.split(" ")[0];

        if (!Objects.equals(userCode, "331"))
            throw new RuntimeException("Error while authenticate user. Reason: "+response);

        writer.write("PASS "+credentials[1]+"\r\n");
        writer.flush();
        response = reader.readLine();
        String passCode = response.split(" ")[0];

        if (!Objects.equals(passCode, "230"))
            throw new RuntimeException("Error while authenticate user. Reason: "+response);

        System.out.println("Authentication successfully executed");
    }
}
