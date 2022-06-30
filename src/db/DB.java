package db;

import entity.Message;
import entity.RoomInfo;
import entity.User;

import java.sql.*;
import java.util.ArrayList;

import static base.Constants.ONLINE;

/**
 * @Author Hx
 * @Date 2022/5/23 18:37
 * @Describe 数据库操作类
 */
public class DB {

    public final static int PENDING = -11; //待接受的好友
    public final static int ACCEPTED = -12;//已接受的好友
    public final static int ALL = 3;//全部好友
    public final static int MODE_SINGLE = 1;//好友
    public final static int MODE_GROUPS = 2;//群聊
    private final static String DRIVER = "com.mysql.cj.jdbc.Driver"; //MySQL驱动
    private final static int SUCCESS = 1;
    private final static int ERROR = 0;
    PreparedStatement insertRecord;
    PreparedStatement insertRoomInfo;
    PreparedStatement queryRecord;
    PreparedStatement queryUsers;
    PreparedStatement queryRelationship;
    PreparedStatement queryRoomInfo;
    PreparedStatement queryFriends;
    private Connection conn;
    private boolean driverLoaded = false;
    private boolean isConnected = false;

    private DB() {
        init();
    }

    public static DB getInstance() {
        return DBHolder.instance;
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
        Statement statement = null;
        try {
            statement = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            //选择数据库chatroom
            statement.execute("use chatroom");
            //创建用户表
            statement.execute("create table if not exists user(" +
                    "uid        int not null, " +
                    "name       varchar(10) not null, " +
                    "account    varchar(10) not null, " +
                    "password   varchar(10) not null," +
                    "head       varchar(10) default 'header'," +
                    "online     int not null default 0)");

            statement.execute("truncate table user");
            //初始化用户
            User[] users = {
                    new User(1127, "小何", "123456", "123456", "header"),
                    new User(1225, "小潘", "654321", "654321", "pan"),
                    new User(717, "小川", "101010", "101010", "chuan")};
            insertUser(users);
            System.out.println("user表初始化成功~");

            //创建好友关系表
            statement.execute("create table if not exists friends(" +
                    "uid        int not null," +
                    "friend_uid int not null," +
                    "status     int not null);");
            System.out.println("friends表初始化成功~");

            //创建聊天室信息表
            statement.execute("create table if not exists roomInfo(" +
                    "room_id    int not null primary key," +
                    "room_Name  varchar(10) not null," +
                    "talkSketch text," +
                    "timestamp  bigint not null default 0," +
                    "header     varchar(10) default 'header'," +
                    "port       int not null," +
                    "mode       int not null default 0);");
            System.out.println("roomInfo表初始化成功~");

            //创建聊天室关系表
            statement.execute("create table if not exists room(" +
                    "room_id int not null," +
                    "uid     int not null);");

            //创建聊天记录表
            statement.execute("create table if not exists record(" +
                    "room_id     int default 0 not null," +
                    "timestamp   bigint        not null," +
                    "uid         int           not null," +
                    "text        text          not null);");

            statement.execute("insert into roominfo(room_id, room_Name, header, port, mode) " +
                    "VALUE (1,'相亲相爱打工人','header',30001,2)on duplicate key update port = 30001");
            statement.execute("insert into roominfo(room_id, room_Name, header, port, mode) " +
                    "VALUE (2,'咱们三','header',30002,2)on duplicate key update port = 30002");

//            statement.execute("truncate table room");
//            statement.execute("insert into room(room_id, uid) VALUE (1,1127)");
//            statement.execute("insert into room(room_id, uid) VALUE (1,1225)");
//            statement.execute("insert into room(room_id, uid) VALUE (2,1127)");
//            statement.execute("insert into room(room_id, uid) VALUE (2,1225)");
//            statement.execute("insert into room(room_id, uid) VALUE (2,717)");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        try {
            //关联聊天室关系表和聊天室信息表
            statement.execute("alter table room " +
                    "add constraint room_roominfo_room_id_fk " +
                    "foreign key (room_id) references roominfo (room_id)" +
                    "on update cascade on delete cascade;");
            System.out.println("room表初始化成功~");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            //关联记录表和聊天室信息表
            statement.execute("alter table record " +
                    "add constraint record_roominfo_room_id_fk " +
                    "foreign key (room_id) references roominfo (room_id)" +
                    "on update cascade on delete cascade;");
            System.out.println("record表初始化成功~");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            //初始化插入语句
            insertRecord =
                    conn.prepareStatement("insert into record(room_id,timestamp,uid,text) value (?,?,?,?)");
            insertRoomInfo =
                    conn.prepareStatement("insert into roominfo(room_id, room_Name, port, mode) " +
                            "value (?,?,?,?)");

            //初始化查询语句
            queryUsers = conn.prepareStatement("select * from user");
            queryFriends = conn.prepareStatement("select * from friends");
            queryRelationship = conn.prepareStatement("select * from room");
            queryRoomInfo = conn.prepareStatement("select * from roominfo");
            queryRecord = conn.prepareStatement("select * from record");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * 登录
     *
     * @param account  账号
     * @param password 密码
     * @return User
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
                    user.setUid(res.getInt("uid"));
                    user.setName(res.getString("name"));
                    user.setHeader(res.getString("head"));
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
     * 查询传入uid对应用户的状态为status好友,[ALL]查询所有好友
     *
     * @param uid    用户id
     * @param status 用户状态
     * @return ArrayList<User>
     */
    public ArrayList<User> queryFriends(int uid, int status) {
        ArrayList<User> arrayList = new ArrayList<>();
        try {
            //循环查找所有好友
            ResultSet friends = queryFriends.executeQuery();
            while (friends.next()) {
                //是该uid的好友
                int f_status = friends.getInt("status");
                if (friends.getInt("uid") == uid) {
                    //如果status为ALL则查询所有好友，否则只查询状态为status的好友
                    if (status == ALL || f_status == status) {
                        int friends_id = friends.getInt("friend_uid");
                        //查询该好友的信息
                        ResultSet users = queryUsers.executeQuery();
                        while (users.next()) {
                            //将该好友的数据存入列表
                            if (users.getInt("uid") == friends_id) {
                                User user = new User(users.getInt("uid"),
                                        users.getString("name"),
                                        users.getString("account"),
                                        users.getString("password"),
                                        users.getString("head"));
                                //设置好友状态，是否在线
                                if (users.getInt("online") == ONLINE)
                                    user.setStatus(ONLINE);
                                arrayList.add(user);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("查询好友失败~");
        }
        return arrayList;
    }

    /**
     * 根据uid查询用户
     *
     * @param uid 用户id
     * @return user
     */
    public User queryUserById(int uid) {
        try {
            ResultSet res = queryUsers.executeQuery();
            while (res.next()) {
                if (res.getInt("uid") == uid) {
                    return new User(res.getInt("uid"),
                            res.getString("name"),
                            res.getString("account"),
                            res.getString("password"),
                            res.getString("head"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("查询用户失败~");
        }
        return null;
    }

    /**
     * 根据用户Id查询聊天室
     *
     * @param uid
     * @return
     */
    public ArrayList<RoomInfo> queryRoomInfo(int uid) {
        ArrayList<RoomInfo> infos = new ArrayList<>();
        try {
            //查询Room表
            ResultSet rs = queryRelationship.executeQuery();
            while (rs.next()) {
                int contentId = rs.getInt("uid");
                int room_id = rs.getInt("room_id");
                //聊天室包含该uid
                if (contentId == uid) {
                    //查询该聊天室的信息，并添加到infos
                    ResultSet info = queryRoomInfo.executeQuery();
                    while (info.next()) {
                        if (info.getInt("room_id") == room_id) {
                            infos.add(new RoomInfo(
                                    info.getInt("room_id"),
                                    info.getString("room_Name"),
                                    info.getString("header"),
                                    info.getString("talkSketch"),
                                    info.getLong("timestamp"),
                                    info.getInt("port"),
                                    info.getInt("mode")
                            ));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("查询聊天室失败~");
        }
        return infos;
    }

    /**
     * @param roomId
     * @return
     */
    public RoomInfo queryRoomInfoByRoomId(int roomId) {
        try {
            ResultSet info = queryRoomInfo.executeQuery();
            while (info.next()) {
                if (info.getInt("room_id") == roomId)
                    return new RoomInfo(
                            info.getInt("room_id"),
                            info.getString("room_Name"),
                            info.getString("header"),
                            info.getString("talkSketch"),
                            info.getLong("timestamp"),
                            info.getInt("port"),
                            info.getInt("mode")
                    );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回所有聊天室信息
     *
     * @return roomInfos
     * @throws SQLException
     */
    public ArrayList<RoomInfo> getAllRoomInfo() {

        ArrayList<RoomInfo> roomInfos = new ArrayList<>();
        try {
            ResultSet rs = queryRoomInfo.executeQuery();
            while (rs.next()) {
                roomInfos.add(new RoomInfo(
                        rs.getInt("room_id"),
                        rs.getString("room_Name"),
                        rs.getString("header"),
                        rs.getString("talkSketch"),
                        rs.getLong("timestamp"),
                        rs.getInt("port"),
                        rs.getInt("mode")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roomInfos;
    }

    /**
     * 查询uid与friendId的聊天室
     *
     * @param uid      我
     * @param friendId 朋友
     * @return roomInfo
     */
    public RoomInfo queryRoomWithFriendId(int uid, int friendId) {
        //查询uid的所有聊天室
        ArrayList<RoomInfo> infos = queryRoomInfo(uid);
        //遍历聊天室
        for (RoomInfo info : infos) {
            //如果聊天室为私聊
            if (info.mode == MODE_SINGLE) {
                try {
                    //找到和friendId的聊天室
                    ResultSet rs = queryRelationship.executeQuery();
                    while (rs.next()) {
                        //包含friendId
                        if (rs.getInt("uid") == friendId) {
                            //返回聊天室信息
                            return info;
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 插入RoomInfo
     *
     * @param room_name
     */
    public int insertRoomInfo(String room_name, int mode) {
        int id = 0;
        try {
            //获取数据数量
            PreparedStatement sql = conn.prepareStatement("select count(1) from roominfo");
            ResultSet res = sql.executeQuery();
            while (res.next())
                id = res.getInt(1) + 1;

            insertRoomInfo.setInt(1, id);
            insertRoomInfo.setString(2, room_name);
            insertRoomInfo.setInt(3, id + 30000);
            insertRoomInfo.setInt(4, mode);
            insertRoomInfo.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("插入RoomInfo失败");
            id = 0;
        }
        return id;
    }

    /**
     * 插入聊天信息
     *
     * @param roomId
     * @param uid
     * @param text
     * @throws SQLException
     */
    public void insertRecord(int roomId, int uid, String text) throws SQLException {
        insertRecord.setInt(1, roomId);
        insertRecord.setLong(2, System.currentTimeMillis());
        insertRecord.setInt(3, uid);
        insertRecord.setString(4, text);
        insertRecord.executeUpdate();
    }

    /**
     * 返回聊天室记录
     *
     * @param roomId
     * @return
     * @throws SQLException
     */
    public ArrayList<Message> queryRecord(int roomId) throws SQLException {
        ArrayList<Message> messages = new ArrayList<>();
        ResultSet records = queryRecord.executeQuery();
        while (records.next()) {
            if (records.getInt("room_id") == roomId) {
                messages.add(new Message(
                        records.getInt("room_id"),
                        records.getInt("uid"),
                        records.getString("text"),
                        records.getLong("timestamp")));
            }
        }
        return messages;
    }

    /**
     * 插入用户数据
     *
     * @param users
     */
    public void insertUser(User[] users) throws SQLException {

        PreparedStatement sql
                = conn.prepareStatement("insert into user(uid,name,account,password,head) value (?,?,?,?,?)");
        for (int i = 0; i < users.length; i++) {
            sql.setInt(1, users[i].getUid());
            sql.setString(2, users[i].getName());
            sql.setString(3, users[i].getAccount());
            sql.setString(4, users[i].getPassword());
            sql.setString(5,users[i].getHeader());
            sql.executeUpdate();
        }
    }

    /**
     * 插入好友关系
     *
     * @param uid
     * @param friends_uid
     * @return
     * @throws SQLException
     */
    public int insertFriendship(int uid, int friends_uid) {
        try {
            PreparedStatement sql
                    = conn.prepareStatement("insert into friends(uid,friend_uid, status) value (?,?,?)");

            //首先在数据库中查找是否有该好友关系
            ResultSet rs = queryFriends.executeQuery();
            while (rs.next()) {
                //已存在好友关系,处理接受好友申请处理
                if (uid == rs.getInt("friend_uid")
                        && friends_uid == rs.getInt("uid")) {

                    //更新好友状态
                    Statement statement = conn.createStatement();
                    statement.execute("update friends set status = -12 where uid =" + friends_uid
                            + " and friend_uid =" + uid);
                    statement.execute("update friends set status = -12 where uid =" + uid
                            + " and friend_uid =" + friends_uid);

                    //建立聊天室
                    User me = queryUserById(uid);
                    User friend = queryUserById(friends_uid);
                    int roomId = insertRoomInfo(me.getName() + " and " + friend.getName(), MODE_SINGLE);
                    //插入聊天室关系
                    Statement insertRelationship = conn.createStatement();
                    insertRelationship.execute("insert into room(room_id, uid) VALUE"
                            + " (" + roomId + "," + uid + ")");
                    insertRelationship.execute("insert into room(room_id, uid) VALUE"
                            + " (" + roomId + "," + friends_uid + ")");

                    return roomId;
                }
            }
            //建立好友申请
            sql.setInt(1, uid);
            sql.setInt(2, friends_uid);
            sql.setInt(3, PENDING);
            sql.executeUpdate();
            sql.setInt(1, friends_uid);
            sql.setInt(2, uid);
            sql.setInt(3, PENDING);
            sql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return PENDING;
    }

    /**
     * 插入群聊关系
     *
     * @param roomId
     * @param uid
     * @throws SQLException
     */
    public void insertRoom(int roomId, int uid) {
        try {
            Statement insertRelationship = conn.createStatement();
            insertRelationship.execute("insert into room(room_id, uid) VALUE"
                    + " (" + roomId + "," + uid + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置用户状态 ture在线 false离线
     *
     * @param uid
     * @param b
     * @throws SQLException
     */
    public void setUserOnline(int uid, boolean b) throws SQLException {
        Statement statement = conn.createStatement();
        if (b) {
            statement.execute("update user set online = 1 where uid =" + uid);
        } else {
            statement.execute("update user set online = 0 where uid =" + uid);
        }
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
