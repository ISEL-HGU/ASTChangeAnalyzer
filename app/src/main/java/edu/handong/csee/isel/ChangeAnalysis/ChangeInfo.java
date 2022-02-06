package edu.handong.csee.isel.ChangeAnalysis;

import java.util.ArrayList;

import com.github.gumtreediff.actions.model.Action;
import script.model.EditOp;

public class ChangeInfo {
    private String srcFilePath;
    private String dstFilePath;
    private ArrayList<Action> actionInfo;
    private ArrayList<EditOp> editInfo;
    private String projectName;
    private String commitID;
    private String hunks;

    public String getHunks() { return hunks; }
    public String getProjectName() { return projectName; }
    public String getCommitID() { return commitID; }

    public ChangeInfo(String srcFilePath, String dstFilePath, String projectName, String commitId) {
        this.srcFilePath = srcFilePath;
        this.dstFilePath = dstFilePath;
        this.projectName = projectName;
        this.commitID = commitId;
        actionInfo = new ArrayList<Action>();
        editInfo = new ArrayList<EditOp>();
        hunks = "";
    }

    public void addAction(Action action) {
        actionInfo.add(action);
        hunks = hunks + action.getName() + "|" ;
    }
    public void addEditOp(EditOp op) {
        editInfo.add(op);
        hunks = hunks + op.getType() + "|" ;
    }

    public void printChange() {
        System.out.println("\ncommitId: " + commitID
                + "\n L file path: "
                + "\n\t src: " + srcFilePath
                + "\n\t dst: " + dstFilePath
                + "\n L hunk info");
        for (Action action : actionInfo) {
            System.out.println("\thunk name: \n" + action.getName()
                    + "\n\thunk type: " + action.getNode().getType());
        }
        System.out.println();
    }
}
