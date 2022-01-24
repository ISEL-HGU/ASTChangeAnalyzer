package edu.handong.csee.isel.RepoMiner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.TreeGenerators;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;

public class ASTExtracter {
	
	public void ASTExtract(String fileSource, String fileExtension) throws IOException {
		
		Run.initGenerators(); // registers the available parsers
		
		File file = new File("file" + fileExtension);
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(fileSource);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Tree tree = TreeGenerators.getInstance().getTree(file.getPath()).getRoot(); // retrieves and applies the default parser for the file
		
		System.out.println(tree.toTreeString());
		
	}
	
	public EditScript ASTDiffMine(String srcFileSource, String dstFileSource, String fileExtension) throws IOException {

		Run.initGenerators(); // registers the available parsers
		
		File srcFile = new File("src" + fileExtension);
		File dstFile = new File("dst" + fileExtension);
		
		try {
			BufferedWriter srcWriter = new BufferedWriter(new FileWriter(srcFile));
			srcWriter.write(srcFileSource);
			srcWriter.close();
			BufferedWriter dstWriter = new BufferedWriter(new FileWriter(dstFile));
			dstWriter.write(dstFileSource);
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
