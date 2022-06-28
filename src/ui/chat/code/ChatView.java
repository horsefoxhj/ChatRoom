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
 * ���촰��չʾ���¼�
 */
public class ChatView {

    private final ChatController controller;
    private final IChatEvent chatEvent;

    public ChatView(ChatController controller, IChatEvent chatEvent) {
        this.controller = controller;
        this.chatEvent = chatEvent;
        //�ں����б������[�µ�����]��ǩ
        initFriendTag();
        //��ʼ�������б�
        initFriendList();
        //��ʼ��Ⱥ���б�
        initGroupList();
        //��ʼ�������б�
        initTalkList();
    }

    /**
     * �����б���ӱ�ǩ[�µ�����]�������ʾ����������������
     */
    private void initFriendTag() {

        //��ȡ�����б��Item
        ObservableList<Pane> items = controller.contactList.getItems();
        //��պ����б��Item
        controller.contactList.getSelectionModel().clearSelection();
        //�½�[�µĺ���]Tag
        ListTag listTag = new ListTag("�µ�����");
        //���б������[�µĺ���]Tag
        items.add(listTag.pane());

        //���[�µĺ���]Item
        NewFriend newFriend = new NewFriend();
        Pane pane = newFriend.pane();
        items.add(pane);

        //Item[�µĺ���]����¼�
        pane.setOnMousePressed(event -> {
            Pane newFriendPane = newFriend.newFriendPane();
            setContentPaneBox(666, "��Ӻ���", newFriendPane);
            //����б�����
            controller.clearViewListSelectedAll(controller.$("friends_ListView", ListView.class), controller.$("groupListView", ListView.class));
            //��������Ӻ����б�
            ListView<Pane> listView = newFriend.newFriendListView();
            listView.getItems().clear();

            //�����ݿ��л�ȡ������ӵĺ��ѣ������ص��б���
            DB db = DB.getInstance();
            //��ȡ����ӵĺ���
            ArrayList<User> friends_P = db.queryFriends(controller.userId, DB.PENDING);
            ArrayList<User> friends_A = db.queryFriends(controller.userId, DB.ACCEPTED);

            //���δ���ܵĺ���
            for (User u : friends_P) {
                chatEvent.doLoadNewFriend(u, listView, DB.PENDING);
            }
            //����ѽ��ܵĺ���
            for (User u : friends_A) {
                chatEvent.doLoadNewFriend(u, listView, DB.ACCEPTED);
            }
        });

        //�������¼�����������
        TextField friendSearch = newFriend.friendSearch();
        friendSearch.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                String text = friendSearch.getText();
                //�ж������Ƿ�Ϊ����
                if (null != text && isNumeric(text)) {
                    int friendId = Integer.parseInt(text);
                    //�����û�
                    chatEvent.doSearchFriend(friendId);
                }
            }
        });
    }

    /**
     * Ⱥ��Tag��Ⱥ���б�
     */
    private void initGroupList() {
        ObservableList<Pane> items = controller.contactList.getItems();
        ListTag listTag = new ListTag("Ⱥ��");
        items.add(listTag.pane());

        GroupList groupList = new GroupList();
        Pane pane = groupList.pane();
        items.add(pane);
        controller.groupsList_Pane = pane;
        controller.groupsList_ListView = groupList.listView();
        //�����ݿ��ȡȺ�鲢�����б�
        DB db = DB.getInstance();
        ArrayList<RoomInfo> roomInfos = db.queryRoomInfo(controller.userId);
        for (RoomInfo r : roomInfos) {
            if (r.mode == DB.MODE_GROUPS) {
                controller.addFriendGroup(false, r);
            }
        }
    }

    /**
     * ����Tag�ͺ����б�
     */
    private void initFriendList() {
        ObservableList<Pane> items = controller.contactList.getItems();
        ListTag listTag = new ListTag("����");
        items.add(listTag.pane());

        FriendList friendList = new FriendList();
        Pane pane = friendList.pane();
        items.add(pane);
        controller.friendsList_Pane = pane;
        controller.friendsList_ListView = friendList.listView();
        //�����ݿ��ȡ���Ѳ����ص��б�
        DB db = DB.getInstance();
        ArrayList<User> friends = db.queryFriends(controller.userId, DB.ACCEPTED);
        for (User u : friends) {
            controller.addFriendUser(false, u);
        }
    }

    /**
     * ��ʼ�������б�
     */
    private void initTalkList() {
        DB db = DB.getInstance();
        ArrayList<RoomInfo> rooms = db.queryRoomInfo(controller.userId);
        for (RoomInfo room : rooms) {
            //��ʼ����Ϣ�б�
            controller.addTalkBox(-1, room, false);
            //����Ϣ�б������������
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
            //�������ӵ��÷���˿ڵĿͻ���
            ClientManager.createClientThread(room.port, room.roomId, controller.userId);
        }
    }

    /**
     * group_bar_chat�����Ի��б� & �Ի�������
     *
     * @param id   �û���Ⱥ���ID
     * @param name �û���Ⱥ�������
     * @param node չ�����
     */
    void setContentPaneBox(int id, String name, Node node) {
        // ���Ի��б�
        Pane content_pane_box = controller.$("content_pane_box", Pane.class);
        content_pane_box.setUserData(id);
        content_pane_box.getChildren().clear();
        content_pane_box.getChildren().add(node);
        //����
        Label info_name = controller.$("content_name", Label.class);
        info_name.setText(name);
    }
}
