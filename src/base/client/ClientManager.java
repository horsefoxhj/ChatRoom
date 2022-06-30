package base.client;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Author Hx
 * @Date 2022/5/18 22:19
 * @Describe
 */
public class ClientManager {

    //缓存Client线程，Key为RoomId，Value为ClientThread
    private static HashMap<Integer, ClientThread> clientThreads = new HashMap<>();

    private ClientManager() {
    }

    public static ClientManager getInstance() {
        return MyClientHolder.instance;
    }

    /**
     * 获取Client线程
     *
     * @param roomId 聊天室id
     * @return
     */
    public static ClientThread getClientThread(int roomId) {
        return clientThreads.get(roomId);
    }

    /**
     * 删除client线程
     *
     * @param roomId 聊天室id
     */
    public static void removeClientThread(int roomId) {
        clientThreads.remove(roomId);
    }

/**
 * 根据传入的port，如果客户端已存在，则返回该客户端，否则创建一个新的返回
 *
 * @param port   端口
 * @param roomId 聊天室ID
 * @param uid    用户ID，该客户端线程的所有者
 */
public static void createClientThread(int port, int roomId, int uid) {

    //Client线程已存在
    if (clientThreads.containsKey(roomId)) return;

    ClientThread c = null;
    try {
        c = new ClientThread(port, uid, roomId);
        c.setName(String.valueOf(roomId));
    } catch (IOException e) {
        e.printStackTrace();
    }
    //将客户端线程放入缓存
    clientThreads.put(roomId, c);
}

    //用内部静态类编译前持有MyClient类，创建时才实例化
    private static class MyClientHolder {
        private final static ClientManager instance = new ClientManager();
    }
}
