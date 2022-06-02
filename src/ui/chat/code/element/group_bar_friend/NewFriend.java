package ui.chat.code.element.group_bar_friend;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * �������Ӻ���(Icon������)�����(�û���壬�û��������û��б�[����ӵ��û�])
 */
public class NewFriend {

    private Pane pane;
    // ͷ��
    private Label head;
    // ����
    private Label name;
    // �û����
    private Pane newFriendPane;
    // �û�����
    private TextField newFriendSearch;
    // �û��б�[����Ӻ����û�]
    private ListView<Pane> newFriendListView;

    public NewFriend() {
        //�µĺ���Item
        pane = new Pane();
        pane.setId("newFriendTag");
        pane.setPrefSize(270, 70);
        pane.getStyleClass().add("newFriend_Tag");
        ObservableList<Node> children = pane.getChildren();

        //�µĺ���Item�е�Icon����
        head = new Label();
        head.setPrefSize(50, 50);
        head.setLayoutX(15);
        head.setLayoutY(10);
        head.getStyleClass().add("newFriendTag_head");
        children.add(head);

        //�µĺ���Item�е����Ʋ���
        name = new Label();
        name.setPrefSize(200, 40);
        name.setLayoutX(80);
        name.setLayoutY(15);
        name.setText("�µ�����");
        name.getStyleClass().add("newFriendTag_name");
        children.add(name);

        //��ʼ���������[�������ѿ�����չʾ��]
        newFriendPane = new Pane();
        newFriendPane.setPrefSize(850, 560);
        newFriendPane.getStyleClass().add("newFriend_Pane");
        ObservableList<Node> newFriendPaneChildren = newFriendPane.getChildren();

        //�û�������
        newFriendSearch = new TextField();
        newFriendSearch.setPrefSize(600, 50);
        newFriendSearch.setLayoutX(125);
        newFriendSearch.setLayoutY(25);
        newFriendSearch.setPromptText("�������û�ID");
        newFriendSearch.setPadding(new Insets(10));
        newFriendSearch.getStyleClass().add("newFriend_Search");
        newFriendPaneChildren.add(newFriendSearch);

        //�û��б��[��ʼ����δװ��]
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

