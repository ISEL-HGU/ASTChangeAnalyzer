package edu.handong.csee.isel.ChangeAnalysis;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.gumtreediff.actions.model.Action;

public class ChangeInfo {
    private String srcFilePath;
    private String dstFilePath;
    private ArrayList<Action> hunkInfo;
    private String projectName;
    private String commitID;
    private String actionsWithName;
    private String actionsWithType;

    public String getActionsWithName() { return actionsWithName; }
    public String getActionsWithType() { return actionsWithType; }
    public String getProjectName() { return projectName; }
    public String getCommitID() { return commitID; }

    public ChangeInfo(String srcFilePath, String dstFilePath, String projectName, String commitId) {
        this.srcFilePath = srcFilePath;
        this.dstFilePath = dstFilePath;
        this.projectName = projectName;
        this.commitID = commitId;
        hunkInfo = new ArrayList<Action>();
        actionsWithName = "";
        actionsWithType = "";
    }

    public void addHunk(Action action) {
        hunkInfo.add(action);
        actionsWithName = actionsWithName + action.getName() + "|";
        actionsWithType = actionsWithType + action.getName() + "@" + action.getNode().getType().toString().replaceAll("\\:\s\\S$", "")
                .replaceAll("[0-9+,+0-9]", "").replaceAll("\\[", "").replaceAll("\\]","") + "|";
    }

    public void printChange() {
        System.out.println("\ncommitId: " + commitID
                + "\n L file path: "
                + "\n\t src: " + srcFilePath
                + "\n\t dst: " + dstFilePath
                + "\n L hunk info");
        for (Action action : hunkInfo) {
            System.out.println("\thunk name: \n" + action.getName()
                    + "\n\thunk type: " + action.getNode().getType());
        }
        System.out.println();
    }
}
