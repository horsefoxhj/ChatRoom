package ui.chat.code;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import ui.UIParent;

import java.io.IOException;

public abstract class Chat extends UIParent {

    // 用户ID
    public String userId;
    // 用户昵称
    public String userNickName;
    // 用户头像
    public String userHead;
    // 输入框
    public TextArea txt_input;
    //最小化
    public Button group_bar_chat_min;
    public Button group_bar_friend_min;
    //关闭
    public Button group_bar_chat_close;
    public Button group_bar_friend_close;
    //聊天界面按钮&聊天界面
    public Button bar_chat;
    public Pane group_bar_chat;
    //好友界面按钮&好友界面&好友列表
    public Button bar_friend;
    public Pane group_bar_friend;
    public ListView<Pane> friendsList_ListView;
    public Pane friendsList_Pane;
    public ListView<Pane> friendList;
    //发送
    public Label touch_send;
    //聊天列表
    public ListView talkList;

    Chat() throws IOException {
        root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/chat/chat.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        setResizable(false);
        //绑定UI
        bindUI();
        //初始化事件
        initEventDefine();
        //初始化界面
        initView();
    }

    //绑定UI
    private void bindUI() {
        txt_input = $("txt_input", TextArea.class);
        group_bar_chat_min = $("group_bar_chat_min", Button.class);
        group_bar_friend_min = $("group_bar_friend_min", Button.class);
        group_bar_chat_close = $("group_bar_chat_close", Button.class);
        group_bar_friend_close = $("group_bar_friend_close", Button.class);
        bar_chat = $("bar_chat", Button.class);
        group_bar_chat = $("group_bar_chat", Pane.class);
        bar_friend = $("bar_friend", Button.class);
        group_bar_friend = $("group_bar_friend", Pane.class);
        touch_send = $("touch_send", Label.class);
        talkList = $("talkList", ListView.class);
        friendList = $("friendList", ListView.class);
    }

    public abstract void initView();

    //清除列表元素
    public void clearViewListSelectedAll(ListView<Pane>... listViews) {

        for (ListView<Pane> listView : listViews) {
            listView.getSelectionModel().clearSelection();
        }
    }
}
