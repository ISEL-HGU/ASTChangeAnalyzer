package edu.handong.csee.isel.RepoMiner;

import java.util.ArrayList;
import java.util.List;

import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import com.github.gumtreediff.tree.Tree;
import edu.handong.csee.isel.DiffTools.GumTree;
import edu.handong.csee.isel.DiffTools.LAS;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class ChangeMiner {
	
	private Repository repo;
	private String language;
	private boolean level;
	private String DiffTool;
	private String fileExtension;
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

	public void setDiffTool(String DiffTool) { this.DiffTool = DiffTool; }
	
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

		System.out.println("Git Change Mining Started");

		for (RevCommit commit : commitList) {

			if (commit.getParentCount() < 1) {
				System.err.println("WARNING: Parent commit does not exit: " + commit.name());
				continue;
			}

			RevCommit parent = commit.getParent(0);
			
			List<DiffEntry> diffs = RepoUtils.diff(parent, commit, repo);

			ChangeInfo changeInfo = null;

			for (DiffEntry diff : diffs) {

				String oldPath = diff.getOldPath();
				String newPath = diff.getNewPath();

				if (newPath.indexOf("Test") >= 0 || !newPath.endsWith(fileExtension))
					continue;

				String srcFileSource = RepoUtils.fetchBlob(repo, commit.getId().getName() + "~1", oldPath).replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
				String dstFileSource = RepoUtils.fetchBlob(repo, commit.getId().getName(), newPath).replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*\n)","");

				changeInfo = new ChangeInfo(oldPath, newPath, repo.getDirectory().getParent(), commit.name());

				switch (DiffTool) {
					case "LAS":
						LAS las = new LAS(level, fileExtension, srcFileSource, dstFileSource);
						changeInfo = las.constructChange(changeInfo);
						break;
					default:
						GumTree gumtree = new GumTree(level, fileExtension, srcFileSource, dstFileSource);
						changeInfo = gumtree.constructChange(changeInfo);
						break;
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


