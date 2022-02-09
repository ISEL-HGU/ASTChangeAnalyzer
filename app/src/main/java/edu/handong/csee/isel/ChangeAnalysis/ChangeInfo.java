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
    private String actionsWithName;
    private String EditOpWithName;
    private String actionsWithType;

    public String getActionsWithName() { return actionsWithName; }
    public String getEditOpWithName() { return EditOpWithName; }
    public String getActionsWithType() { return actionsWithType; }
    public String getProjectName() { return projectName; }
    public String getCommitID() { return commitID; }

    public ChangeInfo(String srcFilePath, String dstFilePath, String projectName, String commitId) {
        this.srcFilePath = srcFilePath;
        this.dstFilePath = dstFilePath;
        this.projectName = projectName;
        this.commitID = commitId;
        actionInfo = new ArrayList<Action>();
        editInfo = new ArrayList<EditOp>();
        actionsWithName = "";
        EditOpWithName = "";
        actionsWithType = "";
    }

    public void addAction(Action action) {
        actionInfo.add(action);
        actionsWithName = actionsWithName + action.getName() + "|";
        actionsWithType = actionsWithType + action.getName() + "@" + action.getNode().getType().toString().replaceAll("\\:\\s\\S$", "")
                .replaceAll("[0-9+,+0-9]", "").replaceAll("\\[", "").replaceAll("\\]","") + "|";
    }
  
    public void addEditOp(EditOp op) {
        editInfo.add(op);
        EditOpWithName = EditOpWithName + op.getType() + "|" ;
    }
}
