package base.login;


/**
 * 身份验证结果：成功（用户详细信息）或错误消息。
 */
class LoginResult {

    private LoggedInUser successInfo;
    private Integer error;

    LoginResult(Integer error) {
        this.error = error;
    }

    LoginResult(LoggedInUser successInfo) {
        this.successInfo = successInfo;
    }

    LoggedInUser getSuccessInfo() {
        return successInfo;
    }

    Integer getError() {
        return error;
    }
}