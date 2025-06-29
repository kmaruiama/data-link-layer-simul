import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Dispositivo2 {
    public void listens() throws IOException {
        Socket socket = new Socket("localhost", 9999);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

// Escuta mensagem
        String mensagem = in.readLine();
        System.out.println("Servidor disse: " + mensagem);

// Envia resposta
        out.println("Oi servidor, recebi sua mensagem!");
    }

}
