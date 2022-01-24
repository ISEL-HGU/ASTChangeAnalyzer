package edu.handong.csee.isel.RepoMiner;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.EditScript;

import edu.handong.csee.isel.Main.Utils;

public class CodeMiner {
	
	private Repository repo;
	private String language;
	private String fileExtension;
	private String Java = ".java";
	private String Python = ".py";
	
	public void setRepo(Repository repo) {
		this.repo = repo;
	}
	
	public void setLang(String language) {
		this.language = language;
	}
	
	public String getFileExtension() {
		return fileExtension;
	}
	
	public void collect(List<RevCommit> commitList) {
	
		switch(language.toUpperCase()) {
		case "JAVA":
			fileExtension = Java;
			break;
		case "PYTHON":
			fileExtension = Python;
			break;
		}
		
		for (RevCommit commit : commitList) {

			if (commit.getParentCount() < 1) {
				System.err.println("WARNING: Parent commit does not exit: " + commit.name());
				continue;
			}
			
			RevCommit parent = commit.getParent(0);
			
			List<DiffEntry> diffs = Utils.diff(parent, commit, repo);
			
			for (DiffEntry diff : diffs) {

				String oldPath = diff.getOldPath();
				String newPath = diff.getNewPath();

				if (newPath.indexOf("Test") >= 0 || !newPath.endsWith(fileExtension))
					continue;

				String srcFileSource = Utils.fetchBlob(repo, commit.getId().getName() + "~1", oldPath);
				String dstFileSource = Utils.fetchBlob(repo, commit.getId().getName(), newPath);

				EditScript editscript = null;
				List<Action> actionList = null;
				
				ASTExtracter ASTExtract = new ASTExtracter();
				
				try {
					editscript = ASTExtract.ASTDiffMine(srcFileSource, dstFileSource, fileExtension);
					actionList = editscript.asList();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				for (Action action : actionList) {
					// prints diff result
					System.out.println(action);
				}
			}
		}
	}
	
}


