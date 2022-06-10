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
     * @param roomId ������id
     * @param roomName ����������
     * @param header ������ͷ��
     * @param talkSketch ������Ϣ
     * @param date ������Ϣ��ʱ��
     * @param port �˿ں�
     * @param mode ����1/Ⱥ��0
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
