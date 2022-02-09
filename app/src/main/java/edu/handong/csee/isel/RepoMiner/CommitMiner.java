package edu.handong.csee.isel.RepoMiner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.IterableUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import edu.handong.csee.isel.Main.CommandLineExecutor;


public class CommitMiner {
	
	private List<RevCommit> commitList;
	private File file = null;
	private Git git;
	
	public CommitMiner(String path) throws IOException, GitAPIException{
		
		Pattern pattern = Pattern.compile("(git@|ssh|https://)github.com()(.*?)$");
		Matcher matcher = pattern.matcher(path);
		
		if (matcher.find()) {
//			file = new File("/Users/nayeawon/Desktop/" + matcher.group(3));
//			file = new File("/home/zackcglee/Documents/projects/ISEL/ASTChangeAnalyzer/clones/" + matcher.group(3));
			file = new File("/data/CGYW/clones/" + matcher.group(3) + "/.git");
			if (file.exists()) {
				git = Git.open(file);
			} else {
				git = Git.cloneRepository()
						.setURI(path)
						.setDirectory(file).call();
				System.out.println("\nRepository Cloning Completed\n");
			}
		} else {
			file = new File(path + "/.git");
			git = Git.open(file);
		}
		Iterable<RevCommit> walk = git.log().all().call();
		commitList = IterableUtils.toList(walk);
	}
	
	public List<RevCommit> getCommitList() {
		return commitList;
	}
	
	public Repository getRepo() {
		return git.getRepository();
	}
    
}


