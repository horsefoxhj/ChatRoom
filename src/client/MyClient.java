package client;

import server.MyServer;

import java.util.ArrayList;

/**
 * @Author Hx
 * @Date 2022/5/18 22:19
 * @Describe
 */
public class MyClient {

    //保存Client线程
    private ArrayList<ClientThread> clientThreads = new ArrayList<>();

    //隐藏构造器
    private MyClient(){
    }

    //获取单例MyClient
    public static MyClient getInstance(){
        return MyClientHolder.instance;
    }

    //添加一个客户端线程
    public boolean addClientThread(ClientThread ct){
        return clientThreads.add(ct);
    }

    //用内部静态类编译前持有MyClient类，创建时才实例化
    private static class MyClientHolder {
        private final static MyClient instance = new MyClient();
    }

    //返回当前客户端数组
    public ArrayList<ClientThread> getClientThreads() {
        return clientThreads;
    }
}
