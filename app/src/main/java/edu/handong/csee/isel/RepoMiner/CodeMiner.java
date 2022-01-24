package edu.handong.csee.isel.RepoMiner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;


import edu.handong.csee.isel.Main.Utils;


public class CodeMiner {
	
	private String commitId;
	private Repository repo;
	private List<String> bicList = null;
	private String referencePath;
	private String projectName;
	private String language;
	private String Java = ".java";
	private String Python = ".py";
	
	public void setRepo(Repository repo) {
		this.repo = repo;
	}
	
	public void setLang(String language) {
		this.language = language;
	}
	
	public void collect(List<RevCommit> commitList) {
		
		String fileExtension = null;
		
		switch(language.toUpperCase()) {
		case "JAVA":
			fileExtension = Java;
			break;
		case "PYTHON":
			fileExtension = Python;
			break;
		}
		
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

				if (newPath.indexOf("Test") >= 0 || !newPath.endsWith(fileExtension))
					continue;

				key = Utils.getKeyName(commit.getName(), newPath);
				

				String prevFileSource = Utils.fetchBlob(repo, commit.getId().getName() + "~1", oldPath);
				String fileSource = Utils.fetchBlob(repo, commit.getId().getName(), newPath);

				EditScript actions = null;
				List<Action> actionList = null;
				
				try {
					actions = getCharacteristicVector(prevFileSource, fileSource);
					actionList = actions.asList();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for (Action action : actionList) {
					System.out.println(action);
				}
			}
		}
	}
	
	public EditScript getCharacteristicVector(String prevFileSource, String fileSource) throws IOException {

		Run.initGenerators(); // registers the available parsers
		
		File srcFile = new File("src.java");
		File dstFile = new File("dst.java");
		
		try {
			BufferedWriter srcWriter = new BufferedWriter(new FileWriter(srcFile));
			srcWriter.write(prevFileSource);
			srcWriter.close();
			BufferedWriter dstWriter = new BufferedWriter(new FileWriter(dstFile));
			dstWriter.write(fileSource);
			dstWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Tree src = TreeGenerators.getInstance().getTree(srcFile.getPath()).getRoot(); // retrieves and applies the default parser for the file 
		Tree dst = TreeGenerators.getInstance().getTree(dstFile.getPath()).getRoot(); // retrieves and applies the default parser for the file 
		
		
		Matcher defaultMatcher = Matchers.getInstance().getMatcher(); // retrieves the default matcher
		MappingStore mappings = defaultMatcher.match(src, dst); // computes the mappings between the trees
		
		EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
		EditScript actions = editScriptGenerator.computeActions(mappings); // computes the edit script
		
		srcFile.delete();
		dstFile.delete();
		
		return actions;
	}
	
}


