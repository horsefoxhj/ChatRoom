package base.server;

import db.DB;
import entity.RoomInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @Author Hx
 * @Date 2022/5/17 20:41
 */
public class ServerManager extends Thread {

    //��������߳�,Key=RoomId,Value=ServerThread
//    private static HashMap<Integer, ServerThread> serverThreads = new HashMap<>();
    private final ArrayList<ServerThread> serverList = new ArrayList<>();

    //���ع�����
    private ServerManager() {
    }

    //��ȡ����MyServer
    public static ServerManager getInstance() {
        return MyServerHolder.instance;
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            //ÿ������һ���Ƿ��з�����Ѿ���ȥ������У����������÷����
            try {
                Thread.sleep(3000);
                try {
                    for (ServerThread s : serverList) {
                        if (!s.isAlive()) {
                            serverList.remove(s);
                            int port = s.getPort();
                            int roomId = s.getRoomId();
                            createServerThread(port, roomId);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ��ʼ������ˣ�����ÿ���˿ڵ�ServerSocket
     */
    public void initServerManager() {
        DB db = DB.getInstance();
        ArrayList<RoomInfo> roomInfos = db.getAllRoomInfo();
        for (RoomInfo roomInfo : roomInfos) {
            try {
                createServerThread(roomInfo.port, roomInfo.roomId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //����ServerManager
        this.start();
    }

    /**
     * �����¿��ö˿�
     */
    public int newPort() {
        while (true) {
            boolean exist = false;
            //����һ����Χ��30000~60000�Ķ˿ں�
            int port = new Random().nextInt(60000) + 30000;
            //��������أ��鿴�Ƿ�����ͬ�Ķ˿�
            for (ServerThread s : serverList) {
                if (s.getPort() == port) {
                    exist = true;
                    break;
                }
            }
            if (!exist) return port;
        }
    }

    /**
     * ����һ������ˣ����Ѵ����򷵻أ������½�
     */
    public ServerThread createServerThread(int port, int roomId) throws IOException {
        for (ServerThread s : serverList) {
            if (port == s.getPort())
                return s;
        }

        ServerThread s = new ServerThread(port, roomId);
        serverList.add(s);
        return s;
    }

    /**
     * ���ص�ǰ���������
     *
     * @return serverThreads
     */
    public ArrayList<ServerThread> getServerList() {
        return serverList;
    }

    /**
     * ���ڲ���̬�����ǰ����MyServer�࣬����ʱ��ʵ����
     */
    private static class MyServerHolder {
        private final static ServerManager instance = new ServerManager();
    }
}


