package ui.chat.code;

import base.client.ClientManager;
import db.DB;
import entity.Message;
import entity.RoomInfo;
import entity.User;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import ui.chat.code.data.TalkBoxData;
import ui.chat.code.element.group_bar_friend.NewFriendItem;

import java.util.ArrayList;

import static db.DB.ALL;
import static utils.JsonUtils.Msg2Str;

/**
 * �¼�����
 */
public class ChatEventDefine implements IChatEvent {

    private final Chat chat;
    private final IChatMethod chatMethod;

    public ChatEventDefine(Chat chat, IChatMethod chatMethod) {
        this.chat = chat;
        this.chatMethod = chatMethod;

        //���ô����ƶ�
        chat.move();
        //��С��
        min();
        //�˳�
        quit();
        //����
        barChat();
        //����
        barFriend();
        //������Ϣ�¼�
        doEventTextSend();
//        doEventToolFace();   // ���鴰��
    }

    // ��С��
    private void min() {
        chat.group_bar_chat_min.setOnAction(event -> chat.setIconified(true));
        chat.group_bar_friend_min.setOnAction(event -> chat.setIconified(true));
    }

    // �˳�
    private void quit() {
        chat.group_bar_chat_close.setOnAction(event -> {
            chat.close();
            doQuit();
        });
        chat.group_bar_friend_close.setOnAction(event -> {
            chat.close();
            doQuit();
        });
    }

    //��ת���������
    private void barChat() {
        chat.bar_chat.setOnAction(event -> {
            switchBarChat(chat.bar_chat, chat.group_bar_chat, true);
            switchBarFriend(chat.bar_friend, chat.group_bar_friend, false);
        });

        chat.bar_chat.setOnMouseEntered(event -> {
            boolean visible = chat.group_bar_chat.isVisible();
            if (visible) return;
            chat.bar_chat.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/chat_1.png')"));
        });

        chat.bar_chat.setOnMouseExited(event -> {
            boolean visible = chat.group_bar_chat.isVisible();
            if (visible) return;
            chat.bar_chat.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/chat_0.png')"));
        });
    }

    //��ת�����ѽ���
    private void barFriend() {
        chat.bar_friend.setOnAction(event -> {
            switchBarChat(chat.bar_chat, chat.group_bar_chat, false);
            switchBarFriend(chat.bar_friend, chat.group_bar_friend, true);
            refreshFriendList();
        });
        chat.bar_friend.setOnMouseEntered(event -> {
            boolean visible = chat.group_bar_friend.isVisible();
            if (visible) return;
            chat.bar_friend.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/friend_1.png');"));

        });
        chat.bar_friend.setOnMouseExited(event -> {
            boolean visible = chat.group_bar_friend.isVisible();
            if (visible) return;
            chat.bar_friend.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/friend_0.png');"));
        });
    }

    //doSwitchBarChat
    private void switchBarChat(Button bar_chat, Pane group_bar_chat, boolean toggle) {
        if (toggle) {
            bar_chat.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/chat_2.png');"));
            group_bar_chat.setVisible(true);
        } else {
            bar_chat.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/chat_0.png');"));
            group_bar_chat.setVisible(false);
        }
    }

    //doSwitchBarFriend
    private void switchBarFriend(Button bar_friend, Pane group_bar_friend, boolean toggle) {
        if (toggle) {
            bar_friend.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/friend_2.png');"));
            group_bar_friend.setVisible(true);
        } else {
            bar_friend.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/friend_0.png');"));
            group_bar_friend.setVisible(false);
        }
    }

    //ת������������� [���������Ϣʱ�򴥷� -> ��ӵ��Ի���ѡ�С�չʾ�Ի��б�]
    public void switchFriendTalkPane(Button sendMsgButton, int friendId) {
        sendMsgButton.setOnAction(event -> {

            DB db = DB.getInstance();
            //��ѯ������
            RoomInfo roomInfo = db.queryRoomWithFriendId(chat.userId, friendId);
            //��Ӻ��ѵ��Ի���
            chatMethod.addTalkBox(0, roomInfo, true);
            //�л����Ի��򴰿�
            switchBarChat(chat.$("bar_chat", Button.class), chat.$("group_bar_chat", Pane.class), true);
            switchBarFriend(chat.$("bar_friend", Button.class), chat.$("group_bar_friend", Pane.class), false);
            //�¼�������䵽�Ի���
            doAddTalkUser(chat.userId, friendId);
        });
    }

//    // Ⱥ�飻������Ⱥ�鷢����Ϣ
//    public void doEventOpenFriendGroupSendMsg(Button sendMsgButton, String groupId, String groupName, String groupHead) {
//        sendMsgButton.setOnAction(event -> {
//            // 1. ��Ӻ��ѵ��Ի���
//            chatMethod.addTalkBox(0, 1, groupId, groupName, groupHead, null, null, true);
//            // 2. �л����Ի��򴰿�
//            switchBarChat(chat.$("bar_chat", Button.class), chat.$("group_bar_chat", Pane.class), true);
//            switchBarFriend(chat.$("bar_friend", Button.class), chat.$("group_bar_friend", Pane.class), false);
//            // 3. �¼�������䵽�Ի���
//            chatEvent.doEventAddTalkGroup(chat.userId, groupId);
//        });
//    }

    //���÷�����Ϣ�¼�
    private void doEventTextSend() {
        //������Ϣ(�س�)
        chat.txt_input.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                doEventSendMsg();
            }
        });
        //������Ϣ(��ť)
        chat.touch_send.setOnMousePressed(event -> doEventSendMsg());
    }

    //������Ϣ
    private void doEventSendMsg() {
        MultipleSelectionModel selectionModel = chat.talkList.getSelectionModel();
        Pane selectedItem = (Pane) selectionModel.getSelectedItem();
        // ���öԻ���Ϣ
        TalkBoxData talkBoxData = (TalkBoxData) selectedItem.getUserData();
        String msg = chat.txt_input.getText();
        if (null == msg || "".equals(msg) || "".equals(msg.trim())) {
            return;
        }
        //ɾȥ���з�
        msg = msg.replace("\n", "");

        Message message = new Message(talkBoxData.getRoomId(), chat.userId, msg, System.currentTimeMillis());
        // �����¼����Լ������Ϣ
        chatMethod.addTalkMsgRight(message, true, true, false);
        //��������
        chat.txt_input.clear();
        // ������Ϣ
        doSendMsg(message);
    }

    @Override
    public void doQuit() {
        System.exit(0);
    }

    @Override
    public void doSendMsg(Message message) {
        //��ȡ��Ӧroom��client�̲߳�������Ϣ
        ClientManager.getClientThread(message.roomId).sendMsg(Msg2Str(message));
    }

    @Override
    public void doAddTalkUser(int userId, int friendId) {

    }

    @Override
    public void doAddTalkGroup(int userId, String groupId) {
        System.out.println("��䵽���촰��[Ⱥ��] groupId��" + groupId);
    }

    @Override
    public void doDelTalkUser(int userId, int roomId) {
        //ɾ���Ի���ɾ����Ӧclient�߳�
        ClientManager.removeClientThread(roomId);
    }

    @Override
    public void doLoadNewFriend(User user, ListView<Pane> listView, Integer status) {
        if (user != null && listView != null) {
            NewFriendItem item = new NewFriendItem(user, status);
            //���õ���¼�
            item.statusLabel().setOnMousePressed(mouseEvent -> {
                //����ui
                item.statusLabel().setText("�����");
                item.statusLabel().getStyleClass().clear();
                item.statusLabel().setUserData(2);
                item.statusLabel().getStyleClass().add("newFriendItem_statusLabel_2");
                doAddFriend(chat.userId, (Integer) item.pane().getUserData());
            });
            listView.getItems().add(item.pane());
        }
    }

    @Override
    public void doSearchFriend(int friendId) {
        ListView<Pane> listView = chat.$("newFriend_ListView", ListView.class);
        //�����ݿ��������û�
        DB db = DB.getInstance();
        User user = db.queryUserById(friendId);
        // �������Ԫ��
        listView.getItems().clear();
        //��ѯ�����Ϊnull,�ҽ����Ϊ����,�Ҳ��Ǳ��˵�����
        if (user != null && user.getUid() != chat.userId) {
            ArrayList<User> users = db.queryFriends(chat.userId, ALL);
            //�����������û����ص��б�
            for (User s : users) {
                //�û��Ǻ���
                if (s.getUid() == user.getUid()) {
                    doLoadNewFriend(user, listView, DB.ACCEPTED);
                    return;
                }
            }
            //�û����Ǻ���
            doLoadNewFriend(user, listView, DB.PENDING);
        }
    }

    @Override
    public void doAddFriend(int userId, int friendId) {
        //�����ݿ���Ӻ���
        DB db = DB.getInstance();
        db.insertFriendship(userId, friendId);
    }

    @Override
    public void doCreateGroup(int uid) {
        //TODO:����Ⱥ��
    }

    /**
     * ˢ�º����б�
     */
    private void refreshFriendList(){
        ObservableList<Pane> items = chat.friendsList_ListView.getItems();
        items.clear();
        //�����ݿ��ȡ���Ѳ����ص��б�
        DB db = DB.getInstance();
        ArrayList<User> friends = db.queryFriends(chat.userId, DB.ACCEPTED);
        for (User u : friends) {
            chatMethod.addFriendUser(false, u);
        }
    }

    // ����
//    private void doEventToolFace() {
//        FaceController face = new FaceController(chat, chat, chatEvent, chatMethod);
//        Button tool_face = chat.$("tool_face", Button.class);
//        tool_face.setOnMousePressed(event -> {
//            face.doShowFace(chatMethod.getToolFaceX(), chatMethod.getToolFaceY());
//        });
//    }

}
