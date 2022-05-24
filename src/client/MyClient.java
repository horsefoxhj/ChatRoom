package client;

import base.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @Author Hx
 * @Date 2022/5/18 22:19
 * @Describe
 */
public class MyClient {

    //保存Client线程
    private final ArrayList<ClientThread> clientThreads = new ArrayList<>();

    //隐藏构造器
    private MyClient() {
    }

    //获取单例MyClient
    public static MyClient getInstance() {
        return MyClientHolder.instance;
    }

    public static void main(String[] args) throws IOException {
        MyClient clientManager = MyClient.getInstance();
        ClientThread c = clientManager.createClientThread(Constants.IP_DEFAULT, 30000, "Hx");
        new Thread(c).start();

//        ClientThread c1 = clientManager.createClientThread(Constants.IP_DEFAULT, 30000,"DD");
//        new Thread(c1).start();
    }

    //根据传入的port，如果客户端已存在，则返回该客户端，否则创建一个新的返回
    public ClientThread createClientThread(String ip, int port, String name)
            throws IOException {
        for (ClientThread c : clientThreads) {
            if (c.getClient_name().equals(name))
                return c;
        }

        ClientThread c = new ClientThread(ip, port, name);
        clientThreads.add(c);
        return c;
    }

    //返回当前客户线程数
    public int ClientNum() {
        return clientThreads.size();
    }

    //返回当前客户端数组
    public ArrayList<ClientThread> getClientThreads() {
        return clientThreads;
    }

    //返回一个临时名字(随机数)
    public String getTempName() {
        while (true) {
            boolean exist = false;
            String n = String.valueOf(new Random().nextInt(100));
            for (ClientThread c : clientThreads) {
                if (c.getClient_name().equals(n)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) return n;
        }
    }

    //用内部静态类编译前持有MyClient类，创建时才实例化
    private static class MyClientHolder {
        private final static MyClient instance = new MyClient();
    }

}
