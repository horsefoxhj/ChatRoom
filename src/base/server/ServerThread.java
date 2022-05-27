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
    //�������ӵ��÷����̵߳�Client��socket;
    private volatile List<Socket> sockets;
    //��ǰ�����ҵĶ˿�
    private ServerSocket serverSocket;
    //��ǰ�����ҵ�����
    private String groupName;

    public ServerThread(int port) throws IOException {
        sockets = Collections.synchronizedList(new ArrayList<>());
        serverSocket = new ServerSocket(port);
        this.port = port;
        //����������Ĭ��Ϊ�˿ں�+������
        groupName = port + "��������";
        System.out.println("--------" + groupName + "--------");
    }

    @Override
    public void run() {
        try {
            //��ѭ������Ƿ���client����
            while (true) {
                try {
                    Socket s = serverSocket.accept();
                    //��client����ʱ���ص�s����ӵ������̵߳�sockets��
                    sockets.add(s);
                    //������Ϣ�߳�
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
     * ��������������
     *
     * @return
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * ���������ҵ�����
     *
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
     * ��sockets���Ƴ�ָ��socket
     *
     * @param s
     * @return
     */
    public boolean removeSocket(Socket s) {
        return sockets.remove(s);
    }

    /**
     * �������ӵ���server������client
     *
     * @return
     */
    public List<Socket> getSockets() {
        return sockets;
    }
}

/**
 * ������Ϣ�̣߳�Ϊһ��client����
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
                        out.println(TimeUtils.getCurrentTime() + "(��):" + msg);
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
        //�����쳣��˵����Socket��Ӧclient�Ѿ��ر�
        catch (IOException e) {
            //ɾ����Socket
            serverThread.removeSocket(socket);
        }
        return null;
    }
}
