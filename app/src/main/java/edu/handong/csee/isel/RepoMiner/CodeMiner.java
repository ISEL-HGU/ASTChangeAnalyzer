package edu.handong.csee.isel.RepoMiner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.TreeGenerators;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.matchers.optimal.rted.InfoTree;
import com.github.gumtreediff.tree.Tree;


import edu.handong.csee.isel.Main.Utils;


public class CodeMiner {
	
	private static String commitId;
	private static Repository repo;
	private static List<String> bicList = null;
	
	public void setRepo(Repository repo) {
		this.repo = repo;
	}
	
	public static void collect(List<RevCommit> commitList) {
		
		for (RevCommit commit : commitList) {
			
			if (commit.getParentCount() < 1) {
				System.err.println("WARNING: Parent commit does not exit: " + commit.name());
				continue;
			}
			
			RevCommit parent = commit.getParent(0);
			
			String [] commits = commit.getId().toString().split(" ");
			commitId = commits[1];
			
			List<DiffEntry> diffs = Utils.diff(parent, commit, repo);
			
			for (DiffEntry diff : diffs) {
				
				String key = null;
				StringBuffer contentBuffer = new StringBuffer();

				String oldPath = diff.getOldPath();
				String newPath = diff.getNewPath();

				if (newPath.indexOf("Test") >= 0 || !newPath.endsWith(".c"))
					continue;

				key = Utils.getKeyName(commit.getName(), newPath);
				

				String prevFileSource = Utils.fetchBlob(repo, commit.getId().getName() + "~1", oldPath);
				String fileSource = Utils.fetchBlob(repo, commit.getId().getName(), newPath);
				
				System.out.println(prevFileSource);

				EditScript actions = null;
				
				try {
					actions = getCharacteristicVector(prevFileSource, fileSource);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for (Action element : actions) {
					switch (element.getName()) {
					case "INS":
					case "DEL":
					case "UPD":
					case "MOV":

						String changedNode = element.getName() + String.valueOf(element.getNode().getType());//node

						contentBuffer.append(changedNode);
						contentBuffer.append(" ");

						break;

					default:
						continue;
					}
				}
				String content = contentBuffer.toString().trim();
				System.out.println(content);
//				if (isBuggy(commit,diff)) {
//
//					File changedVectorFile = new File(buggyDirectory + File.separator + key + ".txt");
//
//					try {
//						FileUtils.write(changedVectorFile, content, "UTF-8");
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//
//				} else {
//					File changedVectorFile = new File(cleanDirectory + File.separator + key + ".txt");
//
//					try {
//						FileUtils.write(changedVectorFile, content, "UTF-8");
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
			}

//			System.out.println(commitId);
//			gitIDExtrct(commitId, git);
		}
	}
	
	public static EditScript getCharacteristicVector(String srcFile, String dstFile) throws IOException {

		Run.initGenerators(); // registers the available parsers

		Tree src = TreeGenerators.getInstance().getTree(srcFile).getRoot(); // retrieves and applies the default parser for the file 
		Tree dst = TreeGenerators.getInstance().getTree(dstFile).getRoot(); // retrieves and applies the default parser for the file 

		Matcher defaultMatcher = Matchers.getInstance().getMatcher(); // retrieves the default matcher
		MappingStore mappings = defaultMatcher.match(src, dst); // computes the mappings between the trees
		
		EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
		EditScript actions = editScriptGenerator.computeActions(mappings); // computes the edit script
		
		return actions;
	}
	
	private static boolean isBuggy(RevCommit commit, DiffEntry diff) {

		for (String bic : bicList) {
			if (commit.getShortMessage().contains(bic)) {
				return true;
			}
			
			String key = commit.getId().getName() + "-" + diff.getNewPath().toString();
			if(key.contains(bic)) {
				return true;
			}
		}

		return false;
	}
}
