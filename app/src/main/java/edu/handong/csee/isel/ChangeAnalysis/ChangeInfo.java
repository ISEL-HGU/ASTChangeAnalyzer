package edu.handong.csee.isel.ChangeAnalysis;

import java.util.ArrayList;

import com.github.gumtreediff.actions.model.Action;

public class ChangeInfo {
    private String srcFilePath;
    private String dstFilePath;
    private ArrayList<HunkInfo> hunkInfo;
    private int hunks;
    private String commitID;

    public ChangeInfo(String srcFilePath, String dstFilePath, String commitId) {
        this.srcFilePath = srcFilePath;
        this.dstFilePath = dstFilePath;
        this.commitID = commitId;
        hunks = 0;
        hunkInfo = new ArrayList<HunkInfo>();
    }

    public void addHunk(Action action) {
        HunkInfo hunk = new HunkInfo(action);
        hunkInfo.add(hunk);
        hunks++;
    }

    public void printChange() {
        System.out.println("\ncommitId: " + commitID
                + "\n L file path: "
                + "\n\t src: " + srcFilePath
                + "\n\t dst: " + dstFilePath
                + "\n L hunk info");
        int hunks=0;
        for (HunkInfo hunk : hunkInfo) {
            System.out.println("\t#" + ++hunks);
            System.out.println("\thunk name: \n" + hunk.getAction()
                    + "\n\thunk type: " + hunk.getActionType()
                    + "\n\thunk position: " + hunk.getActionPos());
        }
        System.out.println();
    }

    class HunkInfo {
        private String actionName;
        private String actionType;
        private String actionPos;
        private Action action;

        public HunkInfo(Action action) {
            actionName = action.getName();
            actionType = action.getNode().getType().toString();
            actionPos = action.getNode().getPos() + "-" + action.getNode().getEndPos();
            this.action = action;
        }

        public String getActionName() {
            return actionName;
        }

        public String getActionType() {
            return actionType;
        }

        public String getActionPos() {
            return actionPos;
        }

        public Action getAction() {
            return action;
        }
    }
}
