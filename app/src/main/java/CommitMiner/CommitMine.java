package CommitMiner;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;


public class CommitMine {
	
	private Iterable<RevCommit> log;
	
	public CommitMine(String url){
		
		File file = new File("/Users/nayeawon/Desktop/");
		file.exists();
		Git git;
		try {
			git = Git.cloneRepository()
					.setURI(url)
					.setDirectory(file).call();
			log = git.log().call();
			for(RevCommit a : log) {
				System.out.println();
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
