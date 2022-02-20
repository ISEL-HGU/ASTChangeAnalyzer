package edu.handong.csee.isel.ChangeAnalysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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
                coreMap.get(fkey).get(hkey).add(projectName + "," + commitID);
                coreCount++;
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
                writer.write("Mined Repository Path : " + input);
                opened = true;
            }
            else if (finished) {
                writer.write("\n\n\nSummarized Statistical Analysis: "
                        + "\nL Analyzed Change size: " + totalCount
                        + "\nL HashMap(file level) size: " + fileCount
                        + "\nL HashMap(core level) size: " + coreCount);
            }
            else {
                writer.write("\n\nCurrent Statistical Analysis: "
                        + "\nL Analyzed Change size: " + totalCount
                        + "\nL HashMap(file level) size: " + fileCount
                        + "\nL HashMap(core level) size: " + coreCount);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
    public void indexWriter(String path) throws IOException {
        String savingLocation = path + "/index.csv";
        File file = new File(path);
        if (file.exists()) {
            BufferedWriter writer = Files.newBufferedWriter(
                    Paths.get(savingLocation),
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE);
            Reader in = new FileReader(savingLocation);
            CSVParser parser = CSVFormat.EXCEL.parse(in);


            for (String key : getCoreMap().keySet()) {
                for (CSVRecord record : parser) {
                    if (record.getRecordNumber() == 1) {
                        for (int i = 0; i < record.size(); i++) {
                            if (record.get(i).contains(key)) {
                                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
                                csvPrinter.printRecord("1", key);
                                csvPrinter.flush();
                            } else {

                            }
                        }
                    }
                }
            }

            return;
        }
    }
}
