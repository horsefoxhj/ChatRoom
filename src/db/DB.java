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

    // �������ݿⷽ��
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
                        + "//127.0.0.1:3306/", "root", "");
                isConnected = true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("���ݿ�����ʧ��");
            }
        }
        return conn; // ����Connection����
    }

    /**
     * ��ʼ�����ݿ⣬��������Ĭ���˺ţ�
     * {uid:"1127",name:��Hx��,account:"123456",password:"123456",record:null}
     * {uid:"1225",name:��Pj��,account:"654321",password:"654321",record:null}
     */
    private void init() {
        try {
            System.out.println("����chatroom���ݿ�...");
            //�������ݿ�chatroom
            Connection conn = getConnection();
            Statement createDatabase = conn.createStatement();
            createDatabase.execute("create database chatroom;");

        } catch (SQLException e) {
            if (e.getMessage().contains("database exists")) {
                System.out.println("chatroom���ݿ��Ѵ���~");
            } else {
                e.printStackTrace();
            }
        }

        try {
            Statement statement = conn.createStatement();
            //ѡ�����ݿ�chatroom
            statement.execute("use chatroom");
            //�����û���
            statement.execute("create table user(" +
                    "uid int not null, " +
                    "name varchar(10) not null, " +
                    "account  varchar(10) not null, " +
                    "password varchar(10) not null," +
                    "record int null);");

            //��ʼ���û�
            User[] users = {
                    new User(1127, "Hx", "123456", "123456"),
                    new User(1225, "Pj", "654321", "654321")
            };
            insertUser(users);
            System.out.println("���ݿ��ʼ���ɹ�~");
            isInit = true;

        } catch (SQLException e) {
            if (e.getMessage().contains("Table 'user' already exists")) {
                System.out.println("User���Ѵ���~");
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

    //��ѯ�û�����
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
            System.out.println("��ѯʧ��");
        }
        return null;
    }

    //�����û�����
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
