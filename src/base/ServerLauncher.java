package base;

import base.server.ServerManager;

/**
 * @Author Hx
 * @Date 2022/6/6 23:41
 * @Describe Æô¶¯·şÎñ¶Ë
 */
public class ServerLauncher {
    public static void main(String[] args) {
        ServerManager serverManager = ServerManager.getInstance();
        serverManager.initServerManager();
    }
}
