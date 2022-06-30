package base.client;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Author Hx
 * @Date 2022/5/18 22:19
 * @Describe
 */
public class ClientManager {

    //����Client�̣߳�KeyΪRoomId��ValueΪClientThread
    private static HashMap<Integer, ClientThread> clientThreads = new HashMap<>();

    private ClientManager() {
    }

    public static ClientManager getInstance() {
        return MyClientHolder.instance;
    }

    /**
     * ��ȡClient�߳�
     *
     * @param roomId ������id
     * @return
     */
    public static ClientThread getClientThread(int roomId) {
        return clientThreads.get(roomId);
    }

    /**
     * ɾ��client�߳�
     *
     * @param roomId ������id
     */
    public static void removeClientThread(int roomId) {
        clientThreads.remove(roomId);
    }

/**
 * ���ݴ����port������ͻ����Ѵ��ڣ��򷵻ظÿͻ��ˣ����򴴽�һ���µķ���
 *
 * @param port   �˿�
 * @param roomId ������ID
 * @param uid    �û�ID���ÿͻ����̵߳�������
 */
public static void createClientThread(int port, int roomId, int uid) {

    //Client�߳��Ѵ���
    if (clientThreads.containsKey(roomId)) return;

    ClientThread c = null;
    try {
        c = new ClientThread(port, uid, roomId);
        c.setName(String.valueOf(roomId));
    } catch (IOException e) {
        e.printStackTrace();
    }
    //���ͻ����̷߳��뻺��
    clientThreads.put(roomId, c);
}

    //���ڲ���̬�����ǰ����MyClient�࣬����ʱ��ʵ����
    private static class MyClientHolder {
        private final static ClientManager instance = new ClientManager();
    }
}
