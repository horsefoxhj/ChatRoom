package manager;

import entity.User;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;

/**
 * @Author Hx
 * @Date 2022/5/16 0:45
 */
public class UserManager {

    private static class ManagerHolder{
        private final static UserManager instance = new UserManager();
    }
    public static UserManager getInstance(){
        return ManagerHolder.instance;
    }
    private UserManager(){}

    private ArrayList<User> users;


}
