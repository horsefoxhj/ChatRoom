package ui.chat.code;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import ui.chat.code.element.group_bar_friend.NewFriendItem;

import java.util.Date;

/**
 * 事件定义
 */
public class ChatEventDefine implements IChatEvent {

    private final Chat chat;
    private final IChatMethod chatMethod;
    private boolean isChatting = true;

    public ChatEventDefine(Chat chat, IChatMethod chatMethod) {
        this.chat = chat;
        this.chatMethod = chatMethod;

        //设置窗口移动
        chat.move();
        //最小化
        min();
        //退出
        quit();
        //聊天
        barChat();
        //好友
        barFriend();
        //发送消息事件[键盘]
        doEventTextSend();
        //发送消息事件[按钮]
        doEventTouchSend();
//        doEventToolFace();   // 表情窗体
        System.out.println("ChatEventDefine~");
    }

    // 最小化
    private void min() {
        chat.group_bar_chat_min.setOnAction(event -> chat.setIconified(true));
        chat.group_bar_friend_min.setOnAction(event -> chat.setIconified(true));
    }

    // 退出
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

    //跳转到聊天界面
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

    //跳转到好友界面
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

//    // 好友；开启与好友发送消息 [点击发送消息时候触发 -> 添加到对话框、选中、展示对话列表]
//    public void doEventOpenFriendUserSendMsg(Button sendMsgButton, String userFriendId, String userFriendNickName, String userFriendHead) {
//        sendMsgButton.setOnAction(event -> {
//            // 1. 添加好友到对话框
//            chatMethod.addTalkBox(0, 0, userFriendId, userFriendNickName, userFriendHead, null, null, true);
//            // 2. 切换到对话框窗口
//            switchBarChat(chat.$("bar_chat", Button.class), chat.$("group_bar_chat", Pane.class), true);
//            switchBarFriend(chat.$("bar_friend", Button.class), chat.$("group_bar_friend", Pane.class), false);
//            // 3. 事件处理；填充到对话框
//            chatEvent.doEventAddTalkUser(chat.userId, userFriendId);
//        });
//    }
//
//    // 群组；开启与群组发送消息
//    public void doEventOpenFriendGroupSendMsg(Button sendMsgButton, String groupId, String groupName, String groupHead) {
//        sendMsgButton.setOnAction(event -> {
//            // 1. 添加好友到对话框
//            chatMethod.addTalkBox(0, 1, groupId, groupName, groupHead, null, null, true);
//            // 2. 切换到对话框窗口
//            switchBarChat(chat.$("bar_chat", Button.class), chat.$("group_bar_chat", Pane.class), true);
//            switchBarFriend(chat.$("bar_friend", Button.class), chat.$("group_bar_friend", Pane.class), false);
//            // 3. 事件处理；填充到对话框
//            chatEvent.doEventAddTalkGroup(chat.userId, groupId);
//        });
//    }

    // 发送消息事件(回车)
    private void doEventTextSend() {
        chat.txt_input.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                doEventSendMsg();
            }
        });
    }

    // 发送消息(按钮)
    private void doEventTouchSend() {
        chat.touch_send.setOnMousePressed(event -> {
            doEventSendMsg();
        });
    }

    //doSendMsg
    private void doEventSendMsg() {

//        MultipleSelectionModel selectionModel = chat.talkList.getSelectionModel();
//        Pane selectedItem = (Pane) selectionModel.getSelectedItem();
        // 设置对话信息
//        TalkBoxData talkBoxData = (TalkBoxData) selectedItem.getUserData();
        String msg = chat.txt_input.getText();
        if (null == msg || "".equals(msg) || "".equals(msg.trim())) {
            return;
        }
        Date msgDate = new Date();
        // 发送消息
//        doSendMsg(chat.userId, talkBoxData.getTalkId(), talkBoxData.getTalkType(), msg, 0, msgDate);
        doSendMsg(chat.userId, String.valueOf(0), 0, msg, 0, msgDate);
        // 发送事件给自己添加消息
//        chatMethod.addTalkMsgRight(talkBoxData.getTalkId(), msg, 0, msgDate, true, true, false);
        chat.txt_input.clear();
    }

    @Override
    public void doQuit() {
        System.out.println("退出操作！");
    }

    @Override
    public void doSendMsg(String userId, String talkId, Integer talkType, String msg, Integer msgType, Date msgDate) {
//        chatMethod.addTalkMsgRight(userId, msg, msgType, msgDate, true, false, false);
        System.out.println("发送消息");
        System.out.println("userId：" + userId);
        System.out.println("talkType[0好友/1群组]：" + talkType);
        System.out.println("talkId：" + talkId);
        System.out.println("msg：" + msg);
        System.out.println("msgType[0文字消息/1固定表情]：" + msgType);
    }

    @Override
    public void doAddTalkUser(String userId, String userFriendId) {
        System.out.println("填充到聊天窗口[好友] userFriendId：" + userFriendId);
    }

    @Override
    public void doAddTalkGroup(String userId, String groupId) {
        System.out.println("填充到聊天窗口[群组] groupId：" + groupId);
    }

    @Override
    public void doDelTalkUser(String userId, int roomId) {
        System.out.println("删除对话框：" + roomId);
    }

    @Override
    public void doLoadNewFriend(String userId, ListView<Pane> listView) {
        //TODO:从数据库中获取申请添加的好友，并加载到列表中
        System.out.println("新的朋友");
        // 添加朋友
        listView.getItems().add(new NewFriendItem("114514", "阿斯达", "header", 1).pane());
        listView.getItems().add(new NewFriendItem("233333", "周星驰", "header", 2).pane());
    }

    @Override
    public void doSearchFriend(String userId, String text) {
        //TODO:从数据库中搜索用户
        System.out.println("搜索好友：" + text);
    }

    @Override
    public void doAddUser(String userId, String friendId) {
        System.out.println("添加好友：" + friendId);
    }

    // 表情
//    private void doEventToolFace() {
//        FaceController face = new FaceController(chat, chat, chatEvent, chatMethod);
//        Button tool_face = chat.$("tool_face", Button.class);
//        tool_face.setOnMousePressed(event -> {
//            face.doShowFace(chatMethod.getToolFaceX(), chatMethod.getToolFaceY());
//        });
//    }

}
