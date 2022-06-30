package base.server;

import db.DB;
import entity.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utils.JsonUtils.Str2Msg;

/**
 * @Author Hx
 * @Date 2022/5/17 20:21
 */
public class ServerThread extends Thread {

    private final int port;
    //当前聊天室的ServerSocket
    private final ServerSocket serverSocket;
    //保存连接到该服务线程的Client的socket;
    private final List<Socket> sockets;
    //当前聊天室的id
    public int roomId;

    public ServerThread(int port, int roomId) throws IOException {
        sockets = Collections.synchronizedList(new ArrayList<>());
        serverSocket = new ServerSocket(port);
        this.port = port;
        this.roomId = roomId;
        System.out.println("启动ServerThread,port = " + port + " roomId = " + roomId);
        start();
    }

    @Override
    public void run() {
        //死循环检测是否有client连接
        while (true) {
            try {
                Socket s = serverSocket.accept();
                //有client连接时，添加到服务线程的sockets中
                sockets.add(s);
                System.out.println("Client连接到" + s.getPort());
                System.out.println("roomId:" + roomId + " port:" + port + " client:" + sockets.size());
                //启动消息线程
                new Thread(new MsgHandler(s, this)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
     * 返回当前服务器对应的聊天室id
     *
     * @return roomId
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * 从sockets中移除指定socket
     */
    public void removeSocket(Socket s) {
        sockets.remove(s);
    }

    /**
     * 返回连接到该server的所有client
     */
    public List<Socket> getSockets() {
        return sockets;
    }


    /**
     * 消息线程，接受单个客户端发来的消息并向所有客户端广播
     */
    static class MsgHandler implements Runnable {

        private final Socket socket;
        private final BufferedReader in;
        private final ServerThread serverThread;

        public MsgHandler(Socket socket, ServerThread serverThread) throws IOException {
            this.serverThread = serverThread;
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            String msg;

while ((msg = readMsg()) != null) {
    try {
        System.out.println(msg);
        Message message = Str2Msg(msg);
        System.out.println("收到Id为" + message.uid + "的消息：" + message.text);
        //向所有客户端广播
        List<Socket> sockets = serverThread.getSockets();
        for (Socket s : sockets) {
            PrintWriter out = new PrintWriter(s.getOutputStream());
            if (s != this.socket) {
                out.println(msg);
                out.flush();
            }
        }

        DB db = DB.getInstance();
        db.insertRecord(serverThread.roomId, message.uid, message.text);
    } catch (IOException | SQLException e) {
        e.printStackTrace();
    }
}
        }

        /**
         * 读取客户端消息
         */
        private String readMsg() {
            try {
                return in.readLine();
            }
            //捕获到异常，说明该Socket对应client已经关闭
            catch (IOException e) {
                //删除该Socket
                serverThread.removeSocket(socket);
                System.out.println("客户端" + socket.getPort() + "断开");
            }
            return null;
        }
    }
}

