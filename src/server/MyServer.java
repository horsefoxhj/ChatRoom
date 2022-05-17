package server;

import java.util.ArrayList;

/**
 * @Author Hx
 * @Date 2022/5/17 20:41
 */
public class MyServer {

    //保存服务线程
    private static ArrayList<ServerThread> serverThreads;
    //Port
    private static int PORT = 30000;
    //IP
    public static final String IP = "127.0.0.1";

    //构造器
    private MyServer() {
        serverThreads = new ArrayList<>();
    }

    public static MyServer getInstance() {
        return MyServerHolder.instance;
    }

    //返回当前服务线程数
    public static int ServerNum() {
        return serverThreads.size();
    }

    //返回新可用端口
    public static int newPort() {
        return PORT;
    }

    //增加一个服务线程
    public boolean addServerThreads(ServerThread st) {
        PORT++;
        return serverThreads.add(st);
    }

    //用内部静态类编译前持有MyServer类，创建时才实例化
    private static class MyServerHolder {
        private final static MyServer instance = new MyServer();
    }
}
