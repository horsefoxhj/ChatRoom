package ui.chat.code;

import base.client.ClientManager;
import db.DB;
import entity.Message;
import entity.User;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import ui.chat.code.data.TalkBoxData;
import ui.chat.code.element.group_bar_friend.NewFriendItem;

import java.util.ArrayList;

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

    // ���ѣ���������ѷ�����Ϣ [���������Ϣʱ�򴥷� -> ��ӵ��Ի���ѡ�С�չʾ�Ի��б�]
    public void doEventOpenFriendUserSendMsg(Button sendMsgButton, int friendId, String friendName, String friendHeader) {
        sendMsgButton.setOnAction(event -> {
            // 1. ��Ӻ��ѵ��Ի���
//            chatMethod.addTalkBox(0, 0, friendId, friendName, friendHeader, null, null, true);
            // 2. �л����Ի��򴰿�
            switchBarChat(chat.$("bar_chat", Button.class), chat.$("group_bar_chat", Pane.class), true);
            switchBarFriend(chat.$("bar_friend", Button.class), chat.$("group_bar_friend", Pane.class), false);
            // 3. �¼�������䵽�Ի���
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
//        chatMethod.addTalkBox(-1,);
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
    public void doLoadNewFriend(int userId, ListView<Pane> listView) {
        //�����ݿ��л�ȡ������ӵĺ��ѣ������ص��б���
        DB db = DB.getInstance();
        //��ȡ����ӵĺ���
        ArrayList<User> friends_P = db.queryFriends(userId, DB.PENDING);
        ArrayList<User> friends_A = db.queryFriends(userId, DB.ACCEPTED);

        //���δ���ܵĺ���
        for (User u : friends_P) {
            listView.getItems().add(new NewFriendItem(u.getUid(), u.getName(), u.getHeader(), DB.PENDING).pane());
        }
        //����ѽ��ܵĺ���
        for (User u : friends_A) {
            listView.getItems().add(new NewFriendItem(u.getUid(), u.getName(), u.getHeader(), DB.ACCEPTED).pane());
        }
    }

    @Override
    public void doSearchFriend(int userId, String text) {
        //TODO:�����ݿ��������û�
        System.out.println("�������ѣ�" + text);
    }

    @Override
    public void doAddUser(int userId, int friendId) {
        System.out.println("��Ӻ��ѣ�" + friendId);
    }

    @Override
    public void doCreateGroup(int uid) {
        //TODO:����Ⱥ��
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
