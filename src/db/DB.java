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
 * @Describe ���ݿ������
 */
public class DB {

    public final static int PENDING = -11; //�����ܵĺ���
    public final static int ACCEPTED = -12;//�ѽ��ܵĺ���
    public final static int ALL = 3;//ȫ������
    public final static int MODE_SINGLE = 1;//����
    public final static int MODE_GROUPS = 2;//Ⱥ��
    private final static String DRIVER = "com.mysql.cj.jdbc.Driver"; //MySQL����
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
        Statement statement = null;
        try {
            statement = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            //ѡ�����ݿ�chatroom
            statement.execute("use chatroom");
            //�����û���
            statement.execute("create table if not exists user(" +
                    "uid        int not null, " +
                    "name       varchar(10) not null, " +
                    "account    varchar(10) not null, " +
                    "password   varchar(10) not null," +
                    "head       varchar(10) default 'header'," +
                    "online     int not null default 0)");

            statement.execute("truncate table user");
            //��ʼ���û�
            User[] users = {
                    new User(1127, "С��", "123456", "123456", "header"),
                    new User(1225, "С��", "654321", "654321", "pan"),
                    new User(717, "С��", "101010", "101010", "chuan")};
            insertUser(users);
            System.out.println("user���ʼ���ɹ�~");

            //�������ѹ�ϵ��
            statement.execute("create table if not exists friends(" +
                    "uid        int not null," +
                    "friend_uid int not null," +
                    "status     int not null);");
            System.out.println("friends���ʼ���ɹ�~");

            //������������Ϣ��
            statement.execute("create table if not exists roomInfo(" +
                    "room_id    int not null primary key," +
                    "room_Name  varchar(10) not null," +
                    "talkSketch text," +
                    "timestamp  bigint not null default 0," +
                    "header     varchar(10) default 'header'," +
                    "port       int not null," +
                    "mode       int not null default 0);");
            System.out.println("roomInfo���ʼ���ɹ�~");

            //���������ҹ�ϵ��
            statement.execute("create table if not exists room(" +
                    "room_id int not null," +
                    "uid     int not null);");

            //���������¼��
            statement.execute("create table if not exists record(" +
                    "room_id     int default 0 not null," +
                    "timestamp   bigint        not null," +
                    "uid         int           not null," +
                    "text        text          not null);");

            statement.execute("insert into roominfo(room_id, room_Name, header, port, mode) " +
                    "VALUE (1,'�����మ����','header',30001,2)on duplicate key update port = 30001");
            statement.execute("insert into roominfo(room_id, room_Name, header, port, mode) " +
                    "VALUE (2,'������','header',30002,2)on duplicate key update port = 30002");

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
            //���������ҹ�ϵ�����������Ϣ��
            statement.execute("alter table room " +
                    "add constraint room_roominfo_room_id_fk " +
                    "foreign key (room_id) references roominfo (room_id)" +
                    "on update cascade on delete cascade;");
            System.out.println("room���ʼ���ɹ�~");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            //������¼�����������Ϣ��
            statement.execute("alter table record " +
                    "add constraint record_roominfo_room_id_fk " +
                    "foreign key (room_id) references roominfo (room_id)" +
                    "on update cascade on delete cascade;");
            System.out.println("record���ʼ���ɹ�~");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            //��ʼ���������
            insertRecord =
                    conn.prepareStatement("insert into record(room_id,timestamp,uid,text) value (?,?,?,?)");
            insertRoomInfo =
                    conn.prepareStatement("insert into roominfo(room_id, room_Name, port, mode) " +
                            "value (?,?,?,?)");

            //��ʼ����ѯ���
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
     * ��¼
     *
     * @param account  �˺�
     * @param password ����
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
            System.out.println("��ѯʧ��");
        }
        return null;
    }

    /**
     * ��ѯ����uid��Ӧ�û���״̬Ϊstatus����,[ALL]��ѯ���к���
     *
     * @param uid    �û�id
     * @param status �û�״̬
     * @return ArrayList<User>
     */
    public ArrayList<User> queryFriends(int uid, int status) {
        ArrayList<User> arrayList = new ArrayList<>();
        try {
            //ѭ���������к���
            ResultSet friends = queryFriends.executeQuery();
            while (friends.next()) {
                //�Ǹ�uid�ĺ���
                int f_status = friends.getInt("status");
                if (friends.getInt("uid") == uid) {
                    //���statusΪALL���ѯ���к��ѣ�����ֻ��ѯ״̬Ϊstatus�ĺ���
                    if (status == ALL || f_status == status) {
                        int friends_id = friends.getInt("friend_uid");
                        //��ѯ�ú��ѵ���Ϣ
                        ResultSet users = queryUsers.executeQuery();
                        while (users.next()) {
                            //���ú��ѵ����ݴ����б�
                            if (users.getInt("uid") == friends_id) {
                                User user = new User(users.getInt("uid"),
                                        users.getString("name"),
                                        users.getString("account"),
                                        users.getString("password"),
                                        users.getString("head"));
                                //���ú���״̬���Ƿ�����
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
            System.out.println("��ѯ����ʧ��~");
        }
        return arrayList;
    }

    /**
     * ����uid��ѯ�û�
     *
     * @param uid �û�id
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
            System.out.println("��ѯ�û�ʧ��~");
        }
        return null;
    }

    /**
     * �����û�Id��ѯ������
     *
     * @param uid
     * @return
     */
    public ArrayList<RoomInfo> queryRoomInfo(int uid) {
        ArrayList<RoomInfo> infos = new ArrayList<>();
        try {
            //��ѯRoom��
            ResultSet rs = queryRelationship.executeQuery();
            while (rs.next()) {
                int contentId = rs.getInt("uid");
                int room_id = rs.getInt("room_id");
                //�����Ұ�����uid
                if (contentId == uid) {
                    //��ѯ�������ҵ���Ϣ������ӵ�infos
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
            System.out.println("��ѯ������ʧ��~");
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
     * ����������������Ϣ
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
     * ��ѯuid��friendId��������
     *
     * @param uid      ��
     * @param friendId ����
     * @return roomInfo
     */
    public RoomInfo queryRoomWithFriendId(int uid, int friendId) {
        //��ѯuid������������
        ArrayList<RoomInfo> infos = queryRoomInfo(uid);
        //����������
        for (RoomInfo info : infos) {
            //���������Ϊ˽��
            if (info.mode == MODE_SINGLE) {
                try {
                    //�ҵ���friendId��������
                    ResultSet rs = queryRelationship.executeQuery();
                    while (rs.next()) {
                        //����friendId
                        if (rs.getInt("uid") == friendId) {
                            //������������Ϣ
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
     * ����RoomInfo
     *
     * @param room_name
     */
    public int insertRoomInfo(String room_name, int mode) {
        int id = 0;
        try {
            //��ȡ��������
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
            System.out.println("����RoomInfoʧ��");
            id = 0;
        }
        return id;
    }

    /**
     * ����������Ϣ
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
     * ���������Ҽ�¼
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
     * �����û�����
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
     * ������ѹ�ϵ
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

            //���������ݿ��в����Ƿ��иú��ѹ�ϵ
            ResultSet rs = queryFriends.executeQuery();
            while (rs.next()) {
                //�Ѵ��ں��ѹ�ϵ,������ܺ������봦��
                if (uid == rs.getInt("friend_uid")
                        && friends_uid == rs.getInt("uid")) {

                    //���º���״̬
                    Statement statement = conn.createStatement();
                    statement.execute("update friends set status = -12 where uid =" + friends_uid
                            + " and friend_uid =" + uid);
                    statement.execute("update friends set status = -12 where uid =" + uid
                            + " and friend_uid =" + friends_uid);

                    //����������
                    User me = queryUserById(uid);
                    User friend = queryUserById(friends_uid);
                    int roomId = insertRoomInfo(me.getName() + " and " + friend.getName(), MODE_SINGLE);
                    //���������ҹ�ϵ
                    Statement insertRelationship = conn.createStatement();
                    insertRelationship.execute("insert into room(room_id, uid) VALUE"
                            + " (" + roomId + "," + uid + ")");
                    insertRelationship.execute("insert into room(room_id, uid) VALUE"
                            + " (" + roomId + "," + friends_uid + ")");

                    return roomId;
                }
            }
            //������������
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
     * ����Ⱥ�Ĺ�ϵ
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
     * �����û�״̬ ture���� false����
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
