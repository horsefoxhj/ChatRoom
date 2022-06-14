package ui.chat.code;

import base.client.ClientManager;
import db.DB;
import entity.RoomInfo;
import entity.User;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import ui.chat.code.data.RemindCount;
import ui.chat.code.data.TalkBoxData;
import ui.chat.code.element.group_bar_friend.FriendList;
import ui.chat.code.element.group_bar_friend.GroupList;
import ui.chat.code.element.group_bar_friend.ListTag;
import ui.chat.code.element.group_bar_friend.NewFriend;

import java.util.ArrayList;

import static utils.StringUtil.isNumeric;

/**
 * ���촰��չʾ���¼�
 */
public class ChatView {

    private ChatController controller;
    private IChatEvent chatEvent;

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
        ObservableList<Pane> items = controller.friendList.getItems();
        //��պ����б��Item
        controller.friendList.getSelectionModel().clearSelection();
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
        //���
        ObservableList<Pane> items = controller.friendList.getItems();
        ListTag listTag = new ListTag("Ⱥ��");
        items.add(listTag.pane());

        GroupList groupList = new GroupList();
        Pane pane = groupList.pane();
        items.add(pane);
    }

    /**
     * ����Tag�ͺ����б�
     */
    private void initFriendList() {
        ObservableList<Pane> items = controller.friendList.getItems();
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
            controller.addTalkBox(-1, room, false);
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


    /**
     * @param talkPane       �Ի���Ԫ�����
     * @param msgRemindLabel ��Ϣ���ѱ�ǩ
     * @param idxFirst       �Ƿ�������λ
     * @param selected       �Ƿ�ѡ��
     * @param isRemind       �Ƿ�����
     * @Describe ���¶Ի����б�Ԫ��λ��ָ����ѡ��[��������Ϣ����ʱ����]
     */
    void updateTalkListIdxAndSelected(Pane talkPane, Label msgRemindLabel, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        // �Ի���ID������ID
        TalkBoxData talkBoxData = (TalkBoxData) talkPane.getUserData();
        // ��䵽�Ի���
        ListView<Pane> talkList = controller.$("talkList", ListView.class);
        // �Ի���Ϊ�գ���ʼ��[�ö���ѡ�С�����]
        if (talkList.getItems().isEmpty()) {
            if (idxFirst) {
                talkList.getItems().add(0, talkPane);
            }
            if (selected) {
                // ���öԻ���[��ѡ��]
                talkList.getSelectionModel().select(talkPane);
            }
            isRemind(msgRemindLabel, isRemind);
            return;
        }

        // �Ի��ղ�Ϊ�գ��жϵ�һ��Ԫ���Ƿ�ǰ����Pane
        Pane firstPane = talkList.getItems().get(0);
        // �ж�Ԫ���Ƿ�����λ���������λ�ɷ��ز���Ҫ����������λ
        if (talkBoxData.getRoomId() == ((TalkBoxData) firstPane.getUserData()).getRoomId()) {
            Pane selectedItem = talkList.getSelectionModel().getSelectedItem();
            // ѡ���жϣ������һ��Ԫ���Ѿ�ѡ��[˵�����ڻỰ]����ô�����Ϣ����
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
            // ���öԻ���[��ѡ��]
            talkList.getSelectionModel().select(talkPane);
        }
        isRemind(msgRemindLabel, isRemind);
    }

    /**
     * ��Ϣ����
     *
     * @param msgRemindLabel ��Ϣ���
     */
    private void isRemind(Label msgRemindLabel, Boolean isRemind) {
        if (!isRemind) return;
        msgRemindLabel.setVisible(true);
        RemindCount remindCount = (RemindCount) msgRemindLabel.getUserData();
        // ����10��չʾʡ�Ժ�
        if (remindCount.getCount() > 99) {
            msgRemindLabel.setText("������");
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
