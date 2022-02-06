/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.handong.csee.isel.Main;

import edu.handong.csee.isel.ChangeAnalysis.ChangeAnalyzer;
import edu.handong.csee.isel.ChangeAnalysis.ChangeInfo;
import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {

    public static void main(String[] args) throws IOException {
    	Main main = new Main();
    	main.run(args);
    }

	private String os;
	private HashMap<String, ArrayList<String>> fileMap;
	private HashMap<String, ArrayList<String>> hunkMap;
	private HashMap<String, HashMap<String, ArrayList<String>>> coreMap;

    private void run(String[] args) throws IOException {
		checkOS();
		CLI option = new CLI();
		ArrayList<String> value = option.CommonCLI(args);

		if (value.size() == 0)
			return;

		CommitMiner commitMine;
		ChangeMiner changeMine;
		ArrayList<ArrayList<ChangeInfo>> changeInfoList = new ArrayList<ArrayList<ChangeInfo>>();

		try {
			for (String str : value) {
				commitMine = new CommitMiner(str);
				changeMine = new ChangeMiner();
				changeMine.setRepo(commitMine.getRepo());
				changeMine.setLang(option.getLanguage());
				changeMine.setLevel(option.getLevel());
				changeMine.setDiffTool(option.getDiffTool());
				changeInfoList.add(changeMine.collect(commitMine.getCommitList()));
			}
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}

		ChangeAnalyzer changeAnalyzer = new ChangeAnalyzer();

		fileMap = new HashMap<String, ArrayList<String>>();
		hunkMap = new HashMap<String, ArrayList<String>>();
		coreMap = new HashMap<String, HashMap<String, ArrayList<String>>>();

		for (ArrayList<ChangeInfo> changeList : changeInfoList) {
			for (ChangeInfo changeInfo : changeList) {
				String fkey = changeAnalyzer.computeSHA256Hash(changeInfo.getActionsWithName());
				if (fileMap.containsKey(fkey))
					fileMap.get(fkey).add(changeInfo.getProjectName() + "," + changeInfo.getCommitID());
				else {
					ArrayList<String> fileList = new ArrayList<String>();
					fileList.add(changeInfo.getProjectName() + "," + changeInfo.getCommitID());
					fileMap.put(fkey, fileList);
				}

				String hkey = changeAnalyzer.computeSHA256Hash(changeInfo.getActionsWithType());
				if (hunkMap.containsKey(hkey))
					hunkMap.get(hkey).add(changeInfo.getProjectName() + "," + changeInfo.getCommitID());
				else {
					ArrayList<String> hunkList = new ArrayList<String>();
					hunkList.add(changeInfo.getProjectName() + "," + changeInfo.getCommitID());
					hunkMap.put(hkey, hunkList);
				}

				if (coreMap.containsKey(fkey)) {
					if (coreMap.get(fkey).containsKey(hkey)) {
						coreMap.get(fkey).get(hkey).add(changeInfo.getProjectName() + "," + changeInfo.getCommitID());
					}
					else {
						ArrayList<String> combineList = new ArrayList<String>();
						combineList.add(changeInfo.getProjectName() + "," + changeInfo.getCommitID());
						coreMap.get(fkey).put(hkey, combineList);
					}
				}
				else {
					ArrayList<String> combineList = new ArrayList<String>();
					combineList.add(changeInfo.getProjectName() + "," + changeInfo.getCommitID());
					HashMap <String, ArrayList<String>> newCoreMap = new HashMap <String, ArrayList<String>>();
					newCoreMap.put(hkey, combineList);
					coreMap.put(fkey, newCoreMap);
				}
			}
		}
		int count = 0;
		for (String key : fileMap.keySet()) {
			count += fileMap.get(key).size();
		}
		System.out.println("\nHashMap(file level) size: " + count);

		count = 0;
		for (String key : hunkMap.keySet()) {
			count += hunkMap.get(key).size();
		}
		System.out.println("\nHashMap(hunk level) size: " + count);

		count=0;
		for (String strName : coreMap.keySet()) {
			for (String strType : coreMap.get(strName).keySet()) {
				count += coreMap.get(strName).get(strType).size();
			}
		}
		System.out.println("\nHashMap(core level) size: " + count);

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

	public void setOS(String os) {
		this.os = os;
	}

}

