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
import ui.chat.code.element.group_bar_chat.MsgBox;
import ui.chat.code.element.group_bar_chat.TalkListItem;
import ui.chat.code.element.group_bar_friend.FriendListItem;
import ui.util.CacheUtil;
import ui.util.Ids;

import java.io.IOException;

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
        ItemPane.setOnMouseEntered(event -> {
            newTalkListItem.delete().setVisible(true);
        });
        ItemPane.setOnMouseExited(event -> {
            newTalkListItem.delete().setVisible(false);
        });

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
        chatView.updateTalkListIdxAndSelected(talkListItem.pane(), talkListItem.msgRemind(), idxFirst, selected, isRemind);
    }

    @Override
    public void addTalkMsgRight(Message message, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        TalkListItem talkListItem = CacheUtil.talkMap.get(message.roomId);
        ListView<Pane> listView = talkListItem.infoBoxList();
        Pane right = new MsgBox().right(userHeader, message.text);
        // 消息填充
        listView.getItems().add(right);
        // 滚动条
        listView.scrollTo(right);
        // 设置位置&选中
        chatView.updateTalkListIdxAndSelected(talkListItem.pane(), talkListItem.msgRemind(), idxFirst, selected, isRemind);
    }

//    @Override
//    public void addTalkMsgGroupLeft(String talkId, String userId, String userNickName, String userHead, String msg, Integer msgType, Date msgDate, Boolean idxFirst, Boolean selected, Boolean isRemind) {
//        // 自己的消息抛弃
//        if (super.userId.equals(userId)) return;
//        TalkListItem talkElement = CacheUtil.talkMap.get(talkId);
//        if (null == talkElement) {
//            GroupsData groupsData = (GroupsData) $(Ids.TalkListId.createFriendGroupId(talkId), Pane.class).getUserData();
//            if (null == groupsData) return;
//            addTalkBox(0, 1, talkId, groupsData.getGroupName(), groupsData.getGroupHead(), userNickName + "：" + msg, msgDate, false);
//            talkElement = CacheUtil.talkMap.get(talkId);
//            // 事件通知(开启与群组发送消息)
//            chatEvent.doEventAddTalkGroup(super.userId, talkId);
//        }
//        ListView<Pane> listView = talkElement.infoBoxList();
//        Pane left = new MsgBox().left(userNickName, userHead, msg, msgType);
//        // 消息填充
//        listView.getItems().add(left);
//        // 滚动条
//        listView.scrollTo(left);
//        talkElement.fillMsgSketch(0 == msgType ? userNickName + "：" + msg : userNickName + "：[表情]", msgDate);
//        // 设置位置&选中
//        chatView.updateTalkListIdxAndSelected(1, talkElement.pane(), talkElement.msgRemind(), idxFirst, selected, isRemind);
//    }

//    @Override
//    public void addFriendGroup(String groupId, String groupName, String groupHead) {
//        ElementFriendGroup elementFriendGroup = new ElementFriendGroup(groupId, groupName, groupHead);
//        Pane pane = elementFriendGroup.pane();
//        // 添加到群组列表
//        ListView<Pane> groupListView = $("groupListView", ListView.class);
//        ObservableList<Pane> items = groupListView.getItems();
//        items.add(pane);
//        groupListView.setPrefHeight(80 * items.size());
//        $("friendGroupList", Pane.class).setPrefHeight(80 * items.size());
//
//        // 群组，内容框[初始化，未装载]，承载群组信息内容，点击按钮时候填充
//        Pane detailContent = new Pane();
//        detailContent.setPrefSize(850, 560);
//        detailContent.getStyleClass().add("friendGroupDetailContent");
//        ObservableList<Node> children = detailContent.getChildren();
//
//        Button sendMsgButton = new Button();
//        sendMsgButton.setId(groupId);
//        sendMsgButton.getStyleClass().add("friendGroupSendMsgButton");
//        sendMsgButton.setPrefSize(176, 50);
//        sendMsgButton.setLayoutX(337);
//        sendMsgButton.setLayoutY(450);
//        sendMsgButton.setText("发送消息");
//        chatEventDefine.doEventOpenFriendGroupSendMsg(sendMsgButton, groupId, groupName, groupHead);
//        children.add(sendMsgButton);
//
//        // 添加监听事件
//        pane.setOnMousePressed(event -> {
//            clearViewListSelectedAll($("friendList", ListView.class), $("userListView", ListView.class));
//            chatView.setContentPaneBox(groupId, groupName, detailContent);
//        });
//        chatView.setContentPaneBox(groupId, groupName, detailContent);
//    }

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

        chatEventDefine.switchFriendTalkPane(sendMsgButton, user.getUid());

        children.add(sendMsgButton);
        // 添加监听事件
        pane.setOnMousePressed(event -> {
            clearViewListSelectedAll($("friendList", ListView.class), $("groupListView", ListView.class));
            chatView.setContentPaneBox(user.getUid(), user.getName(), detailContent);
        });
    }
}
