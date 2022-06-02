package ui.chat.code;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import ui.chat.code.data.RemindCount;
import ui.chat.code.element.group_bar_chat.ElementInfoBox;
import ui.chat.code.element.group_bar_chat.TalkListItem;
import ui.chat.code.element.group_bar_friend.FriendListItem;
import ui.chat.code.element.group_bar_friend.NewFriendItem;
import ui.util.CacheUtil;
import ui.util.Ids;

import java.io.IOException;
import java.util.Date;

public class ChatController extends Chat implements IChatMethod {

    private ChatView chatView;
    private ChatEventDefine chatEventDefine;

    public ChatController() throws IOException {
        super();
    }

    @Override
    public void initView() {
        chatView = new ChatView(this, chatEventDefine);
    }

    @Override
    public void initEventDefine() {
        chatEventDefine = new ChatEventDefine(this, this);
    }

    @Override
    public void doShow() {
        show();
    }

    @Override
    public void setUserInfo(String userId, String userNickName, String userHead) {
        super.userId = userId;
        super.userNickName = userNickName;
        super.userHead = userHead;
        //����ͷ��
        Button button = $("bar_portrait", Button.class);
        button.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/%s.png')", userHead));
    }

    /**
     * ���Ի����б�
     *
     * @param position    �Ի���λ�ã���λ0��Ĭ��-1
     * @param roomId     �Ի���ID��RoomID
     * @param roomName   �Ի�������
     * @param talkHead   �Ի���ͷ��
     * @param talkSketch �����������һ����Ϣ
     * @param talkDate   �Ի���ͨ��ʱ��
     * @param selected   ѡ��[true/false]
     */
    @Override
    public void addTalkBox(int position, int roomId, String roomName,
                           String talkHead, String talkSketch, Date talkDate, Boolean selected) {
        //��䵽�Ի���
        ListView<Pane> talkList = $("talkList", ListView.class);
        //�жϻỰ���Ƿ��иö���
        TalkListItem talkListItem = CacheUtil.talkMap.get(roomId);
        //�Ự�б��Ѿ����иö���
        if (null != talkListItem) {
            Node talkNode = talkList.lookup("#" + Ids.TalkListId.createTalkPaneId(roomId));

            //�ö���δ��䵽�б���
            if (null == talkNode) {
                talkList.getItems().add(position, talkListItem.pane());
                //���Ի�����Ϣ��
//                fillInfoBox(talkListItem, talkName);
            }
            if (selected) {
                //����ѡ��
                talkList.getSelectionModel().select(talkListItem.pane());
            }

            //���Ի�����Ϣ��
//            fillInfoBox(talkListItem, talkName);
            return;
        }

        //��ʼ���Ի���Ԫ��
        TalkListItem listItem = new TalkListItem(roomId, roomName, talkHead, talkSketch, talkDate);
        CacheUtil.talkMap.put(roomId, listItem);
        // ��䵽�Ի���
        ObservableList<Pane> items = talkList.getItems();
        Pane talkElementPane = listItem.pane();
        if (position >= 0) {
            items.add(position, talkElementPane);  // ��ӵ���һ��λ��
        } else {
            items.add(talkElementPane);           // ˳�����
        }
        if (selected) {
            talkList.getSelectionModel().select(talkElementPane);
        }

        //�Ի���Ԫ�ص���¼�
        talkElementPane.setOnMousePressed(event -> {
            //�����Ϣ��
//            fillInfoBox(talkElement, talkName);
            //�����Ϣ����
            Label msgRemind = listItem.msgRemind();
            msgRemind.setUserData(new RemindCount(0));
            msgRemind.setVisible(false);
        });

        //����¼�[����/�Ƴ�]
        talkElementPane.setOnMouseEntered(event -> {
            listItem.delete().setVisible(true);
        });
        talkElementPane.setOnMouseExited(event -> {
            listItem.delete().setVisible(false);
        });

        //���Ի�����Ϣ��
//        fillInfoBox(talkElement, talkName);
        //�ӶԻ�����ɾ��
        listItem.delete().setOnMouseClicked(event -> {
            talkList.getItems().remove(talkElementPane);
            $("info_pane_box", Pane.class).getChildren().clear();
            $("info_pane_box", Pane.class).setUserData(null);
            $("info_name", Label.class).setText("");
            listItem.infoBoxList().getItems().clear();
            listItem.clearMsgSketch();
            chatEventDefine.doDelTalkUser(super.userId, roomId);
        });
    }

    /**
     * ���Ի�����Ϣ����
     * @param talkListItem �Ի���Ԫ��
     * @param talkName    �Ի�������
     */
    private void fillInfoBox(TalkListItem talkListItem, String talkName) {
        String talkId = talkListItem.pane().getUserData().toString();
        //���Ի��б�
        Pane info_pane_box = $("info_pane_box", Pane.class);
        String boxUserId = (String) info_pane_box.getUserData();
        //�ж��Ƿ��Ѿ����[talkId]��������򷵻�
        if (talkId.equals(boxUserId)) return;

        ListView<Pane> listView = talkListItem.infoBoxList();
        info_pane_box.setUserData(talkId);
        info_pane_box.getChildren().clear();
        info_pane_box.getChildren().add(listView);
        //�Ի�������
        Label info_name = $("info_name", Label.class);
        info_name.setText(talkName);
    }
//
//    @Override
//    public void addTalkMsgUserLeft(String talkId, String msg, Integer msgType, Date msgDate, Boolean idxFirst, Boolean selected, Boolean isRemind) {
//        TalkListItem talkElement = CacheUtil.talkMap.get(talkId);
//        ListView<Pane> listView = talkElement.infoBoxList();
//        TalkData talkUserData = (TalkData) listView.getUserData();
//        Pane left = new ElementInfoBox().left(talkUserData.getTalkName(), talkUserData.getTalkHead(), msg, msgType);
//        // ��Ϣ���
//        listView.getItems().add(left);
//        // ������
//        listView.scrollTo(left);
//        talkElement.fillMsgSketch(0 == msgType ? msg : "[����]", msgDate);
//        // ����λ��&ѡ��
//        chatView.updateTalkListIdxAndSelected(0, talkElement.pane(), talkElement.msgRemind(), idxFirst, selected, isRemind);
//    }

//    @Override
//    public void addTalkMsgGroupLeft(String talkId, String userId, String userNickName, String userHead, String msg, Integer msgType, Date msgDate, Boolean idxFirst, Boolean selected, Boolean isRemind) {
//        // �Լ�����Ϣ����
//        if (super.userId.equals(userId)) return;
//        TalkListItem talkElement = CacheUtil.talkMap.get(talkId);
//        if (null == talkElement) {
//            GroupsData groupsData = (GroupsData) $(Ids.TalkListId.createFriendGroupId(talkId), Pane.class).getUserData();
//            if (null == groupsData) return;
//            addTalkBox(0, 1, talkId, groupsData.getGroupName(), groupsData.getGroupHead(), userNickName + "��" + msg, msgDate, false);
//            talkElement = CacheUtil.talkMap.get(talkId);
//            // �¼�֪ͨ(������Ⱥ�鷢����Ϣ)
//            chatEvent.doEventAddTalkGroup(super.userId, talkId);
//        }
//        ListView<Pane> listView = talkElement.infoBoxList();
//        Pane left = new ElementInfoBox().left(userNickName, userHead, msg, msgType);
//        // ��Ϣ���
//        listView.getItems().add(left);
//        // ������
//        listView.scrollTo(left);
//        talkElement.fillMsgSketch(0 == msgType ? userNickName + "��" + msg : userNickName + "��[����]", msgDate);
//        // ����λ��&ѡ��
//        chatView.updateTalkListIdxAndSelected(1, talkElement.pane(), talkElement.msgRemind(), idxFirst, selected, isRemind);
//    }

    @Override
    public void addTalkMsgRight(String talkId, String msg, Integer msgType, Date msgData, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        TalkListItem talkElement = CacheUtil.talkMap.get(talkId);
        ListView<Pane> listView = talkElement.infoBoxList();
        Pane right = new ElementInfoBox().right(userNickName, userHead, msg, msgType);
        // ��Ϣ���
        listView.getItems().add(right);
        // ������
        listView.scrollTo(right);
        talkElement.fillMsgSketch(0 == msgType ? msg : "[����]", msgData);
        // ����λ��&ѡ��
        chatView.updateTalkListIdxAndSelected(0, talkElement.pane(), talkElement.msgRemind(), idxFirst, selected, isRemind);
    }

//    @Override
//    public void addFriendGroup(String groupId, String groupName, String groupHead) {
//        ElementFriendGroup elementFriendGroup = new ElementFriendGroup(groupId, groupName, groupHead);
//        Pane pane = elementFriendGroup.pane();
//        // ��ӵ�Ⱥ���б�
//        ListView<Pane> groupListView = $("groupListView", ListView.class);
//        ObservableList<Pane> items = groupListView.getItems();
//        items.add(pane);
//        groupListView.setPrefHeight(80 * items.size());
//        $("friendGroupList", Pane.class).setPrefHeight(80 * items.size());
//
//        // Ⱥ�飬���ݿ�[��ʼ����δװ��]������Ⱥ����Ϣ���ݣ������ťʱ�����
//        Pane detailContent = new Pane();
//        detailContent.setPrefSize(850, 560);
//        detailContent.getStyleClass().add("friendGroupDetailContent");
//        ObservableList<Node> children = detailContent.getChildren();
//
//        Button sendMsgButton = new Button();
//        sendMsgButton.setId(groupId);
//        sendMsgButton.getStyleClass().add("friendGroupSendMsgButton");
//        sendMsgButton.setPrefSize(176, 50);
//        sendMsgButton.setLayoutX(337);
//        sendMsgButton.setLayoutY(450);
//        sendMsgButton.setText("������Ϣ");
//        chatEventDefine.doEventOpenFriendGroupSendMsg(sendMsgButton, groupId, groupName, groupHead);
//        children.add(sendMsgButton);
//
//        // ��Ӽ����¼�
//        pane.setOnMousePressed(event -> {
//            clearViewListSelectedAll($("friendList", ListView.class), $("userListView", ListView.class));
//            chatView.setContentPaneBox(groupId, groupName, detailContent);
//        });
//        chatView.setContentPaneBox(groupId, groupName, detailContent);
//    }
    /**
     * �������б�������
     *
     * @param selected     ѡ��;true/false
     * @param userFriendId       ����ID
     * @param userFriendNickName �����ǳ�
     * @param userFriendHead     ����ͷ��
     */
    @Override
    public void addFriendUser(boolean selected, String userFriendId, String userFriendNickName, String userFriendHead) {
        FriendListItem friendListItem = new FriendListItem(userFriendId, userFriendNickName, userFriendHead);
        Pane pane = friendListItem.pane();
        // ��ӵ������б�
//        ListView<Pane> userListView = $("friendsList_ListView", ListView.class);
        ListView<Pane> userListView = friendsList_ListView;
        ObservableList<Pane> items = userListView.getItems();
        items.add(pane);
        userListView.setPrefHeight(80 * items.size());
        friendsList_Pane.setPrefHeight(80 * items.size());
//        $("friendsList_Pane", Pane.class).setPrefHeight(80 * items.size());
        // ѡ��
        if (selected) {
            userListView.getSelectionModel().select(pane);
        }

        // ���ѣ����ݿ�[��ʼ����δװ��]�����غ�����Ϣ���ݣ������ťʱ�����
        Pane detailContent = new Pane();
        detailContent.setPrefSize(850, 560);
        detailContent.getStyleClass().add("friendUserDetailContent");
        ObservableList<Node> children = detailContent.getChildren();

        Button sendMsgButton = new Button();
        sendMsgButton.setId(userFriendId);
        sendMsgButton.getStyleClass().add("friendUserSendMsgButton");
        sendMsgButton.setPrefSize(176, 50);
        sendMsgButton.setLayoutX(337);
        sendMsgButton.setLayoutY(450);
        sendMsgButton.setText("������Ϣ");
//        chatEventDefine.doEventOpenFriendUserSendMsg(sendMsgButton, userFriendId, userFriendNickName, userFriendHead);
        children.add(sendMsgButton);
        // ��Ӽ����¼�
        pane.setOnMousePressed(event -> {
            clearViewListSelectedAll($("friendList", ListView.class), $("groupListView", ListView.class));
            chatView.setContentPaneBox(userFriendId, userFriendNickName, detailContent);
        });
        chatView.setContentPaneBox(userFriendId, userFriendNickName, detailContent);
    }

    /**
     * ��Ӻ���
     *
     * @param userId       ����ID
     * @param userNickName �����ǳ�
     * @param userHead     ����ͷ��
     * @param status       ״̬��0��ӡ�1����2�����
     */
    @Override
    public void addNewFriend(String userId, String userNickName, String userHead, Integer status) {
        NewFriendItem newFriendItem = new NewFriendItem(userId, userNickName, userHead, status);
        Pane pane = newFriendItem.pane();
        // ��ӵ������б�
        ListView<Pane> newFriend_ListView = $("newFriend_ListView", ListView.class);
        ObservableList<Pane> items = newFriend_ListView.getItems();
        items.add(pane);
        // ����¼�
        newFriendItem.statusLabel().setOnMousePressed(event -> {
            chatEventDefine.doAddUser(super.userId, userId);
        });
    }

//    @Override
//    public double getToolFaceX() {
//        return x() + width() - 960;
//    }
//
//    @Override
//    public double getToolFaceY() {
//        return y() + height() - 180;
//    }

}
