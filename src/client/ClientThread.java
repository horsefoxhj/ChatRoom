package client;

import java.net.Socket;
import java.util.Scanner;

/**
 * @Author Hx
 * @Date 2022/5/17 20:17
 */
public class ClientThread implements Runnable {

    private Socket socket = null;
    Scanner input = new Scanner(System.in);
    private String client_name = null;

    public ClientThread(Socket socket, String client_name) {
        this.socket = socket;
        this.client_name = client_name;
    }

    @Override
    public void run() {

    }
}
