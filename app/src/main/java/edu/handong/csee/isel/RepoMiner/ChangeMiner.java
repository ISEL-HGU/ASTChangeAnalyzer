package edu.handong.csee.isel.RepoMiner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.github.gumtreediff.tree.Tree;
import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import edu.handong.csee.isel.Main.CommandLineExecutor;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.gen.SyntaxException;
import com.github.gumtreediff.actions.EditScript;

public class ChangeMiner {
	
	private Repository repo;
	private String language;
	private boolean level;
	private String fileExtension;
	private int commitCount = 0;
	private int diffCount;
	private int actionCount;
	private String Java = ".java";
	private String Python = ".py";
	private String C = ".c";
	private Tree src;
	private Tree dst;

	
	public void setRepo(Repository repo) {
		this.repo = repo;
	}
	
	public void setLang(String language) {
		this.language = language;
	}

	public void setLevel(boolean level) { this.level = level; }

	public String getFileExtension() {
		return fileExtension;
	}
	
	public ArrayList<ChangeInfo> collect(List<RevCommit> commitList) {

		ArrayList<ChangeInfo> changeInfoList = new ArrayList<ChangeInfo>();
	
		switch(language.toUpperCase()) {
			case "PYTHON":
				fileExtension = Python;
				break;
			case "C":
				fileExtension = C;
				break;
			default:
				fileExtension = Java;
		}

		System.out.println("\nGit Change Mining Started");

		for (RevCommit commit : commitList) {

			if (commit.getParentCount() < 1) {
				System.err.println("WARNING: Parent commit does not exit: " + commit.name());
				continue;
			}
			commitCount++;
			System.out.println(" L change mined commitID: " + commit.name());

			RevCommit parent = commit.getParent(0);
			
			List<DiffEntry> diffs = RepoUtils.diff(parent, commit, repo);
			diffCount = 0;

			ChangeInfo changeInfo = null;

			for (DiffEntry diff : diffs) {

				String oldPath = diff.getOldPath();
				String newPath = diff.getNewPath();

				if (newPath.indexOf("Test") >= 0 || !newPath.endsWith(fileExtension))
					continue;

				diffCount++;
				String srcFileSource = RepoUtils.fetchBlob(repo, commit.getId().getName() + "~1", oldPath).replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
				String dstFileSource = RepoUtils.fetchBlob(repo, commit.getId().getName(), newPath).replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*\n)","");

				changeInfo = new ChangeInfo(oldPath, newPath, commit.name());

				EditScript editscript = null;
				List<Action> actionList = null;
				
				ASTExtractor ASTExtract = new ASTExtractor();
				
				try {
					if (fileExtension.equals(C)) {
						editscript = ASTExtract.CASTDiffMine(srcFileSource, dstFileSource);
						src = ASTExtract.CASTExtract(srcFileSource);
						dst = ASTExtract.CASTExtract(dstFileSource);
					}
					else if (fileExtension.equals(Python)) {
						editscript = ASTExtract.PythonASTDiffMine(srcFileSource, dstFileSource);
						src = ASTExtract.PythonASTExtract(srcFileSource);
						dst = ASTExtract.PythonASTExtract(dstFileSource);
					}
					else if (fileExtension.equals(Java)){
						editscript = ASTExtract.JavaASTDiffMine(srcFileSource, dstFileSource);
						src = ASTExtract.JavaASTExtract(srcFileSource);
						dst = ASTExtract.JavaASTExtract(dstFileSource);
					}
					else continue;
					
					if (editscript!=null)
						actionList = editscript.asList();
					
				} catch (SyntaxException e) {
					System.err.print("\nThis change has a syntatic error: "); e.printStackTrace();
					File srcFile = new File("src" + fileExtension);
					File dstFile = new File("dst" + fileExtension);
					srcFile.delete();
					dstFile.delete();
					continue;
				} catch (IOException e) {
					e.printStackTrace();
				}
				actionCount = 0;
				for (Action action : actionList) {
					actionCount++;
					if (level) {
						changeInfo.addHunk(action);
						continue;
					}
					System.out.println("\n" + commitCount + "-" + actionCount
							+ "\n L diff: " + diff.toString()
							+ "\n L action name: " + action.getName()
							+ "\n L action type: " + action.getNode().getType()
							+ "\n L action Position info: " + action.getNode().getPos() + "-" + action.getNode().getEndPos());
					System.out.println("\nsrc: " + src.getTreesBetweenPositions(action.getNode().getPos(), action.getNode().getEndPos())
							+ "\n L hash: " + src.getTreesBetweenPositions(action.getNode().getPos(), action.getNode().getEndPos()).hashCode());
					System.out.println("\ndst: " + dst.getTreesBetweenPositions(action.getNode().getPos(), action.getNode().getEndPos())
							+ "\n L hash: " + dst.getTreesBetweenPositions(action.getNode().getPos(), action.getNode().getEndPos()).hashCode());
				}
				changeInfoList.add(changeInfo);
			}
		}
		if (changeInfoList.size()!=0)
			System.out.println("\nChange Mining Completed\n");
		else
			System.out.println("\nChange Mining Failed\n");
        return changeInfoList;
    }
	
}


