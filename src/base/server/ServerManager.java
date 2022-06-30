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

    //保存服务线程
    private final ArrayList<ServerThread> serverList = new ArrayList<>();
    private final DB db = DB.getInstance();

    //隐藏构造器
    private ServerManager() {
    }

    //获取单例MyServer
    public static ServerManager getInstance() {
        return MyServerHolder.instance;
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            //每三秒检测一次是否有服务端已经死去，如果有，重新启动该服务端
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
 * 初始化服务端，保存每个端口的ServerSocket
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
    //启动ServerManager
    this.start();
}

    /**
     * 插入好友关系，并启动服务线程
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
     * 创建群聊，并启动服务线程
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
 * 创建一个服务端，若已存在则返回，否则新建
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
     * 返回当前服务端数组
     *
     * @return serverThreads
     */
    public ArrayList<ServerThread> getServerList() {
        return serverList;
    }

    /**
     * 用内部静态类编译前持有MyServer类，创建时才实例化
     */
    private static class MyServerHolder {
        private final static ServerManager instance = new ServerManager();
    }
}


