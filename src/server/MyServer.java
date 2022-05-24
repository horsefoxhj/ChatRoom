package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @Author Hx
 * @Date 2022/5/17 20:41
 */
public class MyServer extends Thread {

    //��������߳�
    private ArrayList<ServerThread> serverThreads = new ArrayList<>();

    //���ع�����
    private MyServer() {
    }

    //��ȡ����MyServer
    public static MyServer getInstance() {
        return MyServerHolder.instance;
    }

    public static void main(String[] args) throws IOException {
        MyServer serverManager = MyServer.getInstance();
//        serverManager.start();
        ServerThread s = serverManager.createServerThread(30000);
        new Thread(s).start();
    }

    @Override
    public void run() {
        super.run();
        try {
            //ÿ������һ���Ƿ��з�����Ѿ���ȥ������У�����ӷ�������Ƴ�
            Thread.sleep(3000);
            serverThreads.removeIf(s -> !s.isAlive());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //���ص�ǰ�����߳���
    public int ServerNum() {
        return serverThreads.size();
    }

    //�����¿��ö˿�
    public int newPort() {

        while (true) {
            boolean exist = false;
            //����һ����Χ��30000~60000�Ķ˿ں�
            int port = new Random().nextInt(60000) + 30000;
            //��������أ��鿴�Ƿ�����ͬ�Ķ˿�
            for (ServerThread s : serverThreads) {
                if (s.getPort() == port) {
                    exist = true;
                    break;
                }
            }
            if (!exist) return port;
        }
    }

    //����һ������ˣ����Ѵ����򷵻أ������½�
    public ServerThread createServerThread(int port) throws IOException {
        for (ServerThread s : serverThreads) {
            if (port == s.getPort())
                return s;
        }

        ServerThread s = new ServerThread(port);
        serverThreads.add(s);
        return s;
    }

    //���ص�ǰ���������
    public ArrayList<ServerThread> getServerThreads() {
        return serverThreads;
    }

    //���ڲ���̬�����ǰ����MyServer�࣬����ʱ��ʵ����
    private static class MyServerHolder {
        private final static MyServer instance = new MyServer();
    }
}


