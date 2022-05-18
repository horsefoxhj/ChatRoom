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
    private ServerSocket serverSocket;
    private Socket socket = null;
    //当前聊天室的端口
    private final int port = MyServer.getInstance().newPort();
    //当前聊天室的名字
    private String groupName;

    public ServerThread() throws IOException {
        sockets = new ArrayList<>();
        serverSocket = new ServerSocket(port);
        //聊天室名字默认为端口号+聊天室
        groupName = port + "号聊天室";
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);

            //初始化客户端连接检测线程，并设置回调，当有client连接时向sockets中添加连接后生成的socket
            AcceptThread acceptThread = new AcceptThread(this.serverSocket, s -> sockets.add(s));
            //启动客户端连接线程，检测并接受客户端的连接
            new Thread(acceptThread).start();
            //初始化缓冲字符输入，检测主用户(我)的输入
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //死循环向所有连接到此服务的client发送信息
            while (true) {
                String msg = in.readLine();
                for (Socket socket : sockets) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    if (socket.equals(this.socket)) {
                        out.println("(你)" + msg);
                    } else {
                        out.println(msg);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置聊天室的名字
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 返回聊天室名字
     * @return
     */
    public String getGroupName(){
        return groupName;
    }
}

/**
 * 客户端连接检测线程
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
        //死循环检测是否有client连接
        while (true) {
            try {
                Socket s = serverSocket.accept();
                //有client连接时，回调s，添加到服务线程的sockets中
                acceptCallback.addClient(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 *
 */
class Print implements Runnable{
    @Override
    public void run() {

    }
}

