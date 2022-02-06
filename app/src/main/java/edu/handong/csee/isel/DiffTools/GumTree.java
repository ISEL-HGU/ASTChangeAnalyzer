package edu.handong.csee.isel.DiffTools;

import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.gen.SyntaxException;
import com.github.gumtreediff.tree.Tree;
import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import edu.handong.csee.isel.RepoMiner.ASTExtractor;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GumTree {
    private boolean level;
    private String fileExtension;
    private String srcFileSource;
    private String dstFileSource;
    private Tree src;
    private Tree dst;


    public GumTree (boolean level, String fileExtension, String srcFileSource, String dstFileSource) {
        this.level = level;
        this.fileExtension = fileExtension;
        this.srcFileSource = srcFileSource;
        this.dstFileSource = dstFileSource;
    }

    public ChangeInfo constructChange(ChangeInfo changeInfo) {

        EditScript editscript = null;
        List<Action> actionList = null;

        ASTExtractor ASTExtract = new ASTExtractor();

        try {
            if (fileExtension.equals(".c")) {
                editscript = ASTExtract.CASTDiffMine(srcFileSource, dstFileSource);
                src = ASTExtract.CASTExtract(srcFileSource);
                dst = ASTExtract.CASTExtract(dstFileSource);
            } else if (fileExtension.equals(".py")) {
                editscript = ASTExtract.PythonASTDiffMine(srcFileSource, dstFileSource);
                src = ASTExtract.PythonASTExtract(srcFileSource);
                dst = ASTExtract.PythonASTExtract(dstFileSource);
            } else {
                editscript = ASTExtract.JavaASTDiffMine(srcFileSource, dstFileSource);
                src = ASTExtract.JavaASTExtract(srcFileSource);
                dst = ASTExtract.JavaASTExtract(dstFileSource);
            }

            if (editscript != null)
                actionList = editscript.asList();

        } catch (SyntaxException e) {
            System.err.print("\nThis change has a syntatic error: ");
            e.printStackTrace();
            File srcFile = new File("src" + fileExtension);
            File dstFile = new File("dst" + fileExtension);
            srcFile.delete();
            dstFile.delete();
            return changeInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Action action : actionList) {
            if (level) { changeInfo.addHunk(action); }
        }
        return changeInfo;
    }
}
