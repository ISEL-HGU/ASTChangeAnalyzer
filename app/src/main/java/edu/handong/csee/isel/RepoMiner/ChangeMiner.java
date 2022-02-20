package edu.handong.csee.isel.RepoMiner;

import java.util.List;

import edu.handong.csee.isel.ChangeAnalysis.ChangeAnalyzer;
import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import edu.handong.csee.isel.DiffTools.GumTree;
import edu.handong.csee.isel.DiffTools.LASTool;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import me.tongfei.progressbar.*;

public class ChangeMiner {

	private String projectName;
	private Repository repo;
	private String language;
	private String DiffTool;
	private boolean isAnalysis;
	private String fileExtension;
	private String filePath;
	private int totalCount;
	private String Java = ".java";
	private String Python = ".py";
	private String C = ".c";

	public String getProjectName() { return projectName; }

	public void setProperties(String filePath, Repository repo, String language, String DiffTool) {
		this.filePath = filePath;
		this.repo = repo;
		this.language = language;
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
	
	public void collect(List<RevCommit> commitList, ChangeAnalyzer changeAnalyzer) {
		try (ProgressBar pb = new ProgressBar("Change Mining", commitList.size())) {
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
						projectName = repo.getDirectory().getParent();
						ChangeInfo changeInfo = new ChangeInfo(getProjectName(), commit.name());
						switch (DiffTool) {
							case "LAS":
								LASTool las = new LASTool(filePath, srcFileSource, dstFileSource);
								changeInfo = las.constructChange(changeInfo);
								break;
							default:
								GumTree gumtree = new GumTree(filePath, fileExtension, srcFileSource, dstFileSource);
								changeInfo = gumtree.constructChange(changeInfo);
								break;
						}
						changeAnalyzer.generateMap(changeInfo, DiffTool);
						if (isAnalysis && changeAnalyzer.getTotalCount()==totalCount) {
							changeAnalyzer.printStatistic();
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


