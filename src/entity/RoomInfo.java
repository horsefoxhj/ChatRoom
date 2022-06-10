package entity;

/**
 * @Author Hx
 * @Date 2022/6/3 14:19
 * @Describe RoomInfo
 */
public class RoomInfo {
    public int roomId;
    public String roomName;
    public String header;
    public String talkSketch;
    public int port;
    public long date;
    public int mode;

    /**
     *
     * @param roomId 聊天室id
     * @param roomName 聊天室名字
     * @param header 聊天室头像
     * @param talkSketch 最新信息
     * @param date 最新信息的时间
     * @param port 端口号
     * @param mode 好友1/群聊0
     */
    public RoomInfo(int roomId, String roomName, String header, String talkSketch,
                    long date, int port, int mode) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.header = header;
        this.talkSketch = talkSketch;
        this.date = date;
        this.port = port;
        this.mode = mode;
    }
}
