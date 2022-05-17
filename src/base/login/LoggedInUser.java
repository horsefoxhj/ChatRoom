package base.login;

import entity.User;

/**
 * 向 UI 公开经过身份验证的用户详细信息, 存放UI可调用的数据
 */
public class LoggedInUser {

    private User user;

    public LoggedInUser(User user) {
        this.user = user;
    }

    public User getName() {
        return user;
    }
}