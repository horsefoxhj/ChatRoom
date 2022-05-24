package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @Author Hx
 * @Date 2022/5/17 20:41
 */
public class MyServer extends Thread {

    //保存服务线程
    private ArrayList<ServerThread> serverThreads = new ArrayList<>();

    //隐藏构造器
    private MyServer() {
    }

    //获取单例MyServer
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
            //每三秒检测一次是否有服务端已经死去，如果有，将其从服务池中移除
            Thread.sleep(3000);
            serverThreads.removeIf(s -> !s.isAlive());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //返回当前服务线程数
    public int ServerNum() {
        return serverThreads.size();
    }

    //返回新可用端口
    public int newPort() {

        while (true) {
            boolean exist = false;
            //定义一个范围在30000~60000的端口号
            int port = new Random().nextInt(60000) + 30000;
            //遍历服务池，查看是否有相同的端口
            for (ServerThread s : serverThreads) {
                if (s.getPort() == port) {
                    exist = true;
                    break;
                }
            }
            if (!exist) return port;
        }
    }

    //创建一个服务端，若已存在则返回，否则新建
    public ServerThread createServerThread(int port) throws IOException {
        for (ServerThread s : serverThreads) {
            if (port == s.getPort())
                return s;
        }

        ServerThread s = new ServerThread(port);
        serverThreads.add(s);
        return s;
    }

    //返回当前服务端数组
    public ArrayList<ServerThread> getServerThreads() {
        return serverThreads;
    }

    //用内部静态类编译前持有MyServer类，创建时才实例化
    private static class MyServerHolder {
        private final static MyServer instance = new MyServer();
    }
}


