package ui.chat.code;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import ui.chat.code.data.RemindCount;
import ui.chat.code.data.TalkBoxData;
import ui.chat.code.element.group_bar_friend.ElementFriendGroupList;
import ui.chat.code.element.group_bar_friend.FriendList;
import ui.chat.code.element.group_bar_friend.ListTag;
import ui.chat.code.element.group_bar_friend.NewFriend;

import java.util.Date;

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
            setContentPaneBox("newFriends", "��Ӻ���", newFriendPane);
            //����б�����
            controller.clearViewListSelectedAll(controller.$("friends_ListView", ListView.class), controller.$("groupListView", ListView.class));
            //��������Ӻ����б�
            ListView<Pane> listView = newFriend.newFriendListView();
            listView.getItems().clear();
            chatEvent.doLoadNewFriend(controller.userId, listView);
        });

        // �������¼�
        TextField friendSearch = newFriend.friendSearch();

        // �����¼�����������
        friendSearch.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                String text = friendSearch.getText();
                if (null == text) text = "";
                if (text.length() > 30) text = text.substring(0, 30);
                text = text.trim();
                chatEvent.doSearchFriend(controller.userId, text);
                // �������Ԫ��
                newFriend.newFriendListView().getItems().clear();
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

        ElementFriendGroupList element = new ElementFriendGroupList();
        Pane pane = element.pane();
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
    }

    /**
     * �����б�
     */
    private void initTalkList() {
//        DB db = DB.getInstance();
        //TODO:��ʼ�������б�
        controller.addTalkBox(-1, 1, "��ɽF4", "header", "hello", new Date(), true);
        controller.addTalkBox(-1, 2, "�����మ�������", "header", "hello", new Date(), true);
        controller.addTalkBox(-1, 3, "������", "header", "hello", new Date(), true);
        controller.addTalkBox(-1, 4, "�Ը�ɳ��ҹ�����Ⱥ", "header", "hello", new Date(), true);
    }

    /**
     * group_bar_chat�����Ի��б� & �Ի�������
     *
     * @param id   �û���Ⱥ���ID
     * @param name �û���Ⱥ�������
     * @param node չ�����
     */
    void setContentPaneBox(String id, String name, Node node) {
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
     * @param talkType        �Ի�������[0���ѡ�1Ⱥ��]
     * @param talkElementPane �Ի���Ԫ�����
     * @param msgRemindLabel  ��Ϣ���ѱ�ǩ
     * @param idxFirst        �Ƿ�������λ
     * @param selected        �Ƿ�ѡ��
     * @param isRemind        �Ƿ�����
     * @Describe ���¶Ի����б�Ԫ��λ��ָ����ѡ��[��������Ϣ����ʱ����]
     */
    void updateTalkListIdxAndSelected(int talkType, Pane talkElementPane, Label msgRemindLabel, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        // �Ի���ID������ID
        TalkBoxData talkBoxData = (TalkBoxData) talkElementPane.getUserData();
        // ��䵽�Ի���
        ListView<Pane> talkList = controller.$("talkList", ListView.class);
        // �Ի���Ϊ�գ���ʼ��[�ö���ѡ�С�����]
        if (talkList.getItems().isEmpty()) {
            if (idxFirst) {
                talkList.getItems().add(0, talkElementPane);
            }
            if (selected) {
                // ���öԻ���[��ѡ��]
                talkList.getSelectionModel().select(talkElementPane);
            }
            isRemind(msgRemindLabel, talkType, isRemind);
            return;
        }
        // �Ի��ղ�Ϊ�գ��жϵ�һ��Ԫ���Ƿ�ǰ����Pane
        Pane firstPane = talkList.getItems().get(0);
        // �ж�Ԫ���Ƿ�����λ���������λ�ɷ��ز���Ҫ����������λ
        if (talkBoxData.getTalkId() == ((TalkBoxData) firstPane.getUserData()).getTalkId()) {
            Pane selectedItem = talkList.getSelectionModel().getSelectedItem();
            // ѡ���жϣ������һ��Ԫ���Ѿ�ѡ��[˵�����ڻỰ]����ô�����Ϣ����
            if (null == selectedItem) {
                isRemind(msgRemindLabel, talkType, isRemind);
                return;
            }
            TalkBoxData selectedItemUserData = (TalkBoxData) selectedItem.getUserData();
            if (null != selectedItemUserData && talkBoxData.getTalkId() == selectedItemUserData.getTalkId()) {
                clearRemind(msgRemindLabel);
            } else {
                isRemind(msgRemindLabel, talkType, isRemind);
            }
            return;
        }
        if (idxFirst) {
            talkList.getItems().remove(talkElementPane);
            talkList.getItems().add(0, talkElementPane);
        }
        if (selected) {
            // ���öԻ���[��ѡ��]
            talkList.getSelectionModel().select(talkElementPane);
        }
        isRemind(msgRemindLabel, talkType, isRemind);
    }

    /**
     * ��Ϣ����
     *
     * @param msgRemindLabel ��Ϣ���
     */
    private void isRemind(Label msgRemindLabel, int talkType, Boolean isRemind) {
        if (!isRemind) return;
        msgRemindLabel.setVisible(true);
        // Ⱥ��ֱ��չʾС���
        if (1 == talkType) {
            return;
        }
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
