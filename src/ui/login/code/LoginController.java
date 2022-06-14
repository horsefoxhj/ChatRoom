package ui.login.code;

import entity.User;
import ui.chat.code.ChatController;
import ui.util.CacheUtil;

import java.io.IOException;

public class LoginController extends Login implements ILoginMethod {

    //�����¼�¼�
    private LoginEventDefine loginEventDefine;

    public LoginController() throws IOException {
        super();
    }

    @Override
    public void doShow() {
        //��ʾ��¼����
        show();
    }

    @Override
    public void doLoginError() {
        status.setText("��½ʧ�ܣ��˺Ż��������");
    }

    @Override
    public void doLoginSuccess(User user) {
        //�رյ�ǰ����
        close();
        //��ת�����촰��
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
