package edu.handong.csee.isel.ChangeAnalysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChangeAnalyzer {
    private int total_count;
    private int file_count;
    private int hunk_count;
    private int core_count;
    private HashMap<String, HashMap<String, ArrayList<String>>> coreMap;

    public ChangeAnalyzer() {
        coreMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
        total_count = 0;
        file_count = 0;
        core_count = 0;
    }

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
            file_count++;
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

    public void printResult(String input) {
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
        return;
    }

}
