package ui.chat.code;

import db.DB;
import entity.Message;
import entity.RoomInfo;
import entity.User;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import ui.chat.code.data.RemindCount;
import ui.chat.code.data.TalkBoxData;
import ui.chat.code.element.group_bar_chat.MsgBox;
import ui.chat.code.element.group_bar_chat.TalkListItem;
import ui.chat.code.element.group_bar_friend.FriendListItem;
import ui.chat.code.element.group_bar_friend.GroupListItem;
import ui.util.CacheUtil;
import ui.util.Ids;

import java.io.IOException;
import java.util.Date;

public class ChatController extends Chat implements IChatMethod {

    private ChatView chatView;
    private ChatEventDefine chatEventDefine;

    /**
     * 设置登陆用户
     */
    public ChatController(User user) throws IOException {
        super(user);
        //设置头像
        Button button = $("bar_portrait", Button.class);
        button.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/%s.png')", user.getHeader()));
    }

    @Override
    public void initView() {
        chatView = new ChatView(this, chatEventDefine);
    }

    @Override
    public void initEventDefine() {
        chatEventDefine = new ChatEventDefine(this, this);
    }

    @Override
    public void doShow() {
        show();
    }

    /**
     * 填充对话框列表
     *
     * @param position 对话框位置；首位0、默认-1
     * @param roomInfo 聊天室信息
     * @param selected 选中[true/false]
     */
    @Override
    public void addTalkBox(int position, RoomInfo roomInfo, Boolean selected) {

        //填充到对话框
        ListView<Pane> talkList = $("talkList", ListView.class);
        //判断会话框是否有该对象
        TalkListItem talkListItem = CacheUtil.talkMap.get(roomInfo.roomId);
        //会话列表已经含有该对象
        if (null != talkListItem) {
            Node talkNode = talkList.lookup("#" + Ids.TalkListId.createTalkPaneId(roomInfo.roomId));

            //该对象还未填充到列表中
            if (null == talkNode) {
                talkList.getItems().add(position, talkListItem.pane());
                //填充对话框消息栏
                fillInfoBox(talkListItem, roomInfo.roomName);
            }
            if (selected) {
                //设置选中
                talkList.getSelectionModel().select(talkListItem.pane());
            }
            //填充对话框消息栏
            fillInfoBox(talkListItem, roomInfo.roomName);
            return;
        }

        //初始化对话框元素
        TalkListItem newTalkListItem = new TalkListItem(roomInfo);
        CacheUtil.talkMap.put(roomInfo.roomId, newTalkListItem);
        // 填充到对话框
        ObservableList<Pane> items = talkList.getItems();
        Pane ItemPane = newTalkListItem.pane();
        if (position >= 0) {
            items.add(position, ItemPane);  // 添加到第一个位置
        } else {
            items.add(ItemPane);           // 顺序添加
        }
        if (selected) {
            talkList.getSelectionModel().select(ItemPane);
        }

        //对话框元素点击事件
        ItemPane.setOnMousePressed(event -> {
            //填充消息栏
            fillInfoBox(newTalkListItem, roomInfo.roomName);
            //清除消息提醒
            Label msgRemind = newTalkListItem.msgRemind();
            msgRemind.setUserData(new RemindCount(0));
            msgRemind.setVisible(false);
        });

        //鼠标事件[移入/移出]
        ItemPane.setOnMouseEntered(event -> newTalkListItem.delete().setVisible(true));
        ItemPane.setOnMouseExited(event -> newTalkListItem.delete().setVisible(false));

        //填充对话框消息栏
        fillInfoBox(newTalkListItem, roomInfo.roomName);
        //从对话框中删除
        newTalkListItem.delete().setOnMouseClicked(event -> {
            talkList.getItems().remove(ItemPane);
            $("info_pane_box", Pane.class).getChildren().clear();
            $("info_pane_box", Pane.class).setUserData(null);
            $("info_name", Label.class).setText("");
            newTalkListItem.infoBoxList().getItems().clear();
            newTalkListItem.clearMsgSketch();
            chatEventDefine.doDelTalkUser(super.userId, roomInfo.roomId);
        });
    }

    /**
     * 填充对话框消息内容
     *
     * @param talkListItem 对话框元素
     * @param roomName     对话框名称
     */
    private void fillInfoBox(TalkListItem talkListItem, String roomName) {
        String roomId = talkListItem.pane().getUserData().toString();
        //填充对话列表
        Pane info_pane_box = $("info_pane_box", Pane.class);
        String boxUserId = (String) info_pane_box.getUserData();
        //判断是否已经填充[talkId]，已填充则返回
        if (roomId.equals(boxUserId)) return;

        ListView<Pane> listView = talkListItem.infoBoxList();
        info_pane_box.setUserData(roomId);
        info_pane_box.getChildren().clear();
        info_pane_box.getChildren().add(listView);
        //对话框名称
        Label info_name = $("info_name", Label.class);
        info_name.setText(roomName);
    }

    @Override
    public void addTalkMsgLeft(Message message, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        TalkListItem talkListItem = CacheUtil.talkMap.get(message.roomId);
        talkListItem.fillMsgSketch(message.text,new Date(message.timeStamp));
        ListView<Pane> listView = talkListItem.infoBoxList();
        //查询用户
        User user = DB.getInstance().queryUserById(message.uid);
        //创建聊天气泡
        Pane left = new MsgBox().left(user.getName(), user.getHeader(), message.text);
        // 消息填充
        listView.getItems().add(left);
        // 滚动条
        listView.scrollTo(left);
        // 设置位置&选中
        updateTalkListIdxAndSelected(talkListItem.pane(), talkListItem.msgRemind(), idxFirst, selected, isRemind);
    }

    @Override
    public void addTalkMsgRight(Message message, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        TalkListItem talkListItem = CacheUtil.talkMap.get(message.roomId);
        talkListItem.fillMsgSketch(message.text,new Date(message.timeStamp));
        ListView<Pane> listView = talkListItem.infoBoxList();
        Pane right = new MsgBox().right(userHeader, message.text);
        // 消息填充
        listView.getItems().add(right);
        // 滚动条
        listView.scrollTo(right);
        // 设置位置&选中
        updateTalkListIdxAndSelected(talkListItem.pane(), talkListItem.msgRemind(), idxFirst, selected, isRemind);
    }

    @Override
    public void addFriendGroup(boolean selected, RoomInfo roomInfo) {
        GroupListItem groupListItem = new GroupListItem(roomInfo.roomId, roomInfo.roomName, roomInfo.header);
        Pane pane = groupListItem.pane();
        // 添加到群组列表
        ListView<Pane> groupListView = groupsList_ListView;
        ObservableList<Pane> items = groupListView.getItems();
        items.add(pane);
        groupListView.setPrefHeight(80 * items.size());
        groupsList_Pane.setPrefHeight(80 * items.size());
        //选中
        if (selected) {
            groupListView.getSelectionModel().select(pane);
        }

        // 群组，内容框[初始化，未装载]，承载群组信息内容，点击按钮时候填充
        Pane detailContent = new Pane();
        detailContent.setPrefSize(850, 560);
        detailContent.getStyleClass().add("friendGroupDetailContent");
        ObservableList<Node> children = detailContent.getChildren();

        Button sendMsgButton = new Button();
        sendMsgButton.setId(roomInfo.roomId + "");
        sendMsgButton.getStyleClass().add("friendGroupSendMsgButton");
        sendMsgButton.setPrefSize(176, 50);
        sendMsgButton.setLayoutX(337);
        sendMsgButton.setLayoutY(450);
        sendMsgButton.setText("发送消息");
        chatEventDefine.switch2TalkPaneG(sendMsgButton, roomInfo);
        children.add(sendMsgButton);

        // 添加监听事件
        pane.setOnMousePressed(event -> {
            clearViewListSelectedAll($("friendList", ListView.class), $("groupListView", ListView.class));
            chatView.setContentPaneBox(roomInfo.roomId, roomInfo.roomName, detailContent);
        });
    }

    /**
     * 往好友列表填充好友
     *
     * @param selected 选中;true/false
     * @param user     用户信息
     */
    @Override
    public void addFriendUser(boolean selected, User user) {
        FriendListItem friendListItem = new FriendListItem(user.getUid(), user.getName(), user.getHeader());
        Pane pane = friendListItem.pane();
        // 添加到好友列表
        ListView<Pane> userListView = friendsList_ListView;
        ObservableList<Pane> items = userListView.getItems();
        items.add(pane);
        userListView.setPrefHeight(80 * items.size());
        friendsList_Pane.setPrefHeight(80 * items.size());
        // 选中
        if (selected) {
            userListView.getSelectionModel().select(pane);
        }

        // 好友，内容框[初始化，未装载]，承载好友信息内容，点击按钮时候填充
        Pane detailContent = new Pane();
        detailContent.setPrefSize(850, 560);
        detailContent.getStyleClass().add("friendUserDetailContent");
        ObservableList<Node> children = detailContent.getChildren();

        Button sendMsgButton = new Button();
        sendMsgButton.setId(user.getUid() + "");
        sendMsgButton.getStyleClass().add("friendUserSendMsgButton");
        sendMsgButton.setPrefSize(176, 50);
        sendMsgButton.setLayoutX(337);
        sendMsgButton.setLayoutY(450);
        sendMsgButton.setText("发送消息");

        //查询聊天室
        DB db = DB.getInstance();
        RoomInfo roomInfo = db.queryRoomWithFriendId(userId, user.getUid());
        chatEventDefine.switch2TalkPaneG(sendMsgButton, roomInfo);
        children.add(sendMsgButton);

        // 添加监听事件
        pane.setOnMousePressed(event -> {
            clearViewListSelectedAll($("friendList", ListView.class), $("groupListView", ListView.class));
            chatView.setContentPaneBox(user.getUid(), user.getName(), detailContent);
        });
    }

    /**
     * @param talkPane       对话框元素面板
     * @param msgRemindLabel 消息提醒标签
     * @param idxFirst       是否设置首位
     * @param selected       是否选中
     * @param isRemind       是否提醒
     * @Describe 更新对话框列表元素位置指定并选中[在聊天消息发送时触达]
     */
    void updateTalkListIdxAndSelected(Pane talkPane, Label msgRemindLabel, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        // 对话框ID、好友ID
        TalkBoxData talkBoxData = (TalkBoxData) talkPane.getUserData();
        // 填充到对话框
        ListView<Pane> talkList = $("talkList", ListView.class);
        // 对话空为空，初始化[置顶、选中、提醒]
        if (talkList.getItems().isEmpty()) {
            if (idxFirst) {
                talkList.getItems().add(0, talkPane);
            }
            if (selected) {
                // 设置对话框[√选中]
                talkList.getSelectionModel().select(talkPane);
            }
            isRemind(msgRemindLabel, isRemind);
            return;
        }

        // 对话空不为空，判断第一个元素是否当前聊天Pane
        Pane firstPane = talkList.getItems().get(0);
        // 判断元素是否在首位，如果是首位可返回不需要重新设置首位
        if (talkBoxData.getRoomId() == ((TalkBoxData) firstPane.getUserData()).getRoomId()) {
            Pane selectedItem = talkList.getSelectionModel().getSelectedItem();
            // 选中判断；如果第一个元素已经选中[说明正在会话]，那么清空消息提醒
            if (null == selectedItem) {
                isRemind(msgRemindLabel, isRemind);
                return;
            }
            TalkBoxData selectedItemUserData = (TalkBoxData) selectedItem.getUserData();
            if (null != selectedItemUserData && talkBoxData.getRoomId() == selectedItemUserData.getRoomId()) {
                clearRemind(msgRemindLabel);
            } else {
                isRemind(msgRemindLabel, isRemind);
            }
            return;
        }
        if (idxFirst) {
            talkList.getItems().remove(talkPane);
            talkList.getItems().add(0, talkPane);
        }
        if (selected) {
            // 设置对话框[√选中]
            talkList.getSelectionModel().select(talkPane);
        }
        isRemind(msgRemindLabel, isRemind);
    }

    /**
     * 消息提醒
     *
     * @param msgRemindLabel 消息面板
     */
    private void isRemind(Label msgRemindLabel, Boolean isRemind) {
        if (!isRemind) return;
        msgRemindLabel.setVisible(true);
        RemindCount remindCount = (RemindCount) msgRemindLabel.getUserData();
        // 超过10个展示省略号
        if (remindCount.getCount() > 99) {
            msgRemindLabel.setText("···");
            return;
        }
        int count = remindCount.getCount() + 1;
        msgRemindLabel.setUserData(new RemindCount(count));
        msgRemindLabel.setText(String.valueOf(count));
    }

    private void clearRemind(Label msgRemindLabel) {
        msgRemindLabel.setVisible(false);
        msgRemindLabel.setUserData(new RemindCount(0));
    }
}
