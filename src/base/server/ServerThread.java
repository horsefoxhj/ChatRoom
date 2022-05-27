package base.server;

import utils.TimeUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author Hx
 * @Date 2022/5/17 20:21
 */
public class ServerThread extends Thread {

    private final int port;
    //保存连接到该服务线程的Client的socket;
    private volatile List<Socket> sockets;
    //当前聊天室的端口
    private ServerSocket serverSocket;
    //当前聊天室的名字
    private String groupName;

    public ServerThread(int port) throws IOException {
        sockets = Collections.synchronizedList(new ArrayList<>());
        serverSocket = new ServerSocket(port);
        this.port = port;
        //聊天室名字默认为端口号+聊天室
        groupName = port + "号聊天室";
        System.out.println("--------" + groupName + "--------");
    }

    @Override
    public void run() {
        try {
            //死循环检测是否有client连接
            while (true) {
                try {
                    Socket s = serverSocket.accept();
                    //有client连接时，回调s，添加到服务线程的sockets中
                    sockets.add(s);
                    //启动消息线程
                    new Thread(new MsgHandler(s, this)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回聊天室名字
     *
     * @return
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * 设置聊天室的名字
     *
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 返回当前服务端的端口号
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * 从sockets中移除指定socket
     *
     * @param s
     * @return
     */
    public boolean removeSocket(Socket s) {
        return sockets.remove(s);
    }

    /**
     * 返回连接到该server的所有client
     *
     * @return
     */
    public List<Socket> getSockets() {
        return sockets;
    }
}

/**
 * 单个消息线程，为一个client服务
 */
class MsgHandler implements Runnable {

    private Socket socket = null;
    private BufferedReader in = null;
    private ServerThread serverThread;

    public MsgHandler(Socket socket, ServerThread serverThread) throws IOException {
        this.serverThread = serverThread;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String msg = null;
            while ((msg = readMsg()) != null) {
                System.out.println(msg);
                for (Socket s : serverThread.getSockets()) {
                    PrintStream out = new PrintStream(s.getOutputStream());
                    if (s == this.socket) {
                        out.println(TimeUtils.getCurrentTime() + "(你):" + msg);
                    } else {
                        out.println(TimeUtils.getCurrentTime() + msg);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readMsg() {
        try {
            return in.readLine();
        }
        //捕获到异常，说明该Socket对应client已经关闭
        catch (IOException e) {
            //删除该Socket
            serverThread.removeSocket(socket);
        }
        return null;
    }
}
