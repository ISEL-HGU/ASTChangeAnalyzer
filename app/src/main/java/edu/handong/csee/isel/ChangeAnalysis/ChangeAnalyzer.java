package edu.handong.csee.isel.ChangeAnalysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;


public class ChangeAnalyzer implements Serializable {
    private int totalCount;
    private int fileCount;
    private int coreCount;
    private String input;
    private boolean opened;
    private boolean finished = false;
    private HashMap<String, HashMap<String, ArrayList<String>>> coreMap;
    private String projectName;

    public ChangeAnalyzer(String input) {
        coreMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
        this.input = input;
        totalCount = 0;
        fileCount = 0;
        coreCount = 0;
        opened = false;
    }

    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getProjectName() { return projectName; }
    public int getTotalCount() { return totalCount; }
    public void setDone() { finished = true; }

    public HashMap<String, HashMap<String, ArrayList<String>>> getCoreMap() { return coreMap; }

    public void generateMap (ChangeInfo changeInfo, String language) {
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
        if (coreMap.containsKey(fkey)) {
            fileCount++;
            if (coreMap.get(fkey).containsKey(hkey)) {
                coreMap.get(fkey).get(hkey).add(projectName + "-" + commitID);
                coreCount++;
            }
            else {
                ArrayList<String> combineList = new ArrayList<String>();
                combineList.add(projectName + "-" + commitID);
                coreMap.get(fkey).put(hkey, combineList);
            }
        }
        else {
            ArrayList<String> combineList = new ArrayList<String>();
            combineList.add(projectName + "-" + commitID);
            HashMap <String, ArrayList<String>> newCoreMap = new HashMap <String, ArrayList<String>>();
            newCoreMap.put(hkey, combineList);
            coreMap.put(fkey, newCoreMap);
        }
        totalCount++;
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

    public void printStatistic() {
        int actionGroup = 0;
        for (String fkey : coreMap.keySet())
            actionGroup += coreMap.get(fkey).size();
        try {
            //BufferedWriter writer = new BufferedWriter(new FileWriter("/data/CGYW/ASTChangeAnalyzer/Statistic.txt", true));
            BufferedWriter writer = new BufferedWriter(new FileWriter("../../../../../Statistic.txt", true));
            if (!opened) {
                writer = new BufferedWriter(new FileWriter("../../../../../Statistic.txt"));
                writer.write("Mined Repository Path : " + input);
                opened = true;
            }
            else if (finished) {
                writer.write("\n\n\nSummarized Statistical Analysis: "
                        + "\nL Analyzed Change size: " + totalCount
                        + "\nL HashMap(file level) size: " + fileCount + " {" + coreMap.size() + "}"
                        + "\nL HashMap(core level) size: " + coreCount + " {" + actionGroup + "}");
            }
            else {
                writer.write("\n\nCurrent Statistical Analysis: "
                        + "\nL Analyzed Change size: " + totalCount
                        + "\nL HashMap(file level) size: " + fileCount  + " {" + coreMap.size() + "}"
                        + "\nL HashMap(core level) size: " + coreCount + " {" + actionGroup + "}");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}
