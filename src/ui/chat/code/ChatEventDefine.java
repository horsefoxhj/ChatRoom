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
 * 事件定义
 */
public class ChatEventDefine implements IChatEvent {

    private final Chat chat;
    private final IChatMethod chatMethod;

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
        //发送消息事件
        doEventTextSend();
//        doEventToolFace();   // 表情窗体
    }

    // 最小化
    private void min() {
        chat.group_bar_chat_min.setOnAction(event -> chat.setIconified(true));
        chat.group_bar_friend_min.setOnAction(event -> chat.setIconified(true));
    }

    // 退出
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

    // 好友；开启与好友发送消息 [点击发送消息时候触发 -> 添加到对话框、选中、展示对话列表]
    public void doEventOpenFriendUserSendMsg(Button sendMsgButton, int friendId, String friendName, String friendHeader) {
        sendMsgButton.setOnAction(event -> {
            // 1. 添加好友到对话框
//            chatMethod.addTalkBox(0, 0, friendId, friendName, friendHeader, null, null, true);
            // 2. 切换到对话框窗口
            switchBarChat(chat.$("bar_chat", Button.class), chat.$("group_bar_chat", Pane.class), true);
            switchBarFriend(chat.$("bar_friend", Button.class), chat.$("group_bar_friend", Pane.class), false);
            // 3. 事件处理；填充到对话框
            doAddTalkUser(chat.userId, friendId);
        });
    }

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

    //设置发送消息事件
    private void doEventTextSend() {
        //发送消息(回车)
        chat.txt_input.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                doEventSendMsg();
            }
        });
        //发送消息(按钮)
        chat.touch_send.setOnMousePressed(event -> doEventSendMsg());
    }

    //发送消息
    private void doEventSendMsg() {
        MultipleSelectionModel selectionModel = chat.talkList.getSelectionModel();
        Pane selectedItem = (Pane) selectionModel.getSelectedItem();
        // 设置对话信息
        TalkBoxData talkBoxData = (TalkBoxData) selectedItem.getUserData();
        String msg = chat.txt_input.getText();
        if (null == msg || "".equals(msg) || "".equals(msg.trim())) {
            return;
        }
        //删去换行符
        msg = msg.replace("\n", "");

        Message message = new Message(talkBoxData.getRoomId(), chat.userId, msg, System.currentTimeMillis());
        // 发送事件给自己添加消息
        chatMethod.addTalkMsgRight(message, true, true, false);
        //清空输入框
        chat.txt_input.clear();
        // 发送消息
        doSendMsg(message);
    }

    @Override
    public void doQuit() {
        System.exit(0);
    }

    @Override
    public void doSendMsg(Message message) {
        //获取对应room的client线程并发送消息
        ClientManager.getClientThread(message.roomId).sendMsg(Msg2Str(message));
    }

    @Override
    public void doAddTalkUser(int userId, int friendId) {
//        chatMethod.addTalkBox(-1,);
    }

    @Override
    public void doAddTalkGroup(int userId, String groupId) {
        System.out.println("填充到聊天窗口[群组] groupId：" + groupId);
    }

    @Override
    public void doDelTalkUser(int userId, int roomId) {
        //删除对话框并删除对应client线程
        ClientManager.removeClientThread(roomId);
    }

    @Override
    public void doLoadNewFriend(int userId, ListView<Pane> listView) {
        //从数据库中获取申请添加的好友，并加载到列表中
        DB db = DB.getInstance();
        //获取待添加的好友
        ArrayList<User> friends_P = db.queryFriends(userId, DB.PENDING);
        ArrayList<User> friends_A = db.queryFriends(userId, DB.ACCEPTED);

        //添加未接受的好友
        for (User u : friends_P) {
            listView.getItems().add(new NewFriendItem(u.getUid(), u.getName(), u.getHeader(), DB.PENDING).pane());
        }
        //添加已接受的好友
        for (User u : friends_A) {
            listView.getItems().add(new NewFriendItem(u.getUid(), u.getName(), u.getHeader(), DB.ACCEPTED).pane());
        }
    }

    @Override
    public void doSearchFriend(int userId, String text) {
        //TODO:从数据库中搜索用户
        System.out.println("搜索好友：" + text);
    }

    @Override
    public void doAddUser(int userId, int friendId) {
        System.out.println("添加好友：" + friendId);
    }

    @Override
    public void doCreateGroup(int uid) {
        //TODO:创建群聊
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
