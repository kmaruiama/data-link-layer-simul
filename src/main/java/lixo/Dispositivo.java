package lixo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Dispositivo {
    public void run () throws IOException {
        ServerSocket server = new ServerSocket(9999);
        Socket socket = server.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("oi, aqui é o servidor!");

        String resposta = in.readLine();
        System.out.println("cliente respondeu: " + resposta);
    }
}
