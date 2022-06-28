package ui.util;

/**
 * 组件ID
 */
public class Ids {

    /**
     * 对话框元素，好友对话列表框元素
     */
    public static class TalkListId {

        public static String createTalkPaneId(int id) {
            return "TalkId_TalkPaneId_" + id;
        }

        public static String analysisTalkPaneId(String id) {
            return id.split("_")[2];
        }

        public static String createInfoBoxListId(int id) {
            return "TalkId_InfoBoxListId_" + id;
        }

        public static String analysisInfoBoxListId(String id) {
            return id.split("_")[2];
        }

        public static String createMsgDataId(int id) {
            return "TalkId_MsgDataId_" + id;
        }

        public static String analysisMsgDataId(String id) {
            return id.split("_")[2];
        }

        public static String createMsgSketchId(int id) {
            return "TalkId_MsgSketchId_" + id;
        }

        public static String analysisMsgSketchId(String id) {
            return id.split("_")[2];
        }

        public static String createFriendGroupId(int id) {
            return "TalkId_GroupId_" + id;
        }

    }

}
