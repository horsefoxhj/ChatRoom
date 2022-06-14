package ui.login.code;

import entity.User;

/**
 * @Author Hx
 * @Date 2022/5/25 23:24
 * @Describe Login窗口方法
 */
public interface ILoginMethod {

    /**
     * 打开登陆窗口
     */
    void doShow();

    /**
     * 登陆失败
     */
    void doLoginError();

    /**
     * 登陆成功；跳转聊天窗口
     */
    void doLoginSuccess(User user);

}
