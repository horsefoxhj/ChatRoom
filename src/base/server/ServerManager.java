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

    //保存服务线程,Key=RoomId,Value=ServerThread
//    private static HashMap<Integer, ServerThread> serverThreads = new HashMap<>();
    private final ArrayList<ServerThread> serverList = new ArrayList<>();

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
     * 返回新可用端口
     */
    public int newPort() {
        while (true) {
            boolean exist = false;
            //定义一个范围在30000~60000的端口号
            int port = new Random().nextInt(60000) + 30000;
            //遍历服务池，查看是否有相同的端口
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


