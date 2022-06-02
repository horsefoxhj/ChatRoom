package ui.chat.code.data;


public class TalkBoxData {

    private int talkId;    // 对话Id
    private String talkName;  // 对话名称
    private String talkHead;  // 对话头像

    public TalkBoxData() {
    }

    public TalkBoxData(int talkId, String talkName, String talkHead) {
        this.talkId = talkId;
        this.talkName = talkName;
        this.talkHead = talkHead;
    }

    public int getTalkId() {
        return talkId;
    }

    public void setTalkId(int talkId) {
        this.talkId = talkId;
    }

    public String getTalkName() {
        return talkName;
    }

    public void setTalkName(String talkName) {
        this.talkName = talkName;
    }

    public String getTalkHead() {
        return talkHead;
    }

    public void setTalkHead(String talkHead) {
        this.talkHead = talkHead;
    }
}
