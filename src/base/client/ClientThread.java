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

    //���̸߳����Socket
    private final Socket socket;
    //��Client�������ߵ�uid
    private final int uid;
    //��Client���ӵķ���˵�id
    private final int roomId;
    //���͵���Ϣ
    private String msg_send;
    //�Ƿ�����Ϣ
    private boolean isSend = false;

    //������
    public ClientThread(int port, int uid, int roomId) throws IOException {
        socket = new Socket(Constants.IP_DEFAULT, port);
        this.roomId = roomId;
        this.uid = uid;
        System.out.println("����ClientThread,roomId = " + roomId + "port = " + port);
        //�����߳�
        start();
    }

    @Override
    public void run() {
        try {
            //������Ϣ�߳�
            new Thread(new MsgHandler(socket)).start();
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            //��ѭ�����ܿͻ�����Ϣ
            while (true) {
                if (isSend) {
                    //�����˷�����Ϣ
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
     * �����˷�����Ϣ
     *
     * @param msg ��Ϣ
     */
    public void sendMsg(String msg) {
        this.msg_send = msg;
        isSend = true;
    }

    //��ȡ���������ҵ�ID
    public int getRoomId() {
        return roomId;
    }

    /**
     * ��Ϣ�̣߳��������Է���˵���Ϣ
     */
    class MsgHandler implements Runnable {

        private final BufferedReader in;
        //���ܵ���Ϣ
        private String msg_received;

        public MsgHandler(Socket socket) throws IOException {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            //��ѭ����ȡ�������Ϣ
            while ((msg_received = readMsg()) != null) {
                Message message = Str2Msg(msg_received);
                //����fx�̣߳�����ui
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        CacheUtil.chatController.addTalkMsgLeft(message, false, false, true);
                    }
                });
            }
        }

        /**
         * ��ȡ�ͻ�����Ϣ
         */
        private String readMsg() {
            try {
                return in.readLine();
            }
            //�����쳣��˵����Socket��Ӧclient�Ѿ��ر�
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


