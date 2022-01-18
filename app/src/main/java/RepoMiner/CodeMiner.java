package RepoMiner;

import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import Main.Utils;

public class CodeMiner {
	
	private static String commitId;
	private static Repository repo;
	
	public static void collect(Iterable<RevCommit> commitList) {
		
		for(RevCommit commit : commitList) {
			
			if (commit.getParentCount() < 1) {
				System.err.println("WARNING: Parent commit does not exit: " + commit.name());
				continue;
			}
			
			RevCommit parent = commit.getParent(0);
			
			System.out.println(parent);
			String [] commits = commit.getId().toString().split(" ");
			commitId = commits[1];
			
			List<DiffEntry> diffs = Utils.diff(parent, commit, repo);

//			System.out.println(commitId);
			//gitIDExtrct(commitId, git);
		}
	}
}
