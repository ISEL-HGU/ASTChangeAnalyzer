package edu.handong.csee.isel.RepoMiner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.SyntaxException;
import com.github.gumtreediff.gen.TreeGenerators;
import com.github.gumtreediff.gen.python.PythonTreeGenerator;
import com.github.gumtreediff.gen.c.*;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;


public class ASTExtractor {
	
	public Tree JavaASTExtract(String fileSource) throws IOException {
		Run.initGenerators(); // registers the available parsers
		File file = new File("file.java");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(fileSource);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Tree tree = TreeGenerators.getInstance().getTree(file.getPath()).getRoot();
		file.delete();
		return tree;
	}
	
	
	public EditScript JavaASTDiffMine(String srcFileSource, String dstFileSource) throws IOException {
		Run.initGenerators();
		File srcFile = new File("src.java");
		File dstFile = new File("dst.java");
		/* checking
		File srcTree = new File("srcTree_" + srcFileSource.length() + ".txt");
		File dstTree = new File("dstTree_" + dstFileSource.length() + ".txt");
		*/
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
		/*
		try {
			BufferedWriter srcWriter = new BufferedWriter(new FileWriter(srcTree));
			srcWriter.write(src.toTreeString());
			srcWriter.close();
			BufferedWriter dstWriter = new BufferedWriter(new FileWriter(dstTree));
			dstWriter = new BufferedWriter(new FileWriter(dstTree));
			dstWriter.write(dst.toTreeString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		Matcher defaultMatcher = Matchers.getInstance().getMatcher(); // retrieves the default matcher
		MappingStore mappings = defaultMatcher.match(src, dst); // computes the mappings between the trees
		/*
		try {
			BufferedWriter srcWriter = new BufferedWriter(new FileWriter("mapping" + mappings.toString().length() + ".txt"));
			srcWriter.write(mappings.toString());
			srcWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 */

		EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
		EditScript actions = editScriptGenerator.computeActions(mappings); // computes the edit script

		srcFile.delete();
		dstFile.delete();
		
		return actions;
	}
	
	
	public Tree PythonASTExtract(String fileSource) throws IOException {
		TreeContext fileTC = new PythonTreeGenerator().generateFrom().string(fileSource);
		Tree src = null;
		if (fileTC!=null) {
			src = fileTC.getRoot();
		}
		return src;
	}
	
	
	public EditScript PythonASTDiffMine(String srcFileSource, String dstFileSource) throws IOException, SyntaxException {
		Run.initGenerators(); // registers the available parsers
		TreeContext srcTC = new PythonTreeGenerator().generateFrom().string(srcFileSource);
		TreeContext dstTC = new PythonTreeGenerator().generateFrom().string(dstFileSource);
		EditScript actions = null;
		if (srcTC!= null && dstTC!=null) {
			Tree src = srcTC.getRoot();
			Tree dst = dstTC.getRoot();

			Matcher defaultMatcher = Matchers.getInstance().getMatcher(); // retrieves the default matcher
			MappingStore mappings = defaultMatcher.match(src, dst); // computes the mappings between the trees

			EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
			actions = editScriptGenerator.computeActions(mappings); // computes the edit script
		}
		return actions;
	}

	public Tree CASTExtract(String fileSource) throws IOException {
		TreeContext fileTC = new CTreeGenerator().generateFrom().string(fileSource);
		Tree src = null;
		if (fileTC!=null) {
			src = fileTC.getRoot();
		}
		return src;
	}

	public EditScript CASTDiffMine(String srcFileSource, String dstFileSource) throws IOException, SyntaxException {
		Run.initGenerators(); // registers the available parsers
		TreeContext srcTC = new CTreeGenerator().generateFrom().string(srcFileSource);
		TreeContext dstTC = new CTreeGenerator().generateFrom().string(dstFileSource);
		EditScript actions = null;
		if (srcTC!= null && dstTC!=null) {
			Tree src = srcTC.getRoot();
			Tree dst = dstTC.getRoot();

			Matcher defaultMatcher = Matchers.getInstance().getMatcher(); // retrieves the default matcher
			MappingStore mappings = defaultMatcher.match(src, dst); // computes the mappings between the trees

			EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
			actions = editScriptGenerator.computeActions(mappings); // computes the edit script
		}
		return actions;
	}
	
}
