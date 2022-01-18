package RepoMiner;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;

import Main.Utils;

public class CodeMiner {
	
	private static String commitId;
	private static Repository repo;
	
	public void setRepo(Repository repo) {
		this.repo = repo;
	}
	
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
			
			for (DiffEntry diff : diffs) {
				String key = null;
				StringBuffer contentBuffer = new StringBuffer();

				String oldPath = diff.getOldPath();
				String newPath = diff.getNewPath();

				if (newPath.indexOf("Test") >= 0 || !newPath.endsWith(".java"))
					continue;

				key = Utils.getKeyName(commit.getName(), newPath);

				String prevFileSource = Utils.fetchBlob(repo, commit.getId().getName() + "~1", oldPath);
				String fileSource = Utils.fetchBlob(repo, commit.getId().getName(), newPath);

				List<Action> vector = null;

//				try {
//					vector = getCharacteristicVector(prevFileSource, fileSource);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}

//			System.out.println(commitId);
			//gitIDExtrct(commitId, git);
		}
	}
	
//	public List<Action> getCharacteristicVector(String prevFileSource, String fileSource) throws IOException {
//
//		Run.initGenerators();
//
//		InfoTree src = new JdtTreeGenerator().generateFromString(prevFileSource).getRoot();
//		ITree dst = new JdtTreeGenerator().generateFromString(fileSource).getRoot();
//
//		Matcher m = Matchers.getInstance().getMatcher(src, dst); // retrieve the default matcher
//		m.match();
//
//		ActionGenerator g = new ActionGenerator(src, dst, m.getMappings());
//		g.generate();
//
//		List<Action> actions = g.getActions();
//		return actions;
//	}
}
