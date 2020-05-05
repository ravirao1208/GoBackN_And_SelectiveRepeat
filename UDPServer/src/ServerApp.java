import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ServerApp {
    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        int portNumber;
        String fileName;

        if (args.length != 2) {
            System.out.println("The total number of arguments required are 2");
            System.out.println("The arguments are portNumber and fileName");
        } else {
            portNumber = Integer.parseInt(args[0]);
            fileName = args[1];
            Server server = Server.getInstance(portNumber, fileName);
            server.receiveData(fileName);
        }
    }
}