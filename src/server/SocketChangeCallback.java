package server;

import java.net.Socket;
import java.util.ArrayList;

/**
 * @Author Hx
 * @Date 2022/5/17 21:53
 * @Describe
 */
public interface SocketChangeCallback {
    void change(ArrayList<Socket> sockets);
}
