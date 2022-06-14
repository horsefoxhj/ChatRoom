package ui.login.code;

import entity.User;

/**
 * @Author Hx
 * @Date 2022/5/25 23:24
 * @Describe Login���ڷ���
 */
public interface ILoginMethod {

    /**
     * �򿪵�½����
     */
    void doShow();

    /**
     * ��½ʧ��
     */
    void doLoginError();

    /**
     * ��½�ɹ�����ת���촰��
     */
    void doLoginSuccess(User user);

}
