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
import ui.chat.code.data.TalkBoxData;
import ui.chat.code.element.group_bar_chat.MsgBox;
import ui.chat.code.element.group_bar_chat.TalkListItem;
import ui.chat.code.element.group_bar_friend.FriendListItem;
import ui.chat.code.element.group_bar_friend.GroupListItem;
import ui.util.CacheUtil;
import ui.util.Ids;

import java.io.IOException;
import java.util.Date;

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
        ItemPane.setOnMouseEntered(event -> newTalkListItem.delete().setVisible(true));
        ItemPane.setOnMouseExited(event -> newTalkListItem.delete().setVisible(false));

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
        talkListItem.fillMsgSketch(message.text,new Date(message.timeStamp));
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
        updateTalkListIdxAndSelected(talkListItem.pane(), talkListItem.msgRemind(), idxFirst, selected, isRemind);
    }

    @Override
    public void addTalkMsgRight(Message message, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        TalkListItem talkListItem = CacheUtil.talkMap.get(message.roomId);
        talkListItem.fillMsgSketch(message.text,new Date(message.timeStamp));
        ListView<Pane> listView = talkListItem.infoBoxList();
        Pane right = new MsgBox().right(userHeader, message.text);
        // ��Ϣ���
        listView.getItems().add(right);
        // ������
        listView.scrollTo(right);
        // ����λ��&ѡ��
        updateTalkListIdxAndSelected(talkListItem.pane(), talkListItem.msgRemind(), idxFirst, selected, isRemind);
    }

    @Override
    public void addFriendGroup(boolean selected, RoomInfo roomInfo) {
        GroupListItem groupListItem = new GroupListItem(roomInfo.roomId, roomInfo.roomName, roomInfo.header);
        Pane pane = groupListItem.pane();
        // ��ӵ�Ⱥ���б�
        ListView<Pane> groupListView = groupsList_ListView;
        ObservableList<Pane> items = groupListView.getItems();
        items.add(pane);
        groupListView.setPrefHeight(80 * items.size());
        groupsList_Pane.setPrefHeight(80 * items.size());
        //ѡ��
        if (selected) {
            groupListView.getSelectionModel().select(pane);
        }

        // Ⱥ�飬���ݿ�[��ʼ����δװ��]������Ⱥ����Ϣ���ݣ������ťʱ�����
        Pane detailContent = new Pane();
        detailContent.setPrefSize(850, 560);
        detailContent.getStyleClass().add("friendGroupDetailContent");
        ObservableList<Node> children = detailContent.getChildren();

        Button sendMsgButton = new Button();
        sendMsgButton.setId(roomInfo.roomId + "");
        sendMsgButton.getStyleClass().add("friendGroupSendMsgButton");
        sendMsgButton.setPrefSize(176, 50);
        sendMsgButton.setLayoutX(337);
        sendMsgButton.setLayoutY(450);
        sendMsgButton.setText("������Ϣ");
        chatEventDefine.switch2TalkPaneG(sendMsgButton, roomInfo);
        children.add(sendMsgButton);

        // ��Ӽ����¼�
        pane.setOnMousePressed(event -> {
            clearViewListSelectedAll($("friendList", ListView.class), $("groupListView", ListView.class));
            chatView.setContentPaneBox(roomInfo.roomId, roomInfo.roomName, detailContent);
        });
    }

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

        //��ѯ������
        DB db = DB.getInstance();
        RoomInfo roomInfo = db.queryRoomWithFriendId(userId, user.getUid());
        chatEventDefine.switch2TalkPaneG(sendMsgButton, roomInfo);
        children.add(sendMsgButton);

        // ��Ӽ����¼�
        pane.setOnMousePressed(event -> {
            clearViewListSelectedAll($("friendList", ListView.class), $("groupListView", ListView.class));
            chatView.setContentPaneBox(user.getUid(), user.getName(), detailContent);
        });
    }

    /**
     * @param talkPane       �Ի���Ԫ�����
     * @param msgRemindLabel ��Ϣ���ѱ�ǩ
     * @param idxFirst       �Ƿ�������λ
     * @param selected       �Ƿ�ѡ��
     * @param isRemind       �Ƿ�����
     * @Describe ���¶Ի����б�Ԫ��λ��ָ����ѡ��[��������Ϣ����ʱ����]
     */
    void updateTalkListIdxAndSelected(Pane talkPane, Label msgRemindLabel, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        // �Ի���ID������ID
        TalkBoxData talkBoxData = (TalkBoxData) talkPane.getUserData();
        // ��䵽�Ի���
        ListView<Pane> talkList = $("talkList", ListView.class);
        // �Ի���Ϊ�գ���ʼ��[�ö���ѡ�С�����]
        if (talkList.getItems().isEmpty()) {
            if (idxFirst) {
                talkList.getItems().add(0, talkPane);
            }
            if (selected) {
                // ���öԻ���[��ѡ��]
                talkList.getSelectionModel().select(talkPane);
            }
            isRemind(msgRemindLabel, isRemind);
            return;
        }

        // �Ի��ղ�Ϊ�գ��жϵ�һ��Ԫ���Ƿ�ǰ����Pane
        Pane firstPane = talkList.getItems().get(0);
        // �ж�Ԫ���Ƿ�����λ���������λ�ɷ��ز���Ҫ����������λ
        if (talkBoxData.getRoomId() == ((TalkBoxData) firstPane.getUserData()).getRoomId()) {
            Pane selectedItem = talkList.getSelectionModel().getSelectedItem();
            // ѡ���жϣ������һ��Ԫ���Ѿ�ѡ��[˵�����ڻỰ]����ô�����Ϣ����
            if (null == selectedItem) {
                isRemind(msgRemindLabel, isRemind);
                return;
            }
            TalkBoxData selectedItemUserData = (TalkBoxData) selectedItem.getUserData();
            if (null != selectedItemUserData && talkBoxData.getRoomId() == selectedItemUserData.getRoomId()) {
                clearRemind(msgRemindLabel);
            } else {
                isRemind(msgRemindLabel, isRemind);
            }
            return;
        }
        if (idxFirst) {
            talkList.getItems().remove(talkPane);
            talkList.getItems().add(0, talkPane);
        }
        if (selected) {
            // ���öԻ���[��ѡ��]
            talkList.getSelectionModel().select(talkPane);
        }
        isRemind(msgRemindLabel, isRemind);
    }

    /**
     * ��Ϣ����
     *
     * @param msgRemindLabel ��Ϣ���
     */
    private void isRemind(Label msgRemindLabel, Boolean isRemind) {
        if (!isRemind) return;
        msgRemindLabel.setVisible(true);
        RemindCount remindCount = (RemindCount) msgRemindLabel.getUserData();
        // ����10��չʾʡ�Ժ�
        if (remindCount.getCount() > 99) {
            msgRemindLabel.setText("������");
            return;
        }
        int count = remindCount.getCount() + 1;
        msgRemindLabel.setUserData(new RemindCount(count));
        msgRemindLabel.setText(String.valueOf(count));
    }

    private void clearRemind(Label msgRemindLabel) {
        msgRemindLabel.setVisible(false);
        msgRemindLabel.setUserData(new RemindCount(0));
    }
}
