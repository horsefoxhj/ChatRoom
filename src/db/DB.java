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
    private PreparedStatement insertRecord;

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

            if (!isTableExists("user")) {

                //�����û���
                statement.execute("create table user(" +
                        "uid int not null, " +
                        "name varchar(10) not null, " +
                        "account  varchar(10) not null, " +
                        "password varchar(10) not null)");

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
                System.out.println("user�����ɹ�~");
            } else System.out.println("user���Ѵ���~");

            //�������ѹ�ϵ��
//            if (!isTableExists("Friends")) {
//
//                statement.execute("create table friends(" +
//                        "uid int not null," +
//                        "friend_uid int not null)");
//                System.out.println("friends�����ɹ�~");
//            } else System.out.println("friends���Ѵ���~");

            //������������Ϣ��
//            if (!isTableExists("Group")) {
//                statement.execute("create table `group`(" +
//                        "    group_id int not null," +
//                        "    uid      int not null)");
//                System.out.println("group�����ɹ�~");
//            } else System.out.println("group���Ѵ���~");

            //���������¼��
//            if (!isTableExists("Record")) {
//                statement.execute("create table record(" +
//                        "group_id    int default 0 not null," +
//                        "timestamp   timestamp     not null," +
//                        "uid         int           not null," +
//                        "text        text          not null);");
//                System.out.println("record�����ɹ�~");
//            } else System.out.println("record���Ѵ���~");

//            insertRecord =
//                    conn.prepareStatement("insert into record(group_id,timestamp,uid,text) value (?,?,?,?)");
            isInit = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ѯ�û�����
     *
     * @param account
     * @param password
     * @return
     */
    public User queryUser(String account, String password) {

        User user = new User();
        try {
            getConnection();
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
            System.out.println("��ѯʧ��");
        }
        return null;
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
                = conn.prepareStatement("insert into friends(uid,friends_uid) value (?,?)");

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
