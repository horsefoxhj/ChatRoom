package ui.chat.code;

import base.client.ClientManager;
import db.DB;
import entity.Message;
import entity.RoomInfo;
import entity.User;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import ui.chat.code.element.group_bar_friend.FriendList;
import ui.chat.code.element.group_bar_friend.GroupList;
import ui.chat.code.element.group_bar_friend.ListTag;
import ui.chat.code.element.group_bar_friend.NewFriend;

import java.sql.SQLException;
import java.util.ArrayList;

import static utils.StringUtil.isNumeric;

/**
 * 聊天窗口展示与事件
 */
public class ChatView {

    private final ChatController controller;
    private final IChatEvent chatEvent;

    public ChatView(ChatController controller, IChatEvent chatEvent) {
        this.controller = controller;
        this.chatEvent = chatEvent;
        //在好友列表中添加[新的朋友]标签
        initFriendTag();
        //初始化好友列表
        initFriendList();
        //初始化群聊列表
        initGroupList();
        //初始化聊天列表
        initTalkList();
    }

    /**
     * 好友列表添加标签[新的朋友]，点击显示输入框，用于添加朋友
     */
    private void initFriendTag() {

        //获取好友列表的Item
        ObservableList<Pane> items = controller.contactList.getItems();
        //清空好友列表的Item
        controller.contactList.getSelectionModel().clearSelection();
        //新建[新的好友]Tag
        ListTag listTag = new ListTag("新的朋友");
        //往列表中添加[新的好友]Tag
        items.add(listTag.pane());

        //添加[新的好友]Item
        NewFriend newFriend = new NewFriend();
        Pane pane = newFriend.pane();
        items.add(pane);

        //Item[新的好友]点击事件
        pane.setOnMousePressed(event -> {
            Pane newFriendPane = newFriend.newFriendPane();
            setContentPaneBox(666, "添加好友", newFriendPane);
            //清空列表数据
            controller.clearViewListSelectedAll(controller.$("friends_ListView", ListView.class), controller.$("groupListView", ListView.class));
            //创建待添加好友列表
            ListView<Pane> listView = newFriend.newFriendListView();
            listView.getItems().clear();

            //从数据库中获取申请添加的好友，并加载到列表中
            DB db = DB.getInstance();
            //获取待添加的好友
            ArrayList<User> friends_P = db.queryFriends(controller.userId, DB.PENDING);
            ArrayList<User> friends_A = db.queryFriends(controller.userId, DB.ACCEPTED);

            //添加未接受的好友
            for (User u : friends_P) {
                chatEvent.doLoadNewFriend(u, listView, DB.PENDING);
            }
            //添加已接受的好友
            for (User u : friends_A) {
                chatEvent.doLoadNewFriend(u, listView, DB.ACCEPTED);
            }
        });

        //搜索框事件，搜索好友
        TextField friendSearch = newFriend.friendSearch();
        friendSearch.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                String text = friendSearch.getText();
                //判断输入是否为数字
                if (null != text && isNumeric(text)) {
                    int friendId = Integer.parseInt(text);
                    //搜索用户
                    chatEvent.doSearchFriend(friendId);
                }
            }
        });
    }

    /**
     * 群聊Tag和群聊列表
     */
    private void initGroupList() {
        ObservableList<Pane> items = controller.contactList.getItems();
        ListTag listTag = new ListTag("群聊");
        items.add(listTag.pane());

        GroupList groupList = new GroupList();
        Pane pane = groupList.pane();
        items.add(pane);
        controller.groupsList_Pane = pane;
        controller.groupsList_ListView = groupList.listView();
        //从数据库获取群组并加载列表
        DB db = DB.getInstance();
        ArrayList<RoomInfo> roomInfos = db.queryRoomInfo(controller.userId);
        for (RoomInfo r : roomInfos) {
            if (r.mode == DB.MODE_GROUPS) {
                controller.addFriendGroup(false, r);
            }
        }
    }

    /**
     * 好友Tag和好友列表
     */
    private void initFriendList() {
        ObservableList<Pane> items = controller.contactList.getItems();
        ListTag listTag = new ListTag("好友");
        items.add(listTag.pane());

        FriendList friendList = new FriendList();
        Pane pane = friendList.pane();
        items.add(pane);
        controller.friendsList_Pane = pane;
        controller.friendsList_ListView = friendList.listView();
        //从数据库获取好友并加载到列表
        DB db = DB.getInstance();
        ArrayList<User> friends = db.queryFriends(controller.userId, DB.ACCEPTED);
        for (User u : friends) {
            controller.addFriendUser(false, u);
        }
    }

    /**
     * 初始化聊天列表
     */
    private void initTalkList() {
        DB db = DB.getInstance();
        ArrayList<RoomInfo> rooms = db.queryRoomInfo(controller.userId);
        for (RoomInfo room : rooms) {
            //初始化消息列表
            controller.addTalkBox(-1, room, false);
            //往消息列表加载聊天数据
            try {
                ArrayList<Message> messages = db.queryRecord(room.roomId);
                for (Message message : messages) {
                    if (message.uid == controller.userId)
                        controller.addTalkMsgRight(message, false, false, false);
                    else
                        controller.addTalkMsgLeft(message, false, false, false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //创建连接到该服务端口的客户端
            ClientManager.createClientThread(room.port, room.roomId, controller.userId);
        }
    }

    /**
     * group_bar_chat：填充对话列表 & 对话框名称
     *
     * @param id   用户、群组等ID
     * @param name 用户、群组等名称
     * @param node 展现面板
     */
    void setContentPaneBox(int id, String name, Node node) {
        // 填充对话列表
        Pane content_pane_box = controller.$("content_pane_box", Pane.class);
        content_pane_box.setUserData(id);
        content_pane_box.getChildren().clear();
        content_pane_box.getChildren().add(node);
        //标题
        Label info_name = controller.$("content_name", Label.class);
        info_name.setText(name);
    }
}
