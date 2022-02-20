package edu.handong.csee.isel.Main;

import edu.handong.csee.isel.ChangeAnalysis.ChangeAnalyzer;
import edu.handong.csee.isel.ChangeAnalysis.StatisticsGenerator;
import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
	private String language;
	private String DiffTool;
	private String input;
	private boolean isChangeMine;
	private boolean isGitClone;
	private int volume = 0;
	private String savePath;
	private String combinePath;

    public static void main(String[] args) {
    	Main main = new Main();
		main.run(args);
    }

    private void run(String[] args) {
		checkOS();
		CLI cli = new CLI();
		ArrayList<String> projects = cli.CommonCLI(args);
		Utils utils = cli.getUtils();
		language = cli.getLanguage();
		DiffTool = cli.getDiffTool();
		input = cli.getInputPath();
		isChangeMine = cli.isChangeMine();
		isGitClone = cli.isGitClone();
		savePath = cli.getSavepath();
		combinePath = cli.getCombinePath();

		if (projects.size() == 0)
			return;

		if (cli.activateThread()) {
			int numOfCoresInMyCPU = Runtime.getRuntime().availableProcessors()/2;
			ExecutorService executor = Executors.newFixedThreadPool(numOfCoresInMyCPU);
			ArrayList<Callable<Object>> calls = new ArrayList<Callable<Object>>();

			for (String project : projects) {
				Processor processor = new Processor();
				processor.setProjectProperties(project, utils.getProjectName(project));
				processor.setProperties(language, DiffTool, input, isChangeMine, isGitClone, savePath);
				Runnable worker = processor;
				executor.execute(worker);
				calls.add(Executors.callable(worker));
			}
			try {
				executor.invokeAll(calls);
			} catch (InterruptedException e) { e.printStackTrace(); }
			executor.shutdown();
			while (!executor.isTerminated()) {}
			System.out.println("Finished\n");
		} else {
			for (String project : projects) {
				runWithNoThread(language, DiffTool, input, isChangeMine, isGitClone, savePath, project);
			}
		}

		System.out.println("For the graphical representation run graph.py file with following command" +
				"\nL $ python3 graph.py");

		if (combinePath.length()>1) {
			StatisticsGenerator statisticsGenerator = new StatisticsGenerator(combinePath);
			statisticsGenerator.combine();
		}

		return;
    }

	private void runWithNoThread(String language, String DiffTool, String input, boolean isChangeMine, boolean isGitClone, String savePath, String project) {
		try {
			ChangeAnalyzer changeAnalyzer = new ChangeAnalyzer(input);
			if (!isChangeMine) changeAnalyzer.printStatistic();
			CommitMiner commitMine = new CommitMiner(project, isGitClone);
			if (commitMine.isCompleted()) {
				ChangeMiner changeMine = new ChangeMiner();
				changeMine.setProperties(commitMine.getFilePath(), commitMine.getRepo(), language, DiffTool, commitMine.getFilePath());
				if (isChangeMine) volume += changeMine.collect(commitMine.getCommitList());
				else { changeMine.collect(commitMine.getCommitList(), changeAnalyzer); }
			}
			changeAnalyzer.setDone();
			if (isChangeMine) System.out.println("Changed Mined: " + volume);
			else if (isGitClone) return;
			else {
				changeAnalyzer.printStatistic();
				FileOutputStream fileOut = new FileOutputStream(savePath + "/" +  changeAnalyzer.getProjectName() + ".chg");
				ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
				objectOut.writeObject(changeAnalyzer);
				objectOut.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
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
//			System.setProperty("gt.pp.path", "app/pythonparser/pythonparser");
//			cmd = "pip3 install -r app/pythonparser/requirements.txt";

			System.setProperty("gt.pp.path", "../../../../pythonparser/pythonparser");
			cmd = "pip3 install -r ../../../../pythonparser/requirements.txt";

			System.setProperty("gt.cgum.path", "/data/CGYW/ASTChangeAnalyzer/app/cgum/cgum");
        }

		CommandLineExecutor cli = new CommandLineExecutor();
		cli.executeSettings(cmd);
    }
}

