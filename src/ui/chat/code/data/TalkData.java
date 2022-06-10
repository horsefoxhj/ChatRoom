package ui.chat.code.data;

/**
 * 对话框用户数据
 */
public class TalkData {

    private String roomName;
    private String roomHeader;

    public TalkData(){}

    public TalkData(String roomName, String talkHead) {
        this.roomName = roomName;
        this.roomHeader = talkHead;
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
