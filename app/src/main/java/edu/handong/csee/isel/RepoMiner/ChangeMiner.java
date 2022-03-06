package edu.handong.csee.isel.RepoMiner;

import java.util.List;

import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import edu.handong.csee.isel.ChangeAnalysis.ChangeData;
import edu.handong.csee.isel.DiffTools.GumTree;
import edu.handong.csee.isel.DiffTools.LASTool;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import me.tongfei.progressbar.*;

public class ChangeMiner {
	private Repository repo;
	private String DiffTool;
	private String fileExtension;
	private String filePath;
	private String Java = ".java";
	private String Python = ".py";
	private String C = ".c";


	public void setProperties(String filePath, Repository repo, String language, String DiffTool) {
		this.filePath = filePath;
		this.repo = repo;
		this.DiffTool = DiffTool;
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
	}
	
	public void collect(String projectName, List<RevCommit> commitList, ChangeInfo changeInfo) {
		try (ProgressBar pb = new ProgressBar(projectName, commitList.size())) {
			for (RevCommit commit : commitList) {
				pb.step();
				if (commit.getParentCount() < 1) {
					continue;
				}
				RevCommit parent = commit.getParent(0);
				List<DiffEntry> diffs = RepoUtils.diff(parent, commit, repo);
				for (DiffEntry diff : diffs) {
					try {
						String oldPath = diff.getOldPath();
						String newPath = diff.getNewPath();
						if (newPath.indexOf("Test") >= 0 || !newPath.endsWith(fileExtension))
							continue;
						String srcFileSource = RepoUtils.fetchBlob(repo, commit.getId().getName() + "~1", oldPath);
						String dstFileSource = RepoUtils.fetchBlob(repo, commit.getId().getName(), newPath);
						ChangeData changeData = new ChangeData();
						switch (DiffTool) {
							case "LAS":
								LASTool las = new LASTool(filePath, srcFileSource, dstFileSource);
								changeData = las.constructChange(changeData);
								break;
							default:
								GumTree gumtree = new GumTree(filePath, fileExtension, srcFileSource, dstFileSource);
								changeData = gumtree.constructChange(changeData);
						}
						changeInfo.generateMap(changeData, DiffTool, commit.getId().getName(), newPath);
						if (changeInfo.getTotalCount() > 0 && changeInfo.getTotalCount()%200000==0) {
							changeInfo.printStatistic();
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		}
    }

	public int collect(List<RevCommit> commitList) {
		int count = 0;
		try (ProgressBar pb = new ProgressBar("Change Mining", commitList.size())) {
			for (RevCommit commit : commitList) {
				pb.step();
				if (commit.getParentCount() < 1) {
					continue;
				}
				RevCommit parent = commit.getParent(0);
				List<DiffEntry> diffs = RepoUtils.diff(parent, commit, repo);
				for (DiffEntry diff : diffs) {
					String newPath = diff.getNewPath();
					if (newPath.indexOf("Test") >= 0 || !newPath.endsWith(fileExtension))
						continue;
					count++;
				}
			}
		}
		return count;
	}
}


