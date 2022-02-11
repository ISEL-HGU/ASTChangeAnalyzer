/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.handong.csee.isel.Main;

import edu.handong.csee.isel.ChangeAnalysis.ChangeAnalyzer;
import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {

	private String os;
	private String language;
	private String DiffTool;
	private String input;
	private int total_count = 0;
	private int file_count = 0;
	private int hunk_count = 0;
	private int core_count = 0;
	private HashMap<String, ArrayList<String>> fileMap;
	private HashMap<String, ArrayList<String>> hunkMap;
	private HashMap<String, HashMap<String, ArrayList<String>>> coreMap;

    public static void main(String[] args) {
    	Main main = new Main();
		main.run(args);
    }

    private void run(String[] args) {
		checkOS();
		CLI option = new CLI();
		ArrayList<String> inputs = option.CommonCLI(args);
		language = option.getLanguage();
		DiffTool = option.getDiffTool();
		input = option.getOptionValueP();

		fileMap = new HashMap<String, ArrayList<String>>();
		hunkMap = new HashMap<String, ArrayList<String>>();
		coreMap = new HashMap<String, HashMap<String, ArrayList<String>>>();

		if (inputs.size() == 0)
			return;

		CommitMiner commitMine;
		ChangeMiner changeMine;

		for (String str : inputs) {
			try {
				System.out.println(str);
				System.out.print("ASTChangeAnalyzing...");
				commitMine = new CommitMiner(str);
				if (commitMine.isCompleted()) {
					System.out.print("Change Mining...");
					changeMine = new ChangeMiner();
					changeMine.setProperties(commitMine.getRepo(), language, DiffTool);
					ArrayList<ChangeInfo> changeInfoList = changeMine.collect(commitMine.getCommitList());
					if (changeInfoList.size() < 1) {
						System.out.println("Change Mining Failed\n");
						continue;
					}
					for (ChangeInfo changeInfo : changeInfoList) {
						generateMap(changeInfo);
					}
					System.out.println("Finish\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"));
			writer.write("Mined Repository Path : " + input
					+ "\nAnalyzed Change size : " + total_count
					+ "\nHashMap(file level) size: " + file_count
					+ "\nHashMap(hunk level) size: " + hunk_count
					+ "\nHashMap(core level) size: " + core_count);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	private void setOS(String os) {
		this.os = os;
	}

	private void checkOS() {
		String cmd;
    	if (System.getProperty("os.name").toUpperCase().contains("MAC")) {
            setOS("MAC");
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
			setOS("LINUX");
			System.setProperty("gt.pp.path", "../../../../pythonparser/pythonparser");
			System.setProperty("gt.cgum.path", "/data/CGYW/ASTChangeAnalyzer/app/cgum/cgum");
			cmd = "pip3 install -r ../../../../pythonparser/requirements.txt";
        }
		CommandLineExecutor cli = new CommandLineExecutor();
		cli.executeSettings(cmd);
    }

	private void generateMap (ChangeInfo changeInfo) {
		String fkey;
		String hkey;
		String projectName = changeInfo.getProjectName();
		String commitID = changeInfo.getCommitID();
		switch (language) {
			case "LAS":
				fkey = computeSHA256Hash(changeInfo.getEditOpWithName());
				hkey = computeSHA256Hash(changeInfo.getEditOpWithType());
				break;
			default:
				fkey = computeSHA256Hash(changeInfo.getActionsWithName());
				hkey = computeSHA256Hash(changeInfo.getActionsWithType());
				break;
		}

		if (fileMap.containsKey(fkey)) {
			fileMap.get(fkey).add(projectName + "," + commitID);
			file_count++;
		}
		else {
			ArrayList<String> fileList = new ArrayList<String>();
			fileList.add(projectName + "," + commitID);
			fileMap.put(fkey, fileList);
		}


		if (hunkMap.containsKey(hkey)) {
			hunkMap.get(hkey).add(projectName + "," + commitID);
			hunk_count++;
		}
		else {
			ArrayList<String> hunkList = new ArrayList<String>();
			hunkList.add(projectName + "," + commitID);
			hunkMap.put(hkey, hunkList);
		}


		if (coreMap.containsKey(fkey)) {
			if (coreMap.get(fkey).containsKey(hkey)) {
				coreMap.get(fkey).get(hkey).add(projectName + "," + commitID);
				core_count++;
			}
			else {
				ArrayList<String> combineList = new ArrayList<String>();
				combineList.add(projectName + "," + commitID);
				coreMap.get(fkey).put(hkey, combineList);
			}
		}
		else {
			ArrayList<String> combineList = new ArrayList<String>();
			combineList.add(projectName + "," + commitID);
			HashMap <String, ArrayList<String>> newCoreMap = new HashMap <String, ArrayList<String>>();
			newCoreMap.put(hkey, combineList);
			coreMap.put(fkey, newCoreMap);
		}

		total_count++;
	}

	public String computeSHA256Hash(String hashString) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(hashString.getBytes());
			byte bytes[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for(byte b : bytes){
				sb.append(Integer.toString((b&0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}

