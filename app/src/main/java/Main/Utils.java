package Main;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
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
}
