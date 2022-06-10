package ui.chat.code.data;


public class TalkBoxData {

    private int roomId;    // 对话Id
    private String roomName;  // 对话名称
    private String roomHeader;  // 对话头像
    private Integer talkType; // 对话类型

    public TalkBoxData(int roomId, String talkName, String talkHead) {
        this.roomId = roomId;
        this.roomName = talkName;
        this.roomHeader = talkHead;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomHeader() {
        return roomHeader;
    }

    public void setRoomHeader(String roomHeader) {
        this.roomHeader = roomHeader;
    }
}
