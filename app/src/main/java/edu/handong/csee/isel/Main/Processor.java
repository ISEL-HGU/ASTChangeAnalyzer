package edu.handong.csee.isel.Main;

import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import edu.handong.csee.isel.ChangeAnalysis.IndexParser;
import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


public class Processor implements Runnable {
    private String language;
    private String DiffTool;
    private String input;
    private boolean isChangeCount;
    private boolean isGitClone;
    private String savePath;
    private String project;
    private String projectName;
    private int volume = 0;

    public void setProperties(String language, String DiffTool, String input, boolean isChangeCount, boolean isGitClone, String savePath) {
        this.language = language;
        this.DiffTool = DiffTool;
        this.input = input;
        this.isChangeCount = isChangeCount;
        this.isGitClone = isGitClone;
        this.savePath = savePath;
    }

    public void setProjectProperties(String project) {
        this.project = project;
    }

    @Override
    public void run() {
        try {
            ChangeInfo changeInfo = new ChangeInfo(input);
            CommitMiner commitMine = new CommitMiner(project, isGitClone);
            projectName = commitMine.getMatcherGroup().replaceAll("/", "~");
            changeInfo.setProjectName(projectName);
            if (commitMine.isCompleted()) {
                ChangeMiner changeMine = new ChangeMiner();
                changeMine.setProperties(commitMine.getFilePath(), commitMine.getRepo(), language, DiffTool);
                if (isChangeCount) volume += changeMine.collect(commitMine.getCommitList());
                else { changeMine.collect(projectName, commitMine.getCommitList(), changeInfo); }
                if (isChangeCount) System.out.println("Changed Mined: " + volume);
                else if (isGitClone) return;
                else {
                    if (changeInfo.getHashMap().size() == 0) return;
                    writeObjectToFile(changeInfo);
                    synchronized (this) {
                        IndexParser index = new IndexParser(savePath, changeInfo.getHashMap());
                        index.generateIndex();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void writeObjectToFile (Object changeAnalyzer) {
        try {
//			FileOutputStream fileOut = new FileOutputStream("/data/CGYW/ASTChangeAnalyzer/ASTChanges.chg", true);
            FileOutputStream fileOut = new FileOutputStream(savePath + "/" +  projectName + ".chg");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(changeAnalyzer);
            objectOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
