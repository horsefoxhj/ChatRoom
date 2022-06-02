package ui.login.code;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import ui.UIParent;

import java.io.IOException;

/**
 * @Author Hx
 * @Date 2022/5/25 23:12
 * @Describe
 */
public abstract class Login extends UIParent {

    public TextField account;
    public PasswordField password;
    public Button btn_login;
    public Button btn_minimise;
    public Button btn_close;
    public Label status;

    public Login() throws IOException {
        root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/login/login.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        setResizable(false);
        //绑定UI
        bindUI();
        //初始化事件
        initEventDefine();
    }

    private void bindUI() {
        account = $("account", TextField.class);
        password = $("password", PasswordField.class);
        btn_minimise = $("btn_minimise", Button.class);
        btn_close = $("btn_close", Button.class);
        btn_login = $("btn_login", Button.class);
        status = $("status", Label.class);
    }
}
