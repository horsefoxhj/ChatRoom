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
        //�������ߴ���һ���������񣬰����������������ݽڵ㡣
        GridPane grid = new GridPane();

//        grid.setGridLinesVisible(true);
        //����
        grid.setAlignment(Pos.CENTER);
        //����֮��ļ��
        grid.setHgap(10);
        grid.setVgap(10);
        //����Ե��Χ�ļ��
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("��¼����");
        grid.add(sceneTitle, 0, 0, 1, 1);

        Label userName = new Label("�˺�:");
        grid.add(userName, 0, 1);

        TextField accountField = new TextField();
        accountField.setPromptText("Enter your account.");
        grid.add(accountField, 1, 1);

        Label pw = new Label("����:");
        grid.add(pw, 0, 2);

        TextField passwordField = new TextField();
        passwordField.setPromptText("Enter your password.");
        grid.add(passwordField, 1, 2);

        Button btn = new Button("��¼");

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
                    actionTarget.setText("��¼�ɹ���");
                    //TODO:��ת����ҳ
                    User user = ((Result.Success<User>) result).getData();
                } else {
                    actionTarget.setText("��¼ʧ�ܣ��˺Ż��������");
                }
            }
        });
        Scene scene = new Scene(grid, 300, 275);
        stage.setScene(scene);
        //���ô����Ƿ��������
        stage.setResizable(false);
        //������Զ�ڶ���
        stage.setAlwaysOnTop(true);
        stage.show();
    }
}
