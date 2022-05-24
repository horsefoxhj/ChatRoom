package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @Author Hx
 * @Date 2022/5/17 20:17
 */
public class ClientThread implements Runnable {

    //该线程负责的Socket
    private final Socket socket;
    //字符输入
    BufferedReader in;
    //该Client的名字
    private String client_name;

    //构造器
    public ClientThread(String ip, int port, String name) throws IOException {
        socket = new Socket(ip, port);
        client_name = name;
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            //启动消息接受线程
            new Thread(new MsgInput(socket)).start();
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(client_name + "加入聊天室");
            out.flush();
            String msg;
            //死循环接受信息
            while ((msg = in.readLine()) != null) {
                out.println(client_name + "说：" + msg);
                out.flush();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    //获取client名字
    public String getClient_name() {
        return client_name;
    }

    //设置client名字
    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    //返回当前客户端线程的Socket
    public Socket getSocket() {
        return socket;
    }
}

class MsgInput implements Runnable {

    BufferedReader out;
    private final Socket s;

    public MsgInput(Socket s) throws IOException {
        this.s = s;
        out = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String msg = null;
            while ((msg = out.readLine()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

