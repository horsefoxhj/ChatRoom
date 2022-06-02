package ui.login.code;

/**
 * @Author Hx
 * @Date 2022/5/25 23:24
 * @Describe LoginEvent
 */
public interface ILoginEvent {
    /**
     * 登陆验证
     * @param userId        用户ID
     * @param userPassword  用户密码
     */
    void doLogin();
}
