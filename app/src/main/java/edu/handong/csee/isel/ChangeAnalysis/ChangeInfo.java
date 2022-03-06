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
    private String input;
    private static boolean opened = false;
    public static final long serialVersionUID = -2073457782355550636L; // to be fixed later
    public HashMap<String, ArrayList<String>> hashMap;
    private String projectName;

    public ChangeInfo(String input) {
        hashMap = new HashMap<String, ArrayList<String>>();
        this.input = input;
    }

    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getProjectName() { return projectName; }
    public int getTotalCount() { return totalCount; }
    public HashMap<String, ArrayList<String>> getHashMap() { return hashMap; }

    public void generateMap (ChangeData changeData, String language, String commitID, String fileName) {
        String key;
        switch (language) {
            case "LAS":
                key = computeSHA256Hash(changeData.getEditOp());
                break;
            default:
                key = computeSHA256Hash(changeData.getActions());
                break;
        }
        if (hashMap.containsKey(key)) {
            hashMap.get(key).add(projectName + "-" + commitID + "-" + fileName);
        }
        else {
            ArrayList<String> list = new ArrayList<String>();
            list.add(projectName + "-" + commitID + "-" + fileName);
            hashMap.put(key, list);
            groupCount++;
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
                        + "\nformat: # of groups / # of change analyzed");
                opened = true;
            }
            if (totalCount > 0)
                writer.write("\n " + groupCount + " / " + totalCount);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

}
