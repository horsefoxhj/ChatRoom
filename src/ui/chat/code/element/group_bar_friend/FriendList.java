package ui.chat.code.element.group_bar_friend;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

/**
 * 组件；好友列表框体
 */
public class FriendList {

    private Pane pane;
    private ListView<Pane> userList; // 好友列表

    public FriendList(){
        pane = new Pane();
        pane.setId("friendsList_Pane");
        pane.setPrefWidth(314);
        pane.setPrefHeight(0);// 自动计算；userListView.setPrefHeight(70 * items.size() + 10);
        pane.setLayoutX(-10);
        pane.getStyleClass().add("friendsList_Pane");
        ObservableList<Node> children = pane.getChildren();

        userList = new ListView<>();
        userList.setId("friends_ListView");
        userList.setPrefWidth(314);
        userList.setPrefHeight(0); // 自动计算；userListView.setPrefHeight(70 * items.size() + 10);
        userList.setLayoutX(-10);
        userList.getStyleClass().add("friends_listView");
        children.add(userList);
    }

    public Pane pane() {
        return pane;
    }
    public ListView<Pane> listView(){return userList;}
}
