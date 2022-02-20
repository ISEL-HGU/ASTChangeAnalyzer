package edu.handong.csee.isel.DiffTools;

import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;

import main.LAS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import script.ScriptGenerator;
import script.model.EditOp;
import tree.TreeBuilder;

public class LASTool {
    private String filePath;
    private String srcFileSource;
    private String dstFileSource;
    private int commitCount = 0;


    public LASTool(String filepath, String srcFileSource, String dstFileSource) {
        this.filePath = filepath;
        this.srcFileSource = srcFileSource;
        this.dstFileSource = dstFileSource;
    }

    public ChangeInfo constructChange(ChangeInfo changeInfo) {
        commitCount++;
        File srcFile = new File(filePath + "/srcFile.java");
        File dstFile = new File(filePath + "/dstFile.java");
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

        int actionCount = 0;

        System.setProperty("las.dist.threshold", "0.5");
        System.setProperty("las.depth.threshold", "3");

        try {
            tree.Tree before = TreeBuilder.buildTreeFromFile(srcFile);
            tree.Tree after = TreeBuilder.buildTreeFromFile(dstFile);

            script.model.EditScript script = ScriptGenerator.generateScript(before, after);

            for(EditOp op : script.getEditOps()){
                //System.out.println(op);
//               actionCount++;
//               System.out.println("\n#" +commitCount + "-" + actionCount
//                  + "\n L action name: " + op.getType()
//                       + "\n L action type: " + op.getNode().getLabel()
//                       + "\n L action Position info: " + op.getNode().getLineNumber()
//                       + "\n L location type: " + op.getLocation().getLabel()
//                       + "\n L location Position info: " + op.getLocation().getLineNumber());
//               System.out.println(srcFileSource);
//               System.out.println("########################################");
//               System.out.println(dstFileSource);
               changeInfo.addEditOp(op);

            }
//            System.out.println(script.exactMatch);
//            System.out.println(script.exactMatchCount);
        } catch (IOException e) {
            e.printStackTrace();
        }

        srcFile.delete();
        dstFile.delete();



        return changeInfo;
    }

}

