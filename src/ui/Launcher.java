package ui;

import base.login.LoginDataSource;
import base.login.Result;
import db.DB;
import entity.User;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static javafx.geometry.HPos.RIGHT;

/**
 * @Author Hx
 * @Date 2022/5/24 0:53
 * @Describe
 */
public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        DB db = DB.getInstance();

        stage.setTitle("SuperChatRoom");
        //允许开发者创建一个灵活的网格，按行列来布局其内容节点。
        GridPane grid = new GridPane();

//        grid.setGridLinesVisible(true);
        //居中
        grid.setAlignment(Pos.CENTER);
        //行列之间的间隔
        grid.setHgap(10);
        grid.setVgap(10);
        //面板边缘周围的间隔
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("登录界面");
        grid.add(sceneTitle, 0, 0, 1, 1);

        Label userName = new Label("账号:");
        grid.add(userName, 0, 1);

        TextField accountField = new TextField();
        accountField.setPromptText("Enter your account.");
        grid.add(accountField, 1, 1);

        Label pw = new Label("密码:");
        grid.add(pw, 0, 2);

        TextField passwordField = new TextField();
        passwordField.setPromptText("Enter your password.");
        grid.add(passwordField, 1, 2);

        Button btn = new Button("登录");

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 0, 6);
        GridPane.setColumnSpan(actionTarget, 2);
        GridPane.setHalignment(actionTarget, RIGHT);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Result result = new LoginDataSource()
                        .login(accountField.getText(), passwordField.getText());

                if (result instanceof Result.Success) {
                    actionTarget.setText("登录成功！");
                    //TODO:跳转到主页
                    User user = ((Result.Success<User>) result).getData();
                } else {
                    actionTarget.setText("登录失败，账号或密码错误");
                }
            }
        });
        Scene scene = new Scene(grid, 300, 275);
        stage.setScene(scene);
        //设置窗口是否可以缩放
        stage.setResizable(false);
        //设置永远在顶层
        stage.setAlwaysOnTop(true);
        stage.show();
    }
}
