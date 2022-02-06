package edu.handong.csee.isel.DiffTools;

import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.SyntaxException;
import com.github.gumtreediff.gen.TreeGenerators;
import com.github.gumtreediff.tree.Tree;
import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import edu.handong.csee.isel.RepoMiner.ASTExtractor;

import main.LAS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import main.LAS;

public class LASTool {
    private boolean level;
    private String fileExtension;
    private String srcFileSource;
    private String dstFileSource;
    private Tree src;
    private Tree dst;


    public LASTool(boolean level, String fileExtension, String srcFileSource, String dstFileSource) {
        this.level = level;
        this.fileExtension = fileExtension;
        this.srcFileSource = srcFileSource;
        this.dstFileSource = dstFileSource;
    }

    public ChangeInfo constructChange(ChangeInfo changeInfo) {

        EditScript editscript = null;
        List<Action> actionList = null;

        Run.initGenerators(); // registers the available parsers
        File srcFile = new File("srcFile.java");
        File dstFile = new File("dstFile.java");
        try {
            BufferedWriter srcWriter = new BufferedWriter(new FileWriter(srcFile));
            srcWriter.write(srcFileSource);
            srcWriter.close();
            BufferedWriter dstWriter = new BufferedWriter(new FileWriter(dstFile));
            dstWriter.write(dstFileSource);
            dstWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        LAS las = new LAS();
        String [] args = {"srcFile.java","dstFile.java"};
        las.main(args);
        srcFile.delete();
        dstFile.delete();

//        int actionCount = 0;
//        for (Action action : actionList) {
//            actionCount++;
//            if (level) {
//                changeInfo.addHunk(action);
//                continue;
//            }
//            System.out.println("\n#" + actionCount
//                    + "\n L action name: " + action.getName()
//                    + "\n L action type: " + action.getNode().getType()
//                    + "\n L action Position info: " + action.getNode().getPos() + "-" + action.getNode().getEndPos());
//            System.out.println("\nsrc: " + src.getTreesBetweenPositions(action.getNode().getPos(), action.getNode().getEndPos())
//                    + "\n L hash: " + src.getTreesBetweenPositions(action.getNode().getPos(), action.getNode().getEndPos()).hashCode());
//            System.out.println("\ndst: " + dst.getTreesBetweenPositions(action.getNode().getPos(), action.getNode().getEndPos())
//                    + "\n L hash: " + dst.getTreesBetweenPositions(action.getNode().getPos(), action.getNode().getEndPos()).hashCode());
//        }
        return changeInfo;
    }

}
