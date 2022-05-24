package db;

import entity.User;

import java.sql.*;

/**
 * @Author Hx
 * @Date 2022/5/23 18:37
 * @Describe
 */
public class DB {

    private final static String DRIVER = "com.mysql.cj.jdbc.Driver";
    private final static int SUCCESS = 1;
    private final static int ERROR = 0;
    private Connection conn;
    private boolean driverLoaded = false;
    private boolean isInit = false;
    private boolean isConnected = false;

    private DB() {
        init();
    }

    public static void main(String[] args) {
        DB db = new DB();
    }

    public static DB getInstance() {
        return DBHolder.instance;
    }

    // 连接数据库方法
    private Connection getConnection() {
        //加载启动程序
        if (!driverLoaded) {
            try {
                Class.forName(DRIVER);
                System.out.println("数据库驱动加载成功~");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("数据库驱动加载错误~");
            }
        }
        //建立数据库连接
        if (!isConnected) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql:"
                        + "//127.0.0.1:3306/", "root", "");
                isConnected = true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("数据库连接失败");
            }
        }
        return conn; // 返回Connection对象
    }

    /**
     * 初始化数据库，加入两个默认账号：
     * {uid:"1127",name:“Hx”,account:"123456",password:"123456",record:null}
     * {uid:"1225",name:“Pj”,account:"654321",password:"654321",record:null}
     */
    private void init() {
        try {
            System.out.println("创建chatroom数据库...");
            //创建数据库chatroom
            Connection conn = getConnection();
            Statement createDatabase = conn.createStatement();
            createDatabase.execute("create database chatroom;");

        } catch (SQLException e) {
            if (e.getMessage().contains("database exists")) {
                System.out.println("chatroom数据库已存在~");
            } else {
                e.printStackTrace();
            }
        }

        try {
            Statement statement = conn.createStatement();
            //选择数据库chatroom
            statement.execute("use chatroom");
            //创建用户表
            statement.execute("create table user(" +
                    "uid int not null, " +
                    "name varchar(10) not null, " +
                    "account  varchar(10) not null, " +
                    "password varchar(10) not null," +
                    "record int null);");

            //初始化用户
            User[] users = {
                    new User(1127, "Hx", "123456", "123456"),
                    new User(1225, "Pj", "654321", "654321")
            };
            insertUser(users);
            System.out.println("数据库初始化成功~");
            isInit = true;

        } catch (SQLException e) {
            if (e.getMessage().contains("Table 'user' already exists")) {
                System.out.println("User表已存在~");
            } else {
                e.printStackTrace();
            }
        }
//        finally {
//            try {
//                conn.close();
//                isConnected = false;
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }

    }

    //查询用户数据
    public User queryUser(String account, String password) {

        User user = new User();
        try {
            Connection conn = getConnection();
            PreparedStatement sql = conn.prepareStatement("select * from user");
            ResultSet res = sql.executeQuery();
            while (res.next()) {
                if (res.getString("account").equals(account)
                        && res.getString("password").equals(password)) {

                    user.setAccount(account);
                    user.setPassword(password);
                    user.setName(res.getString("name"));
                    return user;
                }
            }
        } catch (NullPointerException | SQLException e) {
            e.printStackTrace();
            System.out.println("查询失败");
        }
        return null;
    }

    //插入用户数据
    public void insertUser(User[] users) {

        try {
            Connection conn = getConnection();
            PreparedStatement sql
                    = conn.prepareStatement("insert into user(uid,name,account,password) value (?,?,?,?)");
            for (int i = 0; i < users.length; i++) {
                sql.setInt(1, users[i].getUid());
                sql.setString(2, users[i].getName());
                sql.setString(3, users[i].getAccount());
                sql.setString(4, users[i].getPassword());
                sql.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class DBHolder {
        public static final DB instance = new DB();
    }
}
