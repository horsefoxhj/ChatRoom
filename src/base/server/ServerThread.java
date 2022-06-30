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
    //��ǰ�����ҵ�ServerSocket
    private final ServerSocket serverSocket;
    //�������ӵ��÷����̵߳�Client��socket;
    private final List<Socket> sockets;
    //��ǰ�����ҵ�id
    public int roomId;

    public ServerThread(int port, int roomId) throws IOException {
        sockets = Collections.synchronizedList(new ArrayList<>());
        serverSocket = new ServerSocket(port);
        this.port = port;
        this.roomId = roomId;
        System.out.println("����ServerThread,port = " + port + " roomId = " + roomId);
        start();
    }

    @Override
    public void run() {
        //��ѭ������Ƿ���client����
        while (true) {
            try {
                Socket s = serverSocket.accept();
                //��client����ʱ����ӵ������̵߳�sockets��
                sockets.add(s);
                System.out.println("Client���ӵ�" + s.getPort());
                System.out.println("roomId:" + roomId + " port:" + port + " client:" + sockets.size());
                //������Ϣ�߳�
                new Thread(new MsgHandler(s, this)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ���ص�ǰ����˵Ķ˿ں�
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * ���ص�ǰ��������Ӧ��������id
     *
     * @return roomId
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * ��sockets���Ƴ�ָ��socket
     */
    public void removeSocket(Socket s) {
        sockets.remove(s);
    }

    /**
     * �������ӵ���server������client
     */
    public List<Socket> getSockets() {
        return sockets;
    }


    /**
     * ��Ϣ�̣߳����ܵ����ͻ��˷�������Ϣ�������пͻ��˹㲥
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
        System.out.println("�յ�IdΪ" + message.uid + "����Ϣ��" + message.text);
        //�����пͻ��˹㲥
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
         * ��ȡ�ͻ�����Ϣ
         */
        private String readMsg() {
            try {
                return in.readLine();
            }
            //�����쳣��˵����Socket��Ӧclient�Ѿ��ر�
            catch (IOException e) {
                //ɾ����Socket
                serverThread.removeSocket(socket);
                System.out.println("�ͻ���" + socket.getPort() + "�Ͽ�");
            }
            return null;
        }
    }
}

