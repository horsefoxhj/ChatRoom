package ui.login.design;

import base.login.LoginDataSource;
import base.login.Result;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @Author Hx
 * @Date 2022/5/26 11:06
 * @Describe
 */
public class LoginEventDefine {
    private Login login;
    private ILoginMethod loginMethod;
    private ILoginEvent loginEvent;

    public LoginEventDefine(Login login, ILoginEvent loginEvent, ILoginMethod loginMethod) {
        this.login = login;
        this.loginMethod = loginMethod;
        this.loginEvent = loginEvent;
        //设置窗口移动属性
        login.move();
        //窗口最小化
        min();
        //关闭窗口
        close();
        //登录
        doLogin();
    }

    // 事件；最小化
    private void min() {
        login.btn_minimise.setOnAction(event -> {
            login.setIconified(true);
        });
    }

    // 事件；退出
    private void close() {
        login.btn_close.setOnAction(event -> {
            //关闭窗口
            login.close();
            //退出程序
            System.exit(0);
        });
    }

    //绑定登录事件
    public void doLogin() {

        login.btn_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //数据库检验账号，并返回检验结果
                Result result = new LoginDataSource()
                        .login(login.account.getText(), login.password.getText());

                //登陆结果为Success
                if (result instanceof Result.Success) {
                    loginMethod.doLoginSuccess();
//                    User user = ((Result.Success<User>) result).getData();
                }
                //登录结果为Error
                else {
                    loginMethod.doLoginError();
                }
            }
        });
    }
}
