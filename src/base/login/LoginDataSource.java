package base.login;

import entity.User;

import java.io.IOException;

/**
 * 通过网络请求处理身份验证登录凭据并检索用户信息。
 */
public class LoginDataSource {

    //请求登录操作
    public Result login(String account, String password) {
        // TODO: 处理登录身份
        try {
            if (account.equals("123") && password.equals("123")) {

                User user = new User();
                return new Result.Success<>(user);
            } else
                return new Result.Error(new Exception("账号或密码错误"));
        } catch (Exception e) {
            return new Result.Error(new IOException("账号或密码错误", e));
        }
    }
}