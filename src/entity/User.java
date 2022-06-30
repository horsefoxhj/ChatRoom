package entity;

import static base.Constants.OFFLINE;

/**
 * User
 *
 * @Author Hx
 * @Date 2022/5/15 22:12
 */
public class User {
    private int status;
    private int uid;
    private String name;
    private String account;
    private String password;
    private String header;

    public User(int uid, String name, String account, String password, String header) {
        this.status = OFFLINE;
        this.uid = uid;
        this.name = name;
        this.account = account;
        this.password = password;
        this.header = header;
    }

    public User() {
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
