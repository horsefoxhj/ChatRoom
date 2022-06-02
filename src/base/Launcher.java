package base;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.chat.code.ChatController;

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
//            LoginController loginController = new LoginController();
//            loginController.doShow();
            ChatController chatController = new ChatController();
            chatController.setUserInfo("1127", "С��", "header");
            chatController.addFriendUser(false, "1000004", "��˹��", "header");
            chatController.addFriendUser(false, "1000001", "���ǳ�", "header");
            chatController.addFriendUser(false, "1000002", "Ǿޱ", "header");
            chatController.addFriendUser(true, "1000003", "���г�", "header");

            chatController.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
