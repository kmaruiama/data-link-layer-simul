package lixo;

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

        String mensagem = in.readLine();
        System.out.println("servidor disse: " + mensagem);

        out.println("oi servidor, recebi sua mensagem");
    }

}
