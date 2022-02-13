package edu.handong.csee.isel.RepoMiner;

import java.util.List;

import edu.handong.csee.isel.ChangeAnalysis.ChangeAnalyzer;
import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import com.github.gumtreediff.tree.Tree;
import edu.handong.csee.isel.DiffTools.GumTree;
import edu.handong.csee.isel.DiffTools.LASTool;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import me.tongfei.progressbar.*;

public class ChangeMiner {
	
	private Repository repo;
	private String language;
	private String DiffTool = "GUMTREE";
	private boolean isAnalysis;
	private String fileExtension;
	private int volumePortion;
	private int volumeCount;
	private String Java = ".java";
	private String Python = ".py";
	private String C = ".c";
	private Tree src;
	private Tree dst;

	public void setProperties(Repository repo, String language, String DiffTool, boolean isAnalysis, int volume) {
		this.repo = repo;
		this.language = language;
		this.DiffTool = DiffTool;
		this.isAnalysis = isAnalysis;
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
		volumePortion = volume/20;
		volumeCount = volumePortion;
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
					String oldPath = diff.getOldPath();
					String newPath = diff.getNewPath();
					if (newPath.indexOf("Test") >= 0 || !newPath.endsWith(fileExtension))
						continue;
					String srcFileSource = RepoUtils.fetchBlob(repo, commit.getId().getName() + "~1", oldPath);
					String dstFileSource = RepoUtils.fetchBlob(repo, commit.getId().getName(), newPath);
					ChangeInfo changeInfo = new ChangeInfo(repo.getDirectory().getParent(), commit.name());
					switch (DiffTool) {
						case "LAS":
							LASTool las = new LASTool(fileExtension, srcFileSource, dstFileSource);
							changeInfo = las.constructChange(changeInfo);
							break;
						default:
							GumTree gumtree = new GumTree(fileExtension, srcFileSource, dstFileSource);
							changeInfo = gumtree.constructChange(changeInfo);
							break;
					}
					changeAnalyzer.generateMap(changeInfo, language);
					if (isAnalysis && changeAnalyzer.getTotalCount()==volumeCount) {
						changeAnalyzer.printStatistic();
						volumeCount += volumePortion;
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


