package edu.handong.csee.isel.ChangeAnalysis;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.gumtreediff.actions.model.Action;

public class ChangeInfo {
    private String srcFilePath;
    private String dstFilePath;
    private ArrayList<Action> hunkInfo;
    private HashMap<String, String> fileHash;
    private HashMap<String, String> hunkHash;
    private String projectName;
    private String commitID;
    private int hunks;

    public ChangeInfo(String srcFilePath, String dstFilePath, String projectName, String commitId) {
        this.srcFilePath = srcFilePath;
        this.dstFilePath = dstFilePath;
        this.projectName = projectName;
        this.commitID = commitId;
        hunks = 0;
        hunkInfo = new ArrayList<Action>();
        fileHash = new HashMap<String, String>();
        hunkHash = new HashMap<String, String>();
    }

    public void addHunk(Action action) {
        ChangeAnalyzer changeAnalyzer = new ChangeAnalyzer();
        hunkInfo.add(action);
//        hunkHash.put(changeAnalyzer.computeSHA256Hash(action.toString()), );
        hunks++;
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
