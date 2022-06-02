package ui.login.code;

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
    public void doLoginSuccess() {
        //�رյ�ǰ����
        close();
        //TODO:��ת�����촰��
    }

    @Override
    public void initEventDefine() {
        loginEventDefine = new LoginEventDefine(this, this);
    }
}
