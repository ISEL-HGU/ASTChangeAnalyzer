package CommitMiner;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;


public class CommitMine {
	
	private Iterable<RevCommit> log;
	
	public CommitMine(String url){
		
		Pattern pattern = Pattern.compile("(git@|ssh|https://)github.com()(.*?)$");
		Matcher matcher = pattern.matcher(url);
		File file=null;
		if (matcher.find()) {
			file = new File("/Users/nayeawon/Desktop/" + matcher.group(3));
		}
		
//		File file = new File("C:\\Users\\Zack CG Lee\\OneDrive\\Desktop\\log");
		Git git;
		try {
			git = Git.cloneRepository()
					.setURI(url)
					.setDirectory(file).call();
			log = git.log().call();
			for(RevCommit a : log) {
				System.out.println(a.getFullMessage());
			}
		} catch (InvalidRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
