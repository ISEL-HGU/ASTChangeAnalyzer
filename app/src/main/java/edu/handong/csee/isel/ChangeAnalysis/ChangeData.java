package edu.handong.csee.isel.ChangeAnalysis;

import com.github.gumtreediff.actions.model.Action;
import script.model.EditOp;

public class ChangeData {
    private String actions;
    private String editOp;

    public String getActions() { return actions; }
    public String getEditOp() { return editOp; }

    public ChangeData() {
        actions = "";
        editOp = "";
    }

    public void addAction(Action action) {
        String location;
        if (action.getName().contains("delete") || action.getName().contains("update"))
            location = "";
        else location = getActionLocation(action);

        actions = actions + action.getName() + "@"
                + action.getNode().getType().toString().replaceAll("\\:\\s\\S$", "")
                .replaceAll("[0-9+,+0-9]", "").replaceAll("\\[", "").replaceAll("\\]","")
                + location.replace(" ", "") + "|";
	System.out.println(actions);
    }

    public String getActionLocation(Action action) {
        String[] actionStringList = action.toString().split("\n");
        boolean checked = false;
        for (int i=0; i<actionStringList.length; i++) {
            if (checked) {
                return "2" + actionStringList[i].replaceAll("\\:\\s\\S$", "")
                        .replaceAll("[0-9+,+0-9]", "").replaceAll("\\[", "").replaceAll("\\]","");
            }
            if (actionStringList[i].contains("\\[")) continue;
            else if (actionStringList[i].indexOf("to") == 0) checked = true;
        }
        return "";
    }

  
    public void addEditOp(EditOp op) {
        editOp = editOp + op.getType() + "@" + op.getNode().getLabel() + "&" + op.getLocation().getLabel() + "|";
    }
}
