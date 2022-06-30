package ui.chat.code.element.group_bar_friend;

import db.DB;
import entity.User;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * �����������еĺ�������Item
 */
public class NewFriendItem {

    private Pane pane;    // �û��װ�
    private Label idLabel;    // չʾ�û�ID
    private Label headLabel;  // ͷ������
    private Label nameLabel;  // ��������
    private Label statusLabel;// ״̬��0���/1����
    private Label line;       // ����

    /**
     * ���캯��
     * @param user �û���Ϣ
     * @param status       ״̬��0/1
     */
    public NewFriendItem(User user, Integer status) {
        pane = new Pane();
        pane.setUserData(user.getUid());
        pane.setPrefWidth(250);
        pane.setPrefHeight(70);
//        pane.getStyleClass().add("newFriendItem");
        ObservableList<Node> children = pane.getChildren();

        // ͷ������
        headLabel = new Label();
        headLabel.setPrefSize(50, 50);
        headLabel.setLayoutX(125);
        headLabel.setLayoutY(10);
        headLabel.getStyleClass().add("newFriendItem_head");
        headLabel.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/%s.png')", user.getHeader()));
        children.add(headLabel);

        // ��������
        nameLabel = new Label();
        nameLabel.setPrefSize(200, 30);
        nameLabel.setLayoutX(190);
        nameLabel.setLayoutY(10);
        nameLabel.setText(user.getName());
        nameLabel.getStyleClass().add("newFriendItem_name");
        children.add(nameLabel);

        // ID����
        idLabel = new Label();
        idLabel.setPrefSize(200, 20);
        idLabel.setLayoutX(190);
        idLabel.setLayoutY(40);
        idLabel.setText(String.valueOf(user.getUid()));
        idLabel.getStyleClass().add("newFriendItem_id");
        children.add(idLabel);

        // ����
        line = new Label();
        line.setPrefSize(582, 1);
        line.setLayoutX(125);
        line.setLayoutY(50);
        line.getStyleClass().add("newFriendItem_line");
        children.add(line);

        // ״̬����
        statusLabel = new Label();
        statusLabel.setPrefSize(56, 30);
        statusLabel.setLayoutX(650);
        statusLabel.setLayoutY(20);
        if (DB.PENDING == status) {
            statusLabel.setText("���");
        } else if (DB.ACCEPTED == status) {
            statusLabel.setText("�����");
        }
        statusLabel.setUserData(status);
        statusLabel.getStyleClass().add("newFriendItem_statusLabel_" + status);
        children.add(statusLabel);
    }

    public Pane pane() {
        return pane;
    }

    // ��Ӱ�ť
    public Label statusLabel() {
        return statusLabel;
    }
}
