package ui.login.code;

import entity.User;
import ui.chat.code.ChatController;
import ui.util.CacheUtil;

import java.io.IOException;

public class LoginController extends Login implements ILoginMethod {

    //定义登录事件
    private LoginEventDefine loginEventDefine;

    public LoginController() throws IOException {
        super();
    }

    @Override
    public void doShow() {
        //显示登录窗口
        show();
    }

    @Override
    public void doLoginError() {
        status.setText("登陆失败，账号或密码错误！");
    }

    @Override
    public void doLoginSuccess(User user) {
        //关闭当前窗口
        close();
        //跳转到聊天窗口
        ChatController chatController = null;
        try {
            chatController = new ChatController(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CacheUtil.chatController = chatController;
        chatController.doShow();
    }

    @Override
    public void initEventDefine() {
        loginEventDefine = new LoginEventDefine(this, this);
    }
}
