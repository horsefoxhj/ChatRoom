package base.client;

import base.Constants;
import entity.Message;
import javafx.application.Platform;
import ui.util.CacheUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static utils.JsonUtils.Str2Msg;

/**
 * @Author Hx
 * @Date 2022/5/17 20:17
 */
public class ClientThread extends Thread {

    //该线程负责的Socket
    private final Socket socket;
    //该Client的所有者的uid
    private final int uid;
    //该Client连接的服务端的id
    private final int roomId;
    //发送的消息
    private String msg_send;
    //是否发送消息
    private boolean isSend = false;

    //构造器
    public ClientThread(int port, int uid, int roomId) throws IOException {
        socket = new Socket(Constants.IP_DEFAULT, port);
        this.roomId = roomId;
        this.uid = uid;
        System.out.println("启动ClientThread,roomId = " + roomId + "port = " + port);
        //启动线程
        start();
    }

    @Override
    public void run() {
        try {
            //启动消息线程
            new Thread(new MsgHandler(socket)).start();
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            //死循环接受客户端消息
            while (true) {
                if (isSend) {
                    //向服务端发出消息
                    out.println(msg_send);
                    out.flush();
                    isSend = false;
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务端发送消息
     *
     * @param msg 消息
     */
    public void sendMsg(String msg) {
        this.msg_send = msg;
        isSend = true;
    }

    //获取连接聊天室的ID
    public int getRoomId() {
        return roomId;
    }

    /**
     * 消息线程，接受来自服务端的消息
     */
    class MsgHandler implements Runnable {

        private final BufferedReader in;
        //接受的消息
        private String msg_received;

        public MsgHandler(Socket socket) throws IOException {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            //死循环获取服务端消息
            while ((msg_received = readMsg()) != null) {
                Message message = Str2Msg(msg_received);
                //换到fx线程，更新ui
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        CacheUtil.chatController.addTalkMsgLeft(message, false, false, true);
                    }
                });
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
                e.printStackTrace();
            }
            return null;
        }
    }
}


