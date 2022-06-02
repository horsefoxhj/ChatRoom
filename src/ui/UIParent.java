package ui;

import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * @Author Hx
 * @Date 2022/5/25 23:46
 * @Describe
 */
public abstract class UIParent extends Stage {
    public Parent root;
    private double xOffset;
    private double yOffset;


    public double x() {
        return getX();
    }

    public double y() {
        return getY();
    }

    public double width() {
        return getWidth();
    }

    public double height() {
        return getHeight();
    }

    //窗口移动
    public void move() {
        root.setOnMousePressed(event -> {
            xOffset = getX() - event.getScreenX();
            yOffset = getY() - event.getScreenY();
            root.setCursor(Cursor.CLOSED_HAND);
        });
        root.setOnMouseDragged(event -> {
            setX(event.getScreenX() + xOffset);
            setY(event.getScreenY() + yOffset);
        });
        root.setOnMouseReleased(event -> {
            root.setCursor(Cursor.DEFAULT);
        });
    }

    //获取UI控件
    public <T> T $(String id, Class<T> clazz) {
        return (T) root.lookup("#" + id);
    }

    // 初始化事件定义
    public abstract void initEventDefine();
}
