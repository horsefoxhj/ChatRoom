package base.login;

import db.DB;
import entity.User;

import java.io.IOException;

import static base.Constants.ONLINE;

/**
 * 通过网络请求处理身份验证登录凭据并检索用户信息。
 */
public class LoginDataSource {

    //请求登录操作
    public Result login(String account, String password) {
        //处理登录身份
        try {
            DB db = DB.getInstance();
            User user = db.login(account, password);
            //登录成功
            if (user != null) {
                user.setStatus(ONLINE);
                //TODO:数据库中更新用户状态为在线

                return new Result.Success<>(user);
            } else
                return new Result.Error(new Exception("登录失败,账号或密码错误~"));
        } catch (Exception e) {
            return new Result.Error(new IOException("登陆失败~", e));
        }
    }
}