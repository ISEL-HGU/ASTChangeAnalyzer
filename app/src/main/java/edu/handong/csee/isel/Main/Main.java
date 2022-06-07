package edu.handong.csee.isel.Main;

import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import edu.handong.csee.isel.ChangeAnalysis.IndexParser;
import edu.handong.csee.isel.ChangeAnalysis.NumberCounter;
import edu.handong.csee.isel.ChangeAnalysis.SampleCollector;
import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;
import edu.handong.csee.isel.RepoMiner.IssueMiner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
	private String language;
	private String DiffTool;
	private String input;
	private boolean isChangeCount;
	private boolean isGitClone;
	private int volume = 0;
	private String chgPath;
	private String indexPath;
	private String count;

    public static void main(String[] args) {
    	Main main = new Main();
		main.run(args);
    }

    private void run(String[] args) {
		checkOS();
		CLI cli = new CLI();
		ArrayList<String> projects = cli.CommonCLI(args);
		language = cli.getLanguage();
		if (!language.equals("JAVA"))
			DiffTool = "GUMTREE";
		else DiffTool = cli.getDiffTool();
		input = cli.getInputPath();
		isChangeCount = cli.isChangeCount();
		isGitClone = cli.isGitClone();
		chgPath = cli.getChgPath();
		indexPath = cli.getIndexPath();
		count = cli.getCount();
		String hashcode = cli.getHashcode();

		String issueMine = cli.getIssueMine();
		if (hashcode != null) {
			if (hashcode.length() == 64 && indexPath.length() > 0) {
				new SampleCollector(indexPath, hashcode);
				return;
			}
		} else if (issueMine != null) {
			new IssueMiner(cli.getInputPath(),issueMine);
			return;
		} else if (count != null) {
			new NumberCounter(cli.getInputPath(),count);
			return;
		}


		if (projects.size() > 0 && !cli.activateThread()) {
			int numOfCoresInMyCPU = Runtime.getRuntime().availableProcessors()/2;
			ExecutorService executor = Executors.newFixedThreadPool(numOfCoresInMyCPU);
			ArrayList<Callable<Object>> calls = new ArrayList<>();

			for (String project : projects) {
				Processor processor = new Processor();
				processor.setProjectProperties(project);
				processor.setProperties(language, DiffTool, input, isChangeCount, isGitClone, chgPath);
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
			System.out.println(projects.size());
			if (projects.size() > 0) {
				for (String project : projects) runWithNoThread(language, DiffTool, input, isChangeCount, isGitClone, chgPath, project);
			}
		}

		System.out.println("For the graphical representation run graph.py file with following command" +
				"\nL $ python3 graph.py\n");

//		if (indexPath.length()>1) {
//			new SampleCollector(indexPath,20);
//			//BinaryReader binaryReader = new BinaryReader(indexPath);
//			//binaryReader.getHashMap();
//		}

		if (isChangeCount) System.out.println("Total Change Mined: " + ChangeMiner.count);

		return;
    }

	private void runWithNoThread(String language, String DiffTool, String input, boolean isChangeMine, boolean isGitClone, String savePath, String project) {
		try {
			ChangeInfo changeInfo = new ChangeInfo(input);
			CommitMiner commitMine = new CommitMiner(project, isGitClone);
			if (commitMine.isCompleted()) {
				ChangeMiner changeMine = new ChangeMiner();
				changeMine.setProperties(commitMine.getFilePath(), commitMine.getRepo(), language, DiffTool);
				changeInfo.setProjectName(commitMine.getMatcherGroup().replaceAll("/", "~"));
				if (isChangeMine) changeMine.collect(commitMine.getCommitList());
				else { changeMine.collect(commitMine.getMatcherGroup().replaceAll("/", "~"), commitMine.getCommitList(), changeInfo); }
				if (isChangeMine) System.out.println("Changed Mined: " + volume);
				else if (isGitClone) return;
				else {
//					IndexParser index = new IndexParser(savePath, changeInfo.getHashMap());
//					index.generateIndex();
//					FileOutputStream fileOut = new FileOutputStream(savePath + "/" +  changeInfo.getProjectName() + ".chg");
//					ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
//					objectOut.writeObject((ChangeInfo) changeInfo);
//					objectOut.close();
				}
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
			System.setProperty("gt.pp.path", "/home/nayeawon/.local/bin/pythonparser");
			cmd = "pip3 install -r ../../../../pythonparser/requirements.txt";

			System.setProperty("gt.cgum.path", "/home/nayeawon/.local/bin/cgum");
        }

		CommandLineExecutor cli = new CommandLineExecutor();
		cli.executeSettings(cmd);
    }
}

