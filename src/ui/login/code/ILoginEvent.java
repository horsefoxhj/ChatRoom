package ui.login.code;

/**
 * @Author Hx
 * @Date 2022/5/25 23:24
 * @Describe LoginEvent
 */
public interface ILoginEvent {
    /**
     * ��½��֤
     * @param userId        �û�ID
     * @param userPassword  �û�����
     */
    void doLogin();
}
