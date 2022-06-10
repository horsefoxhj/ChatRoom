package entity;

/**
 * @Author Hx
 * @Date 2022/5/16 1:04
 */
public class Message {
    public int roomId;
    public String text;
    public long timeStamp;
    public int uid;

    public Message(int roomId, int uid, String text, long timeStamp) {
        this.roomId = roomId;
        this.text = text;
        this.timeStamp = timeStamp;
        this.uid = uid;
    }
}
