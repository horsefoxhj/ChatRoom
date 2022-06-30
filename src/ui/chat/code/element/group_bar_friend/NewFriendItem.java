package ui.chat.code.element.group_bar_friend;

import db.DB;
import entity.User;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * 好友添加面板中的好友申请Item
 */
public class NewFriendItem {

    private Pane pane;    // 用户底板
    private Label idLabel;    // 展示用户ID
    private Label headLabel;  // 头像区域
    private Label nameLabel;  // 名称区域
    private Label statusLabel;// 状态；0添加/1接受
    private Label line;       // 底线

    /**
     * 构造函数
     * @param user 用户信息
     * @param status       状态；0/1
     */
    public NewFriendItem(User user, Integer status) {
        pane = new Pane();
        pane.setUserData(user.getUid());
        pane.setPrefWidth(250);
        pane.setPrefHeight(70);
//        pane.getStyleClass().add("newFriendItem");
        ObservableList<Node> children = pane.getChildren();

        // 头像区域
        headLabel = new Label();
        headLabel.setPrefSize(50, 50);
        headLabel.setLayoutX(125);
        headLabel.setLayoutY(10);
        headLabel.getStyleClass().add("newFriendItem_head");
        headLabel.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/%s.png')", user.getHeader()));
        children.add(headLabel);

        // 名称区域
        nameLabel = new Label();
        nameLabel.setPrefSize(200, 30);
        nameLabel.setLayoutX(190);
        nameLabel.setLayoutY(10);
        nameLabel.setText(user.getName());
        nameLabel.getStyleClass().add("newFriendItem_name");
        children.add(nameLabel);

        // ID区域
        idLabel = new Label();
        idLabel.setPrefSize(200, 20);
        idLabel.setLayoutX(190);
        idLabel.setLayoutY(40);
        idLabel.setText(String.valueOf(user.getUid()));
        idLabel.getStyleClass().add("newFriendItem_id");
        children.add(idLabel);

        // 底线
        line = new Label();
        line.setPrefSize(582, 1);
        line.setLayoutX(125);
        line.setLayoutY(50);
        line.getStyleClass().add("newFriendItem_line");
        children.add(line);

        // 状态区域
        statusLabel = new Label();
        statusLabel.setPrefSize(56, 30);
        statusLabel.setLayoutX(650);
        statusLabel.setLayoutY(20);
        if (DB.PENDING == status) {
            statusLabel.setText("添加");
        } else if (DB.ACCEPTED == status) {
            statusLabel.setText("已添加");
        }
        statusLabel.setUserData(status);
        statusLabel.getStyleClass().add("newFriendItem_statusLabel_" + status);
        children.add(statusLabel);
    }

    public Pane pane() {
        return pane;
    }

    // 添加按钮
    public Label statusLabel() {
        return statusLabel;
    }
}
