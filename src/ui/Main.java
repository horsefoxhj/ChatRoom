package ui;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.login.design.LoginController;

/**
 * @Author Hx
 * @Date 2022/5/25 0:17
 * @Describe
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            LoginController loginController = new LoginController();
            loginController.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
