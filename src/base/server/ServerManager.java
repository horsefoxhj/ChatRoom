package base.server;

import db.DB;
import entity.RoomInfo;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author Hx
 * @Date 2022/5/17 20:41
 */
public class ServerManager extends Thread {

    //��������߳�
    private final ArrayList<ServerThread> serverList = new ArrayList<>();
    private final DB db = DB.getInstance();

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
     * ������ѹ�ϵ�������������߳�
     */
    public RoomInfo insertFriendship(int uid, int friends_uid) {

        int status = db.insertFriendship(uid, friends_uid);
        if (status != DB.PENDING) {
            RoomInfo roomInfo = db.queryRoomInfoByRoomId(status);
            if (roomInfo != null) {
                try {
                    createServerThread(roomInfo.port, roomInfo.roomId);
                    return roomInfo;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * ����Ⱥ�ģ������������߳�
     */
    public RoomInfo createGroup(String groupName, int uid) {

        int roomId = db.insertRoomInfo(groupName, DB.MODE_GROUPS);
        db.insertRoom(roomId, uid);
        RoomInfo roomInfo = db.queryRoomInfoByRoomId(roomId);
        if (roomInfo != null) {
            try {
                createServerThread(roomInfo.port, roomId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return roomInfo;
        }
        return null;
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


