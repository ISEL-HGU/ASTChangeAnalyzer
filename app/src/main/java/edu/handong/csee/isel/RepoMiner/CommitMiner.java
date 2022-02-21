package edu.handong.csee.isel.RepoMiner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.IterableUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;


public class CommitMiner {
	
	private List<RevCommit> commitList;
	private File file = null;
	private Git git;
	private boolean completed = false;
	private String filePath;
	private String matcherGroup;
	
	public CommitMiner(String path, boolean isGitClone) throws IOException, GitAPIException{
		Pattern pattern = Pattern.compile("(git@|ssh|https://)github.com/()(.*?)$");
		Matcher matcher = pattern.matcher(path);

		if (matcher.find()) {
//			filePath = "/Users/nayeawon/Desktop/" +  matcher.group(3);
			filePath = "/home/zackcglee/Documents/projects/ISEL/ASTChangeAnalyzer/clones/" +  matcher.group(3);
//			filePath = "/data/CGYW/clones/" +  matcher.group(3);
			matcherGroup = matcher.group(3);

			file = new File(filePath + "/.git");
			if (file.exists()) {
				git = Git.open(file);
			} else {
				try {
					git = Git.cloneRepository()
							.setURI(path)
							.setDirectory(file).call();
				} catch (TransportException e) {
					System.out.println("no CredentialsProvider(Authentication Problem)\n");
					return;
				}
			}
		} else {
			git = Git.open(new File(path + "/.git"));
		}
		if (isGitClone) {
			return;
		}
		try {
			Iterable<RevCommit> walk = git.log().all().call();
			commitList = IterableUtils.toList(walk);
			completed = true;
		} catch (NoHeadException e) { System.out.println("Empty repo\n"); }
		return;
	}
	
	public List<RevCommit> getCommitList() {
		return commitList;
	}
	
	public Repository getRepo() {
		return git.getRepository();
	}

	public boolean isCompleted() { return completed; }

	public String getFilePath() { return filePath; }

	public String getMatcherGroup() {return matcherGroup; }
    
}


