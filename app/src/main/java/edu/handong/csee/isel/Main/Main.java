package edu.handong.csee.isel.Main;

import edu.handong.csee.isel.ChangeAnalysis.ChangeAnalyzer;
import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class Main {
	private String language;
	private String DiffTool;
	private String input;
	private boolean isChangeMine;
	private boolean isAnalysis;
	private boolean isGitClone;
	private int volume = 0;
	private int increment = 0;



    public static void main(String[] args) {
    	Main main = new Main();
		main.run(args);
    }

    private void run(String[] args) {
		checkOS();
		CLI cli = new CLI();
		ArrayList<String> inputs = cli.CommonCLI(args);
		language = cli.getLanguage(); DiffTool = cli.getDiffTool();
		isChangeMine = cli.isChangeMine(); increment = cli.getIncrement();
		isAnalysis = cli.isAnalysis(); isGitClone = cli.isGitClone();
		input = cli.getInputPath();

		if (inputs.size() == 0)
			return;

		CommitMiner commitMine;
		ChangeMiner changeMine;
		ChangeAnalyzer changeAnalyzer = new ChangeAnalyzer(input);
		if (!isChangeMine) changeAnalyzer.printStatistic();

		for (String str : inputs) {
			try {
				System.out.println(str);
				System.out.print("ASTChangeAnalyzing...");
				commitMine = new CommitMiner(str, isGitClone);
				if (commitMine.isCompleted()) {
					changeMine = new ChangeMiner();
					changeMine.setProperties(commitMine.getFilePath(), commitMine.getRepo(), language, DiffTool);
					if (isChangeMine) volume += changeMine.collect(commitMine.getCommitList());
					else { changeMine.collect(commitMine.getCommitList(), changeAnalyzer); }
					System.out.println("Finished\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		changeAnalyzer.setDone();
		if (isChangeMine) System.out.println("Changed Mined: " + volume);
		else if (isGitClone) return;
		else {
			changeAnalyzer.printStatistic();
			System.out.println("For the graphical representation run graph.py file with following command" +
					"\nL $ python3 graph.py");
			writeObjectToFile(changeAnalyzer);
		}
		return;
    }

	private void writeObjectToFile (Object changeAnalyzer) {
		try {
//			FileOutputStream fileOut = new FileOutputStream("/data/CGYW/ASTChangeAnalyzer/ASTChanges.chg", true);
			FileOutputStream fileOut = new FileOutputStream("../../../../../ASTChanges.chg", true);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(changeAnalyzer);
			objectOut.close();
			System.out.println("\nBinary File Generated\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkOS() {
		String cmd;
    	if (System.getProperty("os.name").toUpperCase().contains("MAC")) {
			System.setProperty("gt.pp.path", new File("").getAbsolutePath()
					+ File.separator + "app"
					+ File.separator + "pythonparser"
					+ File.separator + "pythonparser");

			System.setProperty("gt.cgum.path", new File("").getAbsolutePath()
					+ File.separator + "app"
					+ File.separator + "cgum"
					+ File.separator + "cgum");

			cmd = "pip3 install -r " + new File("").getAbsolutePath()
                    + File.separator + "app"
                    + File.separator + "pythonparser"
                    + File.separator + "requirements.txt";
        } else {
			System.setProperty("gt.pp.path", "../../../../pythonparser/pythonparser");
			System.setProperty("gt.cgum.path", "/data/CGYW/ASTChangeAnalyzer/app/cgum/cgum");
			cmd = "pip3 install -r ../../../../pythonparser/requirements.txt";
        }
		CommandLineExecutor cli = new CommandLineExecutor();
		cli.executeSettings(cmd);
    }
}

