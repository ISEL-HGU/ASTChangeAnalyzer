package edu.handong.csee.isel.RepoMiner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.IterableUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;


public class CommitMiner {
	
	private List<RevCommit> commitList;
	private File file = null;
	private Git git;
	private boolean erase = false;
	
	public CommitMiner(String path) throws IOException, InvalidRemoteException, TransportException, GitAPIException{
		
		Pattern pattern = Pattern.compile("(git@|ssh|https://)github.com()(.*?)$");
		Matcher matcher = pattern.matcher(path);
		String desktopPath = "";
		
		if (matcher.find()) {
			try{
				desktopPath = System.getProperty("user.home") + "/Desktop";
				if (desktopPath.contains("\\")) {
					desktopPath = desktopPath.replace("/", "\\");
				}
			} catch (Exception e){
				e.printStackTrace();
			}
			
			file = new File(desktopPath + "/" + matcher.group(3));
			git = Git.cloneRepository()
					.setURI(path)
					.setDirectory(file).call();	
			erase = true;
		}
		
		else {
			
			file = new File(path + "/.git");
			git = Git.open(file);
		}
		
		Iterable<RevCommit> walk = git.log().call();
		commitList = IterableUtils.toList(walk);
		
	}
	
	public File getRepoPath() {
    	return file;
    }
	
	public List<RevCommit> getCommitList() {
		return commitList;
	}
	
	public Repository getRepo() {
		return git.getRepository();
	}
	
	public boolean getErase() {
		return erase;
	}
    
}


