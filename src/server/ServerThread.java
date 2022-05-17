package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @Author Hx
 * @Date 2022/5/17 20:21
 */
public class ServerThread extends Thread {

    //保存连接到该服务线程的Client的socket;
    private volatile ArrayList<Socket> sockets;
    private ServerSocket serverSocket = null;
    private BufferedReader br = null;
    private Socket socket = null;

    public ServerThread() throws IOException {
        this.sockets = new ArrayList<>();
        this.serverSocket = new ServerSocket(MyServer.newPort());
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);

            //客户端连接回调
            AcceptThread acceptThread = new AcceptThread(this.serverSocket, s -> sockets.add(s));
            new Thread(acceptThread).start();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true){
                String msg = in.readLine();
                for (Socket socket : sockets){
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    if (socket.equals(this.socket)){
                        out.println("(你)" + msg);
                    }else{
                        out.println(msg);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * 客户端接受线程
 */
class AcceptThread implements Runnable {

    private ServerSocket serverSocket;
    private AcceptCallback acceptCallback;

    public AcceptThread(ServerSocket serverSocket, AcceptCallback acceptCallback) {
        this.serverSocket = serverSocket;
        this.acceptCallback = acceptCallback;
    }

    @Override
    public void run() {
        while (true){
            try {
                Socket s = serverSocket.accept();
                acceptCallback.addClient(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

