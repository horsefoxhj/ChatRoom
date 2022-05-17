package server;

import java.net.Socket;

/**
 * @Author Hx
 * @Date 2022/5/17 21:53
 * @Describe
 */
public interface AcceptCallback {
    void addClient(Socket s);
}
