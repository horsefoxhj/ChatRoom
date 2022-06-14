package ui.login.code;

import base.login.LoginDataSource;
import base.login.Result;
import entity.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @Author Hx
 * @Date 2022/5/26 11:06
 * @Describe
 */
public class LoginEventDefine implements ILoginEvent {
    private Login login;
    private ILoginMethod loginMethod;

    public LoginEventDefine(Login login, ILoginMethod loginMethod) {
        this.login = login;
        this.loginMethod = loginMethod;
        //���ô����ƶ�����
        login.move();
        //������С��
        min();
        //�رմ���
        close();
        //��¼
        doLogin();
    }

    // �¼�����С��
    private void min() {
        login.btn_minimise.setOnAction(event -> {
            login.setIconified(true);
        });
    }

    // �¼����˳�
    private void close() {
        login.btn_close.setOnAction(event -> {
            //�رմ���
            login.close();
            //�˳�����
            System.exit(0);
        });
    }

    //�󶨵�¼�¼�
    @Override
    public void doLogin() {
        login.btn_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //���ݿ�����˺ţ������ؼ�����
                Result result = new LoginDataSource()
                        .login(login.account.getText(), login.password.getText());

                //��½���ΪSuccess
                if (result instanceof Result.Success) {
                    User user = ((Result.Success<User>) result).getData();
                    loginMethod.doLoginSuccess(user);
                }
                //��¼���ΪError
                else {
                    loginMethod.doLoginError();
                }
            }
        });
    }
}
