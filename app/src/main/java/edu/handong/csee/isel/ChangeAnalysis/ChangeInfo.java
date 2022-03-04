package edu.handong.csee.isel.ChangeAnalysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;


public class ChangeInfo implements Serializable {
    private static int totalCount;
    private static int groupCount;
    private static int coreCount;
    private String input;
    private static boolean opened = false;
    public static final long serialVersionUID = -2073457782355550636L; // to be fixed later
    public HashMap<String, HashMap<String, ArrayList<String>>> coreMap;
    private String projectName;

    public ChangeInfo(String input) {
        coreMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
        this.input = input;
    }

    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getProjectName() { return projectName; }
    public int getTotalCount() { return totalCount; }
    public HashMap<String, HashMap<String, ArrayList<String>>> getCoreMap() { return coreMap; }

    public void generateMap (ChangeData changeData, String language, String commitID) {
        String fkey;
        String hkey;
        switch (language) {
            case "LAS":
                fkey = computeSHA256Hash(changeData.getEditOpWithName());
                hkey = computeSHA256Hash(changeData.getEditOpWithType());
                break;
            default:
                fkey = computeSHA256Hash(changeData.getActionsWithName());
                hkey = computeSHA256Hash(changeData.getActionsWithType());
                break;
        }
        if (coreMap.containsKey(fkey)) {
            if (coreMap.get(fkey).containsKey(hkey)) {
                coreMap.get(fkey).get(hkey).add(projectName + "-" + commitID);
            }
            else {
                ArrayList<String> combineList = new ArrayList<String>();
                combineList.add(projectName + "-" + commitID);
                coreMap.get(fkey).put(hkey, combineList);
                coreCount++;
            }
        }
        else {
            ArrayList<String> combineList = new ArrayList<String>();
            combineList.add(projectName + "-" + commitID);
            HashMap <String, ArrayList<String>> newCoreMap = new HashMap <String, ArrayList<String>>();
            newCoreMap.put(hkey, combineList);
            coreMap.put(fkey, newCoreMap);
            groupCount++;
            coreCount++;
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
        try {
            //BufferedWriter writer = new BufferedWriter(new FileWriter("/data/CGYW/ASTChangeAnalyzer/Statistic.txt", true));
            BufferedWriter writer = new BufferedWriter(new FileWriter("../../../../../Statistic.txt", true));
            if (!opened) {
                writer = new BufferedWriter(new FileWriter("../../../../../Statistic.txt"));
                writer.write("Mined Repository Path: " + input
                        + "\nformat: [# of groups] / # of change analyzed"
                        + "\n-file , hunk");
                opened = true;
            }
            writer.write("\n [" + groupCount + " , " + coreCount + "] / " + totalCount);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

}
