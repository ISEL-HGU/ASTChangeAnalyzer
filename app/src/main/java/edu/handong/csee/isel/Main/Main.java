/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.handong.csee.isel.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jgit.api.errors.GitAPIException;

import edu.handong.csee.isel.ChangeAnalysis.ChangeClassifier;
import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;


public class Main {

	private static boolean isWindows;

    public static void main(String[] args) throws IOException {

    	Main main = new Main();
    	main.run(args);

    }

    private void run(String[] args) throws IOException {

    	checkOS();

    	CLI option = new CLI();

    	ArrayList<String> value = option.CommonCLI(args);

    	if (value.size()==0)
    		return;

    	System.setProperty("gt.pp.path", new File("").getAbsolutePath() +File.separator +"pythonparser"+File.separator+"pythonparser");
    	CommandLineExecuter cli = new CommandLineExecuter();
    	cli.executeSettings();

        CommitMiner commitMine;
        ChangeMiner changeMine = new ChangeMiner();

		try {
			for (String str : value) {
				commitMine = new CommitMiner(str);
				changeMine.setRepo(commitMine.getRepo());
				changeMine.setLang(option.getLanguage());
				changeMine.collect(commitMine.getCommitList());

				if (commitMine.getErase()) {
					cli.executeDeletion(commitMine.getRepoPath().getParentFile());
				}
			}


		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}

    }

    private void checkOS() {
    	if (System.getProperty("os.name").indexOf("Windows") > -1) {
            Main.setWindows(true);
        } else {
            Main.setWindows(false);
        }
    }

	public static boolean isWindows() {
		return isWindows;
	}

	public static void setWindows(boolean isWindows) {
		Main.isWindows = isWindows;
	}

}

