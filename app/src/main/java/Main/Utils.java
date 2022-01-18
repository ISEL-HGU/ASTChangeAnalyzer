package Main;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class Utils {
	
	static public DiffAlgorithm diffAlgorithm = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS);
	static public RawTextComparator diffComparator = RawTextComparator.WS_IGNORE_ALL;
	
	public static List<DiffEntry> diff(RevCommit parent, RevCommit commit, Repository repo) {

		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setRepository(repo);
		df.setDiffAlgorithm(diffAlgorithm);
		df.setDiffComparator(diffComparator);
		df.setDetectRenames(true);
		List<DiffEntry> diffs = null;
		try {
			diffs = df.scan(parent.getTree(), commit.getTree());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return diffs;
	}
	
	public static String getKeyName(String commitName, String newPath) {

		if (newPath.contains("\\")) {
			newPath = newPath.replace("\\", "-");
		}
		if (newPath.contains("/")) {
			newPath = newPath.replace("/", "-");
		}

		return commitName + "-" + newPath;
	}
	
	public static String fetchBlob(Repository repo, String revSpec, String path) {

		try {
			// Resolve the revision specification
			final ObjectId id = repo.resolve(revSpec);

			// Makes it simpler to release the allocated resources in one go
			ObjectReader reader = repo.newObjectReader();

			// Get the commit object for that revision
			RevWalk walk = new RevWalk(reader);
			RevCommit commit = walk.parseCommit(id);
			walk.close();

			// Get the revision's file tree
			RevTree tree = commit.getTree();
			// .. and narrow it down to the single file's path
			TreeWalk treewalk = TreeWalk.forPath(reader, path, tree);
			if (treewalk != null) {
				// use the blob id to read the file's data
				byte[] data = reader.open(treewalk.getObjectId(0)).getBytes();
				reader.close();
				return new String(data, "utf-8");
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
