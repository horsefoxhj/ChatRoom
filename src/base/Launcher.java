package base;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.login.code.LoginController;

/**
 * @Author Hx
 * @Date 2022/5/26 12:23
 * @Describe
 */
public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            LoginController loginController = new LoginController();
            loginController.doShow();
//            ChatController chatController = new ChatController(1127, "Ð¡ºÎ", "header");
//            CacheUtil.chatController = chatController;
//            chatController.doShow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
