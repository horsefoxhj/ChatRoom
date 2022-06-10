package ui.chat.code.element.group_bar_chat;

import entity.RoomInfo;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import ui.chat.code.data.RemindCount;
import ui.chat.code.data.TalkBoxData;
import ui.chat.code.data.TalkData;
import ui.util.DateUtil;
import ui.util.Ids;

import java.util.Date;

/**
 * 对话框元素，好友对话列表框元素
 */
public class TalkListItem {

    private Pane pane;
    private Label head;
    private Label nikeName;  // 昵称
    private Label msgSketch; // 最新信息
    private Label msgData;   // 信息时间
    private Label msgRemind; // 消息提醒
    private Button delete;   // 删除对话框按钮

    private ListView<Pane> infoBoxList; // 初始化填充消息对话框

    public TalkListItem(RoomInfo roomInfo) {
        pane = new Pane();
        pane.setId(Ids.TalkListId.createTalkPaneId(roomInfo.roomId));
        pane.setUserData(new TalkBoxData(roomInfo.roomId, roomInfo.roomName, roomInfo.header));
        pane.setPrefSize(270, 80);
        pane.getStyleClass().add("talkListItem_Pane");
        ObservableList<Node> children = pane.getChildren();

        //头像
        head = new Label();
        head.setPrefSize(50, 50);
        head.setLayoutX(15);
        head.setLayoutY(15);
        head.getStyleClass().add("talkListItem_head");
        head.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/%s.png')", roomInfo.header));
        children.add(head);

        //名字
        nikeName = new Label();
        nikeName.setPrefSize(140, 25);
        nikeName.setLayoutX(80);
        nikeName.setLayoutY(15);
        nikeName.setText(roomInfo.roomName);
        nikeName.getStyleClass().add("talkListItem_name");
        children.add(nikeName);

        //最新信息
        msgSketch = new Label();
        msgSketch.setId(Ids.TalkListId.createMsgSketchId(roomInfo.roomId));
        msgSketch.setPrefSize(200, 25);
        msgSketch.setLayoutX(80);
        msgSketch.setLayoutY(40);
        msgSketch.getStyleClass().add("talkListItem_msgSketch");
        children.add(msgSketch);

        //信息时间
        msgData = new Label();
        msgData.setId(Ids.TalkListId.createMsgDataId(roomInfo.roomId));
        msgData.setPrefSize(60, 25);
        msgData.setLayoutX(220);
        msgData.setLayoutY(15);
        msgData.getStyleClass().add("talkListItem_msgData");
        children.add(msgData);
        // 填充；信息简述 & 信息时间
        fillMsgSketch(roomInfo.talkSketch, new Date(roomInfo.date));

        //消息提醒
        msgRemind = new Label();
        msgRemind.setPrefSize(15, 15);
        msgRemind.setLayoutX(60);
        msgRemind.setLayoutY(5);
        msgRemind.setUserData(new RemindCount());
        msgRemind.setText("");
        msgRemind.setVisible(false);
        msgRemind.getStyleClass().add("talkListItem_msgRemind");
        children.add(msgRemind);

        //删除对话框按钮
        delete = new Button();
        delete.setVisible(false);
        delete.setPrefSize(4, 4);
        delete.setLayoutY(26);
        delete.setLayoutX(-8);
        delete.getStyleClass().add("talkListItem_delete");
        children.add(delete);

        //消息框[初始化，未装载]，承载对话信息内容，点击按钮时候填充
        infoBoxList = new ListView<>();
        infoBoxList.setId(Ids.TalkListId.createInfoBoxListId(roomInfo.roomId));
        infoBoxList.setUserData(new TalkData(roomInfo.roomName, roomInfo.header));
        infoBoxList.setPrefSize(850, 560);
        infoBoxList.getStyleClass().add("infoBoxStyle");
    }

    public Pane pane() {
        return pane;
    }

    public ListView<Pane> infoBoxList() {
        return infoBoxList;
    }

    public Button delete() {
        return delete;
    }

    //填充最新的一条信息
    public void fillMsgSketch(String talkSketch, Date talkDate) {
        if (null != talkSketch) {
            if (talkSketch.length() > 30) talkSketch = talkSketch.substring(0, 30);
            msgSketch.setText(talkSketch);
        }
        if (null == talkDate) talkDate = new Date();
        // 格式化信息
        String talkSimpleDate = DateUtil.simpleDate(talkDate);
        msgData.setText(talkSimpleDate);
    }

    public void clearMsgSketch() {
        msgSketch.setText("");
    }

    public Label msgRemind() {
        return msgRemind;
    }
}
