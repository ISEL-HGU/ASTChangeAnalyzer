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
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;


public class ASTExtractor {
	
	public void JavaASTExtract(String fileSource) throws IOException {
		
		Run.initGenerators(); // registers the available parsers
		
		File file = new File("file.java");
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(fileSource);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Tree tree = TreeGenerators.getInstance().getTree(file.getPath()).getRoot(); // retrieves and applies the default parser for the file
		
		System.out.println(tree.toTreeString());
		
		return;
		
	}
	
	
	public EditScript JavaASTDiffMine(String srcFileSource, String dstFileSource) throws IOException {

		Run.initGenerators(); // registers the available parsers
		
		File srcFile = new File("src.java");
		File dstFile = new File("dst.java");
		
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
	
	
	public void PythonASTExtract(String fileSource) throws IOException {
		
		File file = new File("file.py");
		
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(fileSource);
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TreeContext fileTC = new PythonTreeGenerator().generateFrom().string(fileSource);
		if (fileTC!=null) {
			Tree src = fileTC.getRoot();
			System.out.println(src.toTreeString());
		}
		
		file.delete();
		
		return;
	}
	
	
	public EditScript PythonASTDiffMine(String srcFileSource, String dstFileSource) throws IOException, SyntaxException {
		
		Run.initGenerators(); // registers the available parsers
		
		File srcFile = new File("src.py");
		File dstFile = new File("dst.py");
		
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
		
//		String srcStr = CommandLineExecuter.execute(srcFile);
//		TreeContext srcTC = TreeIoUtils.fromXml().generateFrom().string(srcStr);
//		String dstStr = CommandLineExecuter.execute(dstFile);
//		TreeContext dstTC = TreeIoUtils.fromXml().generateFrom().string(dstStr);
		
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
		
		srcFile.delete();
		dstFile.delete();
		
		return actions;
		
	}
	
	
	
}
