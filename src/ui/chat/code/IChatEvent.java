package ui.chat.code;

import entity.Message;
import entity.User;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

public interface IChatEvent {

    /**
     * 聊天窗口退出操作
     */
    void doQuit();

    /**
     * 发送消息按钮
     *
     * @param message 消息
     */
    void doSendMsg(Message message);

    /**
     * 事件处理；开启与好友发送消息 [点击发送消息时候触发 -> 添加到对话框、选中、展示对话列表]
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     */
    void doAddTalkUser(int userId, int friendId);

    /**
     * 事件处理；开启与群组发送消息
     *
     * @param userId  用户ID
     * @param groupId 群组ID
     */
    void doAddTalkGroup(int userId, String groupId);

    /**
     * 事件处理；删除指定对话框
     *
     * @param userId 用户ID
     * @param roomId 对话框ID
     */
    void doDelTalkUser(int userId, int roomId);

    /**
     * 事件处理；查询用户时，添加到查询列表
     *
     * @param user     用户信息
     * @param listView 用户列表[非必需使用，同步接口可使用]
     * @param status   用户状态
     */
    void doLoadNewFriend(User user, ListView<Pane> listView, Integer status);

    /**
     * 事件处理；好友搜索[搜索后结果调用添加：]
     *
     * @param friendId 用户ID
     */
    void doSearchFriend(int friendId);

    /**
     * 添加好友事件
     *
     * @param userId   个人ID
     * @param friendId 好友ID
     */
    void doAddFriend(int userId, int friendId);

    /**
     * 创建群聊
     *
     * @param uid 个人ID
     */
    void doCreateGroup(int uid);
}
