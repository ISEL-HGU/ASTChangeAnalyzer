package edu.handong.csee.isel.DiffTools;

import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.SyntaxException;
import com.github.gumtreediff.gen.TreeGenerators;
import com.github.gumtreediff.gen.c.CTreeGenerator;
import com.github.gumtreediff.gen.python.PythonTreeGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GumTree {
    private String filePath;
    private String fileExtension;
    private String srcFileSource;
    private String dstFileSource;
    private Tree src;
    private Tree dst;


    public GumTree (String filePath, String fileExtension, String srcFileSource, String dstFileSource) {
        this.filePath = filePath;
        this.fileExtension = fileExtension;
        this.srcFileSource = srcFileSource;
        this.dstFileSource = dstFileSource;
    }

    public ChangeInfo constructChange(ChangeInfo changeInfo) {

        EditScript editscript = null;
        List<Action> actionList = null;

        Run.initGenerators(); // registers the available parsers

        try {
            if (fileExtension.equals(".c")) {
                editscript = CASTDiffMine(srcFileSource, dstFileSource);
            } else if (fileExtension.equals(".py")) {
                editscript = PythonASTDiffMine(srcFileSource, dstFileSource);
            } else {
                editscript = JavaASTDiffMine(srcFileSource, dstFileSource);
            }

            if (editscript != null)
                actionList = editscript.asList();

        } catch (SyntaxException e) {
            File srcFile = new File(filePath + "/src" + fileExtension);
            File dstFile = new File(filePath + "/dst" + fileExtension);
            srcFile.delete();
            dstFile.delete();
            return changeInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Action action : actionList) {
            changeInfo.addAction(action);
        }
        return changeInfo;
    }

    public EditScript JavaASTDiffMine(String srcFileSource, String dstFileSource) throws IOException {
        Run.initGenerators();
        File srcFile = new File("src.java");
        File dstFile = new File("dst.java");
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
        src = TreeGenerators.getInstance().getTree(srcFile.getPath()).getRoot(); // retrieves and applies the default parser for the file
        dst = TreeGenerators.getInstance().getTree(dstFile.getPath()).getRoot(); // retrieves and applies the default parser for the file

        MappingStore mappings = new CompositeMatchers.SimpleGumtree().match(src, dst); // computes the mappings between the trees

        EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
        EditScript actions = editScriptGenerator.computeActions(mappings); // computes the edit script

        srcFile.delete();
        dstFile.delete();

        return actions;
    }

    public EditScript PythonASTDiffMine(String srcFileSource, String dstFileSource) throws IOException, SyntaxException {
        Run.initGenerators(); // registers the available parsers
        TreeContext srcTC = new PythonTreeGenerator().generateFrom().string(srcFileSource);
        TreeContext dstTC = new PythonTreeGenerator().generateFrom().string(dstFileSource);
        EditScript actions = null;
        if (srcTC!= null && dstTC!=null) {
            src = srcTC.getRoot();
            dst = dstTC.getRoot();
            MappingStore mappings = new CompositeMatchers.SimpleGumtree().match(src, dst); // computes the mappings between the trees
            EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
            actions = editScriptGenerator.computeActions(mappings); // computes the edit script
        }
        return actions;
    }

    public EditScript CASTDiffMine(String srcFileSource, String dstFileSource) throws IOException, SyntaxException {
        Run.initGenerators(); // registers the available parsers
        TreeContext srcTC = new CTreeGenerator().generateFrom().string(srcFileSource);
        TreeContext dstTC = new CTreeGenerator().generateFrom().string(dstFileSource);
        EditScript actions = null;
        if (srcTC!= null && dstTC!=null) {
            src = srcTC.getRoot();
            dst = dstTC.getRoot();
            MappingStore mappings = new CompositeMatchers.SimpleGumtree().match(src, dst); // computes the mappings between the trees
            EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
            actions = editScriptGenerator.computeActions(mappings); // computes the edit script
        }
        return actions;
    }
}
