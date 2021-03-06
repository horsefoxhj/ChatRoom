package ui.login.code;

import base.login.LoginDataSource;
import base.login.Result;
import entity.User;

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
    @Override
    public void doLogin() {
        login.btn_login.setOnAction(actionEvent -> {
            //数据库检验账号，并返回检验结果
            Result result = new LoginDataSource()
                    .login(login.account.getText(), login.password.getText());

            //登陆结果为Success
            if (result instanceof Result.Success) {
                User user = ((Result.Success<User>) result).getData();
                loginMethod.doLoginSuccess(user);
            }
            //登录结果为Error
            else {
                loginMethod.doLoginError();
            }
        });
    }
}
