package RepoMiner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;




public class CommitMiner {
	
	private Iterable<RevCommit> log;
	private File file = null;
	private Git git;
	private String commitId;
	
	public CommitMiner(String path,boolean isLocalPath) throws IOException, InvalidRemoteException, TransportException, GitAPIException{
		
		if(isLocalPath) {
			file = new File(path + "/.git");
			git = Git.open(file);
			
		}
		
		else {
			Pattern pattern = Pattern.compile("(git@|ssh|https://)github.com()(.*?)$");
			Matcher matcher = pattern.matcher(path);
			if (matcher.find()) {
				//file = new File("/Users/nayeawon/Desktop/" + matcher.group(3));
				file = new File("C:\\Users\\Zack CG Lee\\OneDrive\\Desktop\\" + matcher.group(3));
			}
			git = Git.cloneRepository()
					.setURI(path)
					.setDirectory(file).call();	
		}
		
		log = git.log().call();
		for(RevCommit a : log) {
			String [] commits = a.getId().toString().split(" ");
			commitId = commits[1];
			System.out.println(a);
			//gitIDExtrct(commitId, git);
		}
		
	}
	
	public File getRepoPath() {
    	return file;
    }
	
	public String getCommitId(){
		return this.commitId;
	}
	
	public void gitIDExtrct(String Id, Git git) throws IOException {
        
            // the diff works on TreeIterators, we prepare two for the two branches
            AbstractTreeIterator oldTreeParser = prepareTreeParser(git.getRepository(), Id);
            //AbstractTreeIterator newTreeParser = prepareTreeParser(repo, "9e0719d7d773b41b49ebf04e6fd7b5c637e96063");

            // then the porcelain diff-command returns a list of diff entries
                List<DiffEntry> diff = null;
				try {
					diff = git.diff().
					        setOldTree(oldTreeParser).
					        //setNewTree(newTreeParser).
					        //setPathFilter(PathFilter.create("README.md")).
					        // to filter on Suffix use the following instead
					        //setPathFilter(PathSuffixFilter.create(".java")).
					        call();
				} catch (GitAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                for (DiffEntry entry : diff) {
                    System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
                    try (DiffFormatter formatter = new DiffFormatter(System.out)) {
                        formatter.setRepository(git.getRepository());
                        formatter.format(entry);
                    }
                }
        
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }
}


