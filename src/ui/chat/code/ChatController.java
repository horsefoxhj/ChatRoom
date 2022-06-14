package ui.chat.code;

import db.DB;
import entity.Message;
import entity.RoomInfo;
import entity.User;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import ui.chat.code.data.RemindCount;
import ui.chat.code.element.group_bar_chat.MsgBox;
import ui.chat.code.element.group_bar_chat.TalkListItem;
import ui.chat.code.element.group_bar_friend.FriendListItem;
import ui.util.CacheUtil;
import ui.util.Ids;

import java.io.IOException;

public class ChatController extends Chat implements IChatMethod {

    private ChatView chatView;
    private ChatEventDefine chatEventDefine;

    /**
     * ���õ�½�û�
     */
    public ChatController(User user) throws IOException {
        super(user);
        //����ͷ��
        Button button = $("bar_portrait", Button.class);
        button.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/%s.png')", user.getHeader()));
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

    /**
     * ���Ի����б�
     *
     * @param position �Ի���λ�ã���λ0��Ĭ��-1
     * @param roomInfo ��������Ϣ
     * @param selected ѡ��[true/false]
     */
    @Override
    public void addTalkBox(int position, RoomInfo roomInfo, Boolean selected) {

        //��䵽�Ի���
        ListView<Pane> talkList = $("talkList", ListView.class);
        //�жϻỰ���Ƿ��иö���
        TalkListItem talkListItem = CacheUtil.talkMap.get(roomInfo.roomId);
        //�Ự�б��Ѿ����иö���
        if (null != talkListItem) {
            Node talkNode = talkList.lookup("#" + Ids.TalkListId.createTalkPaneId(roomInfo.roomId));

            //�ö���δ��䵽�б���
            if (null == talkNode) {
                talkList.getItems().add(position, talkListItem.pane());
                //���Ի�����Ϣ��
                fillInfoBox(talkListItem, roomInfo.roomName);
            }
            if (selected) {
                //����ѡ��
                talkList.getSelectionModel().select(talkListItem.pane());
            }
            //���Ի�����Ϣ��
            fillInfoBox(talkListItem, roomInfo.roomName);
            return;
        }

        //��ʼ���Ի���Ԫ��
        TalkListItem newTalkListItem = new TalkListItem(roomInfo);
        CacheUtil.talkMap.put(roomInfo.roomId, newTalkListItem);
        // ��䵽�Ի���
        ObservableList<Pane> items = talkList.getItems();
        Pane ItemPane = newTalkListItem.pane();
        if (position >= 0) {
            items.add(position, ItemPane);  // ��ӵ���һ��λ��
        } else {
            items.add(ItemPane);           // ˳�����
        }
        if (selected) {
            talkList.getSelectionModel().select(ItemPane);
        }

        //�Ի���Ԫ�ص���¼�
        ItemPane.setOnMousePressed(event -> {
            //�����Ϣ��
            fillInfoBox(newTalkListItem, roomInfo.roomName);
            //�����Ϣ����
            Label msgRemind = newTalkListItem.msgRemind();
            msgRemind.setUserData(new RemindCount(0));
            msgRemind.setVisible(false);
        });

        //����¼�[����/�Ƴ�]
        ItemPane.setOnMouseEntered(event -> {
            newTalkListItem.delete().setVisible(true);
        });
        ItemPane.setOnMouseExited(event -> {
            newTalkListItem.delete().setVisible(false);
        });

        //���Ի�����Ϣ��
        fillInfoBox(newTalkListItem, roomInfo.roomName);
        //�ӶԻ�����ɾ��
        newTalkListItem.delete().setOnMouseClicked(event -> {
            talkList.getItems().remove(ItemPane);
            $("info_pane_box", Pane.class).getChildren().clear();
            $("info_pane_box", Pane.class).setUserData(null);
            $("info_name", Label.class).setText("");
            newTalkListItem.infoBoxList().getItems().clear();
            newTalkListItem.clearMsgSketch();
            chatEventDefine.doDelTalkUser(super.userId, roomInfo.roomId);
        });
    }

    /**
     * ���Ի�����Ϣ����
     *
     * @param talkListItem �Ի���Ԫ��
     * @param roomName     �Ի�������
     */
    private void fillInfoBox(TalkListItem talkListItem, String roomName) {
        String roomId = talkListItem.pane().getUserData().toString();
        //���Ի��б�
        Pane info_pane_box = $("info_pane_box", Pane.class);
        String boxUserId = (String) info_pane_box.getUserData();
        //�ж��Ƿ��Ѿ����[talkId]��������򷵻�
        if (roomId.equals(boxUserId)) return;

        ListView<Pane> listView = talkListItem.infoBoxList();
        info_pane_box.setUserData(roomId);
        info_pane_box.getChildren().clear();
        info_pane_box.getChildren().add(listView);
        //�Ի�������
        Label info_name = $("info_name", Label.class);
        info_name.setText(roomName);
    }

    @Override
    public void addTalkMsgLeft(Message message, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        TalkListItem talkListItem = CacheUtil.talkMap.get(message.roomId);
        ListView<Pane> listView = talkListItem.infoBoxList();
        //��ѯ�û�
        User user = DB.getInstance().queryUserById(message.uid);
        //������������
        Pane left = new MsgBox().left(user.getName(), user.getHeader(), message.text);
        // ��Ϣ���
        listView.getItems().add(left);
        // ������
        listView.scrollTo(left);
        // ����λ��&ѡ��
        chatView.updateTalkListIdxAndSelected(talkListItem.pane(), talkListItem.msgRemind(), idxFirst, selected, isRemind);
    }

    @Override
    public void addTalkMsgRight(Message message, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        TalkListItem talkListItem = CacheUtil.talkMap.get(message.roomId);
        ListView<Pane> listView = talkListItem.infoBoxList();
        Pane right = new MsgBox().right(userHeader, message.text);
        // ��Ϣ���
        listView.getItems().add(right);
        // ������
        listView.scrollTo(right);
        // ����λ��&ѡ��
        chatView.updateTalkListIdxAndSelected(talkListItem.pane(), talkListItem.msgRemind(), idxFirst, selected, isRemind);
    }

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
//        Pane left = new MsgBox().left(userNickName, userHead, msg, msgType);
//        // ��Ϣ���
//        listView.getItems().add(left);
//        // ������
//        listView.scrollTo(left);
//        talkElement.fillMsgSketch(0 == msgType ? userNickName + "��" + msg : userNickName + "��[����]", msgDate);
//        // ����λ��&ѡ��
//        chatView.updateTalkListIdxAndSelected(1, talkElement.pane(), talkElement.msgRemind(), idxFirst, selected, isRemind);
//    }

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
     * @param selected ѡ��;true/false
     * @param user     �û���Ϣ
     */
    @Override
    public void addFriendUser(boolean selected, User user) {
        FriendListItem friendListItem = new FriendListItem(user.getUid(), user.getName(), user.getHeader());
        Pane pane = friendListItem.pane();
        // ��ӵ������б�
        ListView<Pane> userListView = friendsList_ListView;
        ObservableList<Pane> items = userListView.getItems();
        items.add(pane);
        userListView.setPrefHeight(80 * items.size());
        friendsList_Pane.setPrefHeight(80 * items.size());
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
        sendMsgButton.setId(user.getUid() + "");
        sendMsgButton.getStyleClass().add("friendUserSendMsgButton");
        sendMsgButton.setPrefSize(176, 50);
        sendMsgButton.setLayoutX(337);
        sendMsgButton.setLayoutY(450);
        sendMsgButton.setText("������Ϣ");

        chatEventDefine.switchFriendTalkPane(sendMsgButton, user.getUid());

        children.add(sendMsgButton);
        // ��Ӽ����¼�
        pane.setOnMousePressed(event -> {
            clearViewListSelectedAll($("friendList", ListView.class), $("groupListView", ListView.class));
            chatView.setContentPaneBox(user.getUid(), user.getName(), detailContent);
        });
    }
}
