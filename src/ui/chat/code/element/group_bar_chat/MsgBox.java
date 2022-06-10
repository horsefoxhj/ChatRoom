package ui.chat.code.element.group_bar_chat;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import ui.util.AutoSizeTool;

public class MsgBox {

    private Pane pane;

    private Pane header;              // 头像
    private Label name;         // 昵称区
    private Label infoContentArrow; // 内容箭头
    private TextArea infoContent;   // 内容

    /**
     * 好友消息
     *
     * @param name   名字
     * @param header 头像
     * @param msg    消息
     * @return pane
     */
    public Pane left(String name, String header, String msg) {

        double autoHeight = AutoSizeTool.getHeight(msg);
        double autoWidth = AutoSizeTool.getWidth(msg);

        pane = new Pane();
        pane.setPrefSize(500, 50 + autoHeight);
        pane.getStyleClass().add("infoBoxElement");
        ObservableList<Node> children = pane.getChildren();

        // 头像
        this.header = new Pane();
        this.header.setPrefSize(50, 50);
        this.header.setLayoutX(15);
        this.header.setLayoutY(15);
        this.header.getStyleClass().add("box_head");
        this.header.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/%s.png')", header));
        children.add(this.header);

        // 昵称
        this.name = new Label();
        this.name.setPrefSize(450, 20);
        this.name.setLayoutX(75);
        this.name.setLayoutY(5);
        this.name.setText(name);
        this.name.getStyleClass().add("box_nikeName");
        children.add(this.name);

        // 箭头
        infoContentArrow = new Label();
        infoContentArrow.setPrefSize(5, 20);
        infoContentArrow.setLayoutX(75);
        infoContentArrow.setLayoutY(30);
        infoContentArrow.getStyleClass().add("box_infoContent_arrow");
        children.add(infoContentArrow);

        // 内容
        infoContent = new TextArea();
        infoContent.setPrefWidth(autoWidth);
        infoContent.setPrefHeight(autoHeight);
        infoContent.setLayoutX(80);
        infoContent.setLayoutY(30);
        infoContent.setWrapText(true);
        infoContent.setEditable(false);
        infoContent.setText(msg);
        infoContent.getStyleClass().add("box_infoContent_left");
        children.add(infoContent);

        return pane;
    }

    /**
     * 个人消息
     *
     * @param header 用户头像
     * @param msg      消息
     * @return pane
     */
    public Pane right(String header, String msg) {

        double autoHeight = AutoSizeTool.getHeight(msg);
        double autoWidth = AutoSizeTool.getWidth(msg);

        pane = new Pane();
        pane.setPrefSize(500, 50 + autoHeight);
        pane.setLayoutX(853);
        pane.setLayoutY(0);
        pane.getStyleClass().add("infoBoxElement");
        ObservableList<Node> children = pane.getChildren();

        // 头像
        this.header = new Pane();
        this.header.setPrefSize(50, 50);
        this.header.setLayoutX(770);
        this.header.setLayoutY(15);
        this.header.getStyleClass().add("box_head");
        this.header.setStyle(String.format("-fx-background-image: url('file:src/ui/chat/img/%s.png')", header));
        children.add(this.header);

        // 箭头
        infoContentArrow = new Label();
        infoContentArrow.setPrefSize(5, 20);
        infoContentArrow.setLayoutX(755);
        infoContentArrow.setLayoutY(15);
        infoContentArrow.getStyleClass().add("box_infoContent_arrow");
        children.add(infoContentArrow);


        //消息文字
        infoContent = new TextArea();
        infoContent.setPrefWidth(autoWidth);
        infoContent.setPrefHeight(autoHeight);
        infoContent.setLayoutX(755 - autoWidth);
        infoContent.setLayoutY(15);
        infoContent.setWrapText(true);
        infoContent.setEditable(false);
        infoContent.setText(msg);
        infoContent.getStyleClass().add("box_infoContent_right");
        children.add(infoContent);

        return pane;
    }

}
