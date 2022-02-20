package edu.handong.csee.isel.Main;

import edu.handong.csee.isel.ChangeAnalysis.ChangeAnalyzer;
import edu.handong.csee.isel.ChangeAnalysis.IndexParser;
import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Processor implements Runnable {
    private String language;
    private String DiffTool;
    private String input;
    private boolean isChangeMine;
    private boolean isGitClone;
    private String savePath;
    private String project;
    private String projectName;
    private int volume = 0;

    public void setProperties(String language, String DiffTool, String input, boolean isChangeMine, boolean isGitClone, String savePath) {
        this.language = language;
        this.DiffTool = DiffTool;
        this.input = input;
        this.isChangeMine = isChangeMine;
        this.isGitClone = isGitClone;
        this.savePath = savePath;
    }

    public void setProjectProperties(String project, String projectName) {
        this.project = project;
        this.projectName = projectName;
    }

    @Override
    public void run() {
        try {
            ChangeAnalyzer changeAnalyzer = new ChangeAnalyzer(input);
            changeAnalyzer.setProjectName(projectName);
            if (!isChangeMine) changeAnalyzer.printStatistic();
            CommitMiner commitMine = new CommitMiner(project, isGitClone);
            if (commitMine.isCompleted()) {
                ChangeMiner changeMine = new ChangeMiner();
                changeMine.setProperties(commitMine.getFilePath(), commitMine.getRepo(), language, DiffTool, projectName);
                if (isChangeMine) volume += changeMine.collect(commitMine.getCommitList());
                else { changeMine.collect(commitMine.getCommitList(), changeAnalyzer); }
            }
            changeAnalyzer.setDone();
            if (isChangeMine) System.out.println("Changed Mined: " + volume);
            else if (isGitClone) return;
            else {
                changeAnalyzer.printStatistic();
                writeObjectToFile(changeAnalyzer);
                new IndexParser(savePath,changeAnalyzer.getCoreMap());
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
