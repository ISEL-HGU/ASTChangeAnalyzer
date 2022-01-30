package edu.handong.csee.isel.ChangeAnalysis;

import java.util.ArrayList;

import com.github.gumtreediff.actions.model.Action;

public class ChangeInfo {
    private String filePath;
    private ArrayList<HunkInfo> hunkInfo;
    private ArrayList<Action> actions;
    private String commitID;

    public ChangeInfo(String filePath, String commitId) {
        this.filePath = filePath;
        this.commitID = commitId;
    }

    public void addHunk(Action action) {
        HunkInfo hunk = new HunkInfo(action);
        hunkInfo.add(hunk);
    }

    class HunkInfo {
        private String actionName;
        private String actionType;

        public HunkInfo(Action action) {
            actionName = action.getName();
            actionType = action.getNode().getType().toString();

        }
    }
}
