package db;

import entity.User;

import java.sql.*;
import java.util.ArrayList;

/**
 * @Author Hx
 * @Date 2022/5/23 18:37
 * @Describe 数据库操作类
 */
public class DB {
    //MySQL驱动
    private final static String DRIVER = "com.mysql.cj.jdbc.Driver";
    private final static int SUCCESS = 1;
    private final static int ERROR = 0;
    //待接受的好友
    private final static int PENDING = 1;
    //已接受的好友
    private final static int ACCEPTED = 2;
    private Connection conn;
    private boolean driverLoaded = false;
    private boolean isInit = false;
    private boolean isConnected = false;
    private PreparedStatement insertRecord;
    private PreparedStatement queryUsers;
    private PreparedStatement queryRoom;
    private PreparedStatement queryFriends;

    private DB() {
        init();
    }

    public static DB getInstance() {
        return DBHolder.instance;
    }

    public static void main(String[] args) {
        DB db = new DB();
    }

    /**
     * 连接数据库
     *
     * @return
     */
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
                        + "//127.0.0.1:3306", "root", "root");
                isConnected = true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("数据库连接失败");
            }
        }
        return conn; // 返回Connection对象
    }

    /**
     * 初始化数据库，创建user表，装入多个默认账号，创建好友关系映射表，聊天室信息表
     */
    private void init() {
        //创建chatroom数据库
        try {
            System.out.println("创建chatroom数据库...");
            getConnection();
            Statement createDatabase = conn.createStatement();
            //创建数据库
            createDatabase.execute("create database chatroom;");

        } catch (SQLException e) {
            if (e.getMessage().contains("database exists")) {
                System.out.println("chatroom数据库已存在~");
            } else {
                e.printStackTrace();
            }
        }

        //初始化用户表，创建好友关系映射表，聊天室信息表
        try {
            Statement statement = conn.createStatement();
            //选择数据库chatroom
            statement.execute("use chatroom");
            //创建用户表
            statement.execute("create table if not exists user(" +
                    "uid int not null, " +
                    "name varchar(10) not null, " +
                    "account  varchar(10) not null, " +
                    "password varchar(10) not null," +
                    "head varchar(10) default 'header')");

            statement.execute("truncate table user");
            //初始化用户
            User[] users = {
                    new User(1127, "小何", "123456", "123456"),
                    new User(1225, "小潘", "654321", "654321"),
                    new User(510, "小谢", "111111", "111111"),
                    new User(1124, "小林", "222222", "222222"),
                    new User(717, "小川", "101010", "101010"),
                    new User(53, "小辜", "666666", "666666"),
            };
            insertUser(users);
            System.out.println("user表初始化成功~");

            //创建好友关系表
            statement.execute("create table if not exists friends(" +
                    "uid int not null," +
                    "friend_uid int not null," +
                    "status int not null);");
            System.out.println("friends表初始化成功~");

            //创建聊天室信息表
            statement.execute("create table if not exists roomInfo(" +
                    "room_id int not null auto_increment primary key," +
                    "header  varchar(10) default 'header'," +
                    "port    int not null)auto_increment=1;");
            System.out.println("room表初始化成功~");

            //创建聊天室关系表
            statement.execute("create table if not exists room(" +
                    "room_id int not null," +
                    "uid     int not null," +
                    "port    int not null);");
            //关联聊天室关系表和聊天室信息表
            statement.execute("alter table room " +
                    "add constraint room_roominfo_room_id_fk " +
                    "foreign key (room_id) references roominfo (room_id)" +
                    "on update cascade on delete cascade;");
            System.out.println("room表初始化成功~");

            //创建聊天记录表
            statement.execute("create table if not exists record(" +
                    "room_id     int default 0 not null," +
                    "timestamp   timestamp     not null," +
                    "uid         int           not null," +
                    "text        text          not null);");
            //关联记录表和聊天室信息表
            statement.execute("alter table record " +
                    "add constraint record_roominfo_room_id_fk " +
                    "foreign key (room_id) references roominfo (room_id)" +
                    "on update cascade on delete cascade;");
            System.out.println("record表初始化成功~");

            insertRecord =
                    conn.prepareStatement("insert into record(room_id,timestamp,uid,text) value (?,?,?,?)");
            queryUsers = conn.prepareStatement("select * from user");
            queryUsers = conn.prepareStatement("select * from friends");
            queryRoom = conn.prepareStatement("select * from room");
            isInit = true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * 登录
     *
     * @param account
     * @param password
     * @return
     */
    public User login(String account, String password) {

        User user = new User();
        try {
            ResultSet res = queryUsers.executeQuery();
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

    /**
     * 查询传入uid对应用户的所有待添加好友
     *
     * @param uid
     * @return
     */
    public ArrayList<User> getNewFriends(int uid) {
        ArrayList<User> arrayList = new ArrayList<>();
        try {

            ResultSet friends = queryFriends.executeQuery();
            ResultSet users = queryUsers.executeQuery();
            //循环查找所有待添加好友
            while (friends.next()) {
                if (friends.getInt("uid") == uid && friends.getInt("status") == PENDING) {
                    int friends_id = friends.getInt("friend_uid");
                    while (users.next()) {
                        if (users.getInt("uid") == friends_id) {
                            queryUserById(users.getInt("uid"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("查询失败");
        }
        return arrayList;
    }

    /**
     * 根据uid查询用户
     *
     * @param uid
     * @return
     */
    public User queryUserById(int uid) {
        try {
            ResultSet res = queryUsers.executeQuery();
            while (res.next()) {
                if (res.getInt("uid") == uid) {
                    return new User(res.getInt("uid"),
                            res.getString("name"),
                            res.getString("account"),
                            res.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("查询用户失败~");
        }
        return null;
    }

    public ArrayList<Integer> queryRoomByUid(int uid) {
        ArrayList<Integer> integers = new ArrayList<>();
        try {
            ResultSet res = queryRoom.executeQuery();
            while (res.next()) {
                if (res.getInt("uid") == uid) {
                    integers.add(res.getInt("room_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("查询聊天室失败~");
        }
        return integers;
    }

    /**
     * 插入聊天信息
     *
     * @param group_id
     * @param uid
     * @param text
     * @throws SQLException
     */
    public void insertRecord(int group_id, int uid, String text) throws SQLException {

        insertRecord.setInt(1, group_id);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        insertRecord.setTimestamp(2, timestamp);
        insertRecord.setInt(3, uid);
        insertRecord.setString(4, text);
        insertRecord.executeUpdate();
    }

    /**
     * 插入用户数据
     *
     * @param users
     */
    public void insertUser(User[] users) throws SQLException {

        getConnection();
        PreparedStatement sql
                = conn.prepareStatement("insert into user(uid,name,account,password) value (?,?,?,?)");
        for (int i = 0; i < users.length; i++) {
            sql.setInt(1, users[i].getUid());
            sql.setString(2, users[i].getName());
            sql.setString(3, users[i].getAccount());
            sql.setString(4, users[i].getPassword());
            sql.executeUpdate();
        }
    }

    /**
     * 插入好友关系
     *
     * @param uid
     * @param friends_uid
     * @throws SQLException
     */
    private void insertFriendship(int uid, int friends_uid) throws SQLException {
        getConnection();
        PreparedStatement sql
                = conn.prepareStatement("insert into friends(uid,friend_uid) value (?,?)");

        sql.setInt(1, uid);
        sql.setInt(2, friends_uid);
        sql.executeUpdate();
    }

    /**
     * 检查xxx表是否存在
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    private boolean isTableExists(String tableName) throws SQLException {

        getConnection();
        Statement statement = conn.createStatement();
        //选择数据库chatroom
        statement.execute("use chatroom");
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, tableName, null);
        return resultSet.next();
    }

    private static class DBHolder {
        public static final DB instance = new DB();
    }
}
