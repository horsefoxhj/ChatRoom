package ui.chat.code.element.group_bar_friend;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * 组件：添加好友(Icon，名称)，面板(用户面板，用户搜索，用户列表[待添加的用户])
 */
public class NewFriend {

    private Pane pane;
    // 头像
    private Label head;
    // 名称
    private Label name;
    // 用户面板
    private Pane newFriendPane;
    // 用户搜索
    private TextField newFriendSearch;
    // 用户列表[待添加好友用户]
    private ListView<Pane> newFriendListView;

    public NewFriend() {
        //新的好友Item
        pane = new Pane();
        pane.setId("newFriendTag");
        pane.setPrefSize(270, 70);
        pane.getStyleClass().add("newFriend_Tag");
        ObservableList<Node> children = pane.getChildren();

        //新的好友Item中的Icon部分
        head = new Label();
        head.setPrefSize(50, 50);
        head.setLayoutX(15);
        head.setLayoutY(10);
        head.getStyleClass().add("newFriendTag_head");
        children.add(head);

        //新的好友Item中的名称部分
        name = new Label();
        name.setPrefSize(200, 40);
        name.setLayoutX(80);
        name.setLayoutY(15);
        name.setText("新的朋友");
        name.getStyleClass().add("newFriendTag_name");
        children.add(name);

        //初始化面板区域[搜索好友框、搜索展示框]
        newFriendPane = new Pane();
        newFriendPane.setPrefSize(850, 560);
        newFriendPane.getStyleClass().add("newFriend_Pane");
        ObservableList<Node> newFriendPaneChildren = newFriendPane.getChildren();

        //用户搜索框
        newFriendSearch = new TextField();
        newFriendSearch.setPrefSize(600, 50);
        newFriendSearch.setLayoutX(125);
        newFriendSearch.setLayoutY(25);
        newFriendSearch.setPromptText("请输入用户ID");
        newFriendSearch.setPadding(new Insets(10));
        newFriendSearch.getStyleClass().add("newFriend_Search");
        newFriendPaneChildren.add(newFriendSearch);

        //用户列表框[初始化，未装载]
        newFriendListView = new ListView<>();
        newFriendListView.setId("newFriend_ListView");
        newFriendListView.setPrefSize(850, 460);
        newFriendListView.setLayoutY(75);
        newFriendListView.getStyleClass().add("newFriend_ListView");
        newFriendPaneChildren.add(newFriendListView);
    }

    public Pane pane() {
        return pane;
    }

    public Pane newFriendPane() {
        return newFriendPane;
    }

    public TextField friendSearch() {
        return newFriendSearch;
    }

    public ListView<Pane> newFriendListView() {
        return newFriendListView;
    }
}

