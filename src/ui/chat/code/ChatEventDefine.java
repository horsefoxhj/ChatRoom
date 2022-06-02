package ui.chat.code;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import ui.chat.code.element.group_bar_friend.NewFriendItem;

import java.util.Date;

/**
 * �¼�����
 */
public class ChatEventDefine implements IChatEvent {

    private final Chat chat;
    private final IChatMethod chatMethod;
    private boolean isChatting = true;

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
        //������Ϣ�¼�[����]
        doEventTextSend();
        //������Ϣ�¼�[��ť]
        doEventTouchSend();
//        doEventToolFace();   // ���鴰��
        System.out.println("ChatEventDefine~");
    }

    // ��С��
    private void min() {
        chat.group_bar_chat_min.setOnAction(event -> chat.setIconified(true));
        chat.group_bar_friend_min.setOnAction(event -> chat.setIconified(true));
    }

    // �˳�
    private void quit() {
        chat.group_bar_chat_close.setOnAction(event -> {
            doQuit();
            chat.close();
            System.exit(0);
        });
        chat.group_bar_friend_close.setOnAction(event -> {
            doQuit();
            chat.close();
            System.exit(0);
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

//    // ���ѣ���������ѷ�����Ϣ [���������Ϣʱ�򴥷� -> ��ӵ��Ի���ѡ�С�չʾ�Ի��б�]
//    public void doEventOpenFriendUserSendMsg(Button sendMsgButton, String userFriendId, String userFriendNickName, String userFriendHead) {
//        sendMsgButton.setOnAction(event -> {
//            // 1. ��Ӻ��ѵ��Ի���
//            chatMethod.addTalkBox(0, 0, userFriendId, userFriendNickName, userFriendHead, null, null, true);
//            // 2. �л����Ի��򴰿�
//            switchBarChat(chat.$("bar_chat", Button.class), chat.$("group_bar_chat", Pane.class), true);
//            switchBarFriend(chat.$("bar_friend", Button.class), chat.$("group_bar_friend", Pane.class), false);
//            // 3. �¼�������䵽�Ի���
//            chatEvent.doEventAddTalkUser(chat.userId, userFriendId);
//        });
//    }
//
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

    // ������Ϣ�¼�(�س�)
    private void doEventTextSend() {
        chat.txt_input.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                doEventSendMsg();
            }
        });
    }

    // ������Ϣ(��ť)
    private void doEventTouchSend() {
        chat.touch_send.setOnMousePressed(event -> {
            doEventSendMsg();
        });
    }

    //doSendMsg
    private void doEventSendMsg() {

//        MultipleSelectionModel selectionModel = chat.talkList.getSelectionModel();
//        Pane selectedItem = (Pane) selectionModel.getSelectedItem();
        // ���öԻ���Ϣ
//        TalkBoxData talkBoxData = (TalkBoxData) selectedItem.getUserData();
        String msg = chat.txt_input.getText();
        if (null == msg || "".equals(msg) || "".equals(msg.trim())) {
            return;
        }
        Date msgDate = new Date();
        // ������Ϣ
//        doSendMsg(chat.userId, talkBoxData.getTalkId(), talkBoxData.getTalkType(), msg, 0, msgDate);
        doSendMsg(chat.userId, String.valueOf(0), 0, msg, 0, msgDate);
        // �����¼����Լ������Ϣ
//        chatMethod.addTalkMsgRight(talkBoxData.getTalkId(), msg, 0, msgDate, true, true, false);
        chat.txt_input.clear();
    }

    @Override
    public void doQuit() {
        System.out.println("�˳�������");
    }

    @Override
    public void doSendMsg(String userId, String talkId, Integer talkType, String msg, Integer msgType, Date msgDate) {
//        chatMethod.addTalkMsgRight(userId, msg, msgType, msgDate, true, false, false);
        System.out.println("������Ϣ");
        System.out.println("userId��" + userId);
        System.out.println("talkType[0����/1Ⱥ��]��" + talkType);
        System.out.println("talkId��" + talkId);
        System.out.println("msg��" + msg);
        System.out.println("msgType[0������Ϣ/1�̶�����]��" + msgType);
    }

    @Override
    public void doAddTalkUser(String userId, String userFriendId) {
        System.out.println("��䵽���촰��[����] userFriendId��" + userFriendId);
    }

    @Override
    public void doAddTalkGroup(String userId, String groupId) {
        System.out.println("��䵽���촰��[Ⱥ��] groupId��" + groupId);
    }

    @Override
    public void doDelTalkUser(String userId, int roomId) {
        System.out.println("ɾ���Ի���" + roomId);
    }

    @Override
    public void doLoadNewFriend(String userId, ListView<Pane> listView) {
        //TODO:�����ݿ��л�ȡ������ӵĺ��ѣ������ص��б���
        System.out.println("�µ�����");
        // �������
        listView.getItems().add(new NewFriendItem("114514", "��˹��", "header", 1).pane());
        listView.getItems().add(new NewFriendItem("233333", "���ǳ�", "header", 2).pane());
    }

    @Override
    public void doSearchFriend(String userId, String text) {
        //TODO:�����ݿ��������û�
        System.out.println("�������ѣ�" + text);
    }

    @Override
    public void doAddUser(String userId, String friendId) {
        System.out.println("��Ӻ��ѣ�" + friendId);
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
