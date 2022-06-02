package db;

import entity.User;

import java.sql.*;
import java.util.ArrayList;

/**
 * @Author Hx
 * @Date 2022/5/23 18:37
 * @Describe ���ݿ������
 */
public class DB {
    //MySQL����
    private final static String DRIVER = "com.mysql.cj.jdbc.Driver";
    private final static int SUCCESS = 1;
    private final static int ERROR = 0;
    //�����ܵĺ���
    private final static int PENDING = 1;
    //�ѽ��ܵĺ���
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
     * �������ݿ�
     *
     * @return
     */
    private Connection getConnection() {
        //������������
        if (!driverLoaded) {
            try {
                Class.forName(DRIVER);
                System.out.println("���ݿ��������سɹ�~");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("���ݿ��������ش���~");
            }
        }
        //�������ݿ�����
        if (!isConnected) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql:"
                        + "//127.0.0.1:3306", "root", "root");
                isConnected = true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("���ݿ�����ʧ��");
            }
        }
        return conn; // ����Connection����
    }

    /**
     * ��ʼ�����ݿ⣬����user��װ����Ĭ���˺ţ��������ѹ�ϵӳ�����������Ϣ��
     */
    private void init() {
        //����chatroom���ݿ�
        try {
            System.out.println("����chatroom���ݿ�...");
            getConnection();
            Statement createDatabase = conn.createStatement();
            //�������ݿ�
            createDatabase.execute("create database chatroom;");

        } catch (SQLException e) {
            if (e.getMessage().contains("database exists")) {
                System.out.println("chatroom���ݿ��Ѵ���~");
            } else {
                e.printStackTrace();
            }
        }

        //��ʼ���û����������ѹ�ϵӳ�����������Ϣ��
        try {
            Statement statement = conn.createStatement();
            //ѡ�����ݿ�chatroom
            statement.execute("use chatroom");
            //�����û���
            statement.execute("create table if not exists user(" +
                    "uid int not null, " +
                    "name varchar(10) not null, " +
                    "account  varchar(10) not null, " +
                    "password varchar(10) not null," +
                    "head varchar(10) default 'header')");

            statement.execute("truncate table user");
            //��ʼ���û�
            User[] users = {
                    new User(1127, "С��", "123456", "123456"),
                    new User(1225, "С��", "654321", "654321"),
                    new User(510, "Сл", "111111", "111111"),
                    new User(1124, "С��", "222222", "222222"),
                    new User(717, "С��", "101010", "101010"),
                    new User(53, "С��", "666666", "666666"),
            };
            insertUser(users);
            System.out.println("user���ʼ���ɹ�~");

            //�������ѹ�ϵ��
            statement.execute("create table if not exists friends(" +
                    "uid int not null," +
                    "friend_uid int not null," +
                    "status int not null);");
            System.out.println("friends���ʼ���ɹ�~");

            //������������Ϣ��
            statement.execute("create table if not exists roomInfo(" +
                    "room_id int not null auto_increment primary key," +
                    "header  varchar(10) default 'header'," +
                    "port    int not null)auto_increment=1;");
            System.out.println("room���ʼ���ɹ�~");

            //���������ҹ�ϵ��
            statement.execute("create table if not exists room(" +
                    "room_id int not null," +
                    "uid     int not null," +
                    "port    int not null);");
            //���������ҹ�ϵ�����������Ϣ��
            statement.execute("alter table room " +
                    "add constraint room_roominfo_room_id_fk " +
                    "foreign key (room_id) references roominfo (room_id)" +
                    "on update cascade on delete cascade;");
            System.out.println("room���ʼ���ɹ�~");

            //���������¼��
            statement.execute("create table if not exists record(" +
                    "room_id     int default 0 not null," +
                    "timestamp   timestamp     not null," +
                    "uid         int           not null," +
                    "text        text          not null);");
            //������¼�����������Ϣ��
            statement.execute("alter table record " +
                    "add constraint record_roominfo_room_id_fk " +
                    "foreign key (room_id) references roominfo (room_id)" +
                    "on update cascade on delete cascade;");
            System.out.println("record���ʼ���ɹ�~");

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
     * ��¼
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
            System.out.println("��ѯʧ��");
        }
        return null;
    }

    /**
     * ��ѯ����uid��Ӧ�û������д���Ӻ���
     *
     * @param uid
     * @return
     */
    public ArrayList<User> getNewFriends(int uid) {
        ArrayList<User> arrayList = new ArrayList<>();
        try {

            ResultSet friends = queryFriends.executeQuery();
            ResultSet users = queryUsers.executeQuery();
            //ѭ���������д���Ӻ���
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
            System.out.println("��ѯʧ��");
        }
        return arrayList;
    }

    /**
     * ����uid��ѯ�û�
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
            System.out.println("��ѯ�û�ʧ��~");
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
            System.out.println("��ѯ������ʧ��~");
        }
        return integers;
    }

    /**
     * ����������Ϣ
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
     * �����û�����
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
     * ������ѹ�ϵ
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
     * ���xxx���Ƿ����
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    private boolean isTableExists(String tableName) throws SQLException {

        getConnection();
        Statement statement = conn.createStatement();
        //ѡ�����ݿ�chatroom
        statement.execute("use chatroom");
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, tableName, null);
        return resultSet.next();
    }

    private static class DBHolder {
        public static final DB instance = new DB();
    }
}
