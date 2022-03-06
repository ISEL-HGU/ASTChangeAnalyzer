package edu.handong.csee.isel.ChangeAnalysis;

import com.github.gumtreediff.actions.model.Action;
import script.model.EditOp;

public class ChangeData {
    private String actionsWithName;
    private String EditOpWithName;
    private String actionsWithType;
    private String EditOpWithType;

    public String getActionsWithName() { return actionsWithName; }
    public String getEditOpWithName() { return EditOpWithName; }
    public String getActionsWithType() { return actionsWithType; }
    public String getEditOpWithType() { return EditOpWithType; }

    public ChangeData() {
        actionsWithName = "";
        EditOpWithName = "";
        actionsWithType = "";
        EditOpWithType = "";
    }

    public void addAction(Action action) {
        actionsWithName = actionsWithName + action.getName() + "|";
        actionsWithType = actionsWithType + action.getName() + "@" + action.getNode().getType().toString().replaceAll("\\:\\s\\S$", "")
                .replaceAll("[0-9+,+0-9]", "").replaceAll("\\[", "").replaceAll("\\]","") + "|";
    }
  
    public void addEditOp(EditOp op) {
        EditOpWithName = EditOpWithName + op.getType() + "|" ;
        EditOpWithType = EditOpWithType + op.getType() + "@" + op.getNode().getLabel() + "&" + op.getLocation().getLabel() + "|";
    }
}
