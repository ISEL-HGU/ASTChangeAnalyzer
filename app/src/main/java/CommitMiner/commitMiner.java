package CommitMiner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;


public class commitMiner {
	
	private Iterable<RevCommit> log;
	
	commitMiner(String url){
		
		
		Git git = Git.cloneRepository()
				.setURI(url)
				.setDirectory("/path/repo").call();
		log = git.log().call();
		for(RevCommit a : log)
		{
			System.out.println(log);
		}
	}
}
