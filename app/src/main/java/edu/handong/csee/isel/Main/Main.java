/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.handong.csee.isel.Main;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import edu.handong.csee.isel.RepoMiner.CodeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;


public class Main {
	
    public static void main(String[] args) {
    	
    	Main main = new Main();
    	main.run(args);
		
    }
    
    private void run(String[] args) {
    	
    	CLI option = new CLI();
    	String value = option.CommonCLI(args);
    	
    	if (value.length()==0)
    		return;
    	
    	System.setProperty("gt.pp.path", new File("").getAbsolutePath() + "/pythonparser/pythonparser");

        CommitMiner commitMine;
        CodeMiner codeMine = new CodeMiner();
        
		try {
			
			commitMine = new CommitMiner(value);
			codeMine.setRepo(commitMine.getRepo());
			codeMine.setLang(option.getLanguage());
			codeMine.collect(commitMine.getCommitList());
			
			if (commitMine.getErase()) {
				new CommandLineExecuter().executeDeletion(commitMine.getRepoPath().getParentFile());
			}
			
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}
		
    }
}
