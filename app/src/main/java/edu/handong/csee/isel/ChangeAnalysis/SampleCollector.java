package edu.handong.csee.isel.ChangeAnalysis;

import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SampleCollector {
    private String indexPath;
    private int recordNum;

    public SampleCollector (String inputPath, int recordNum) {
        this.indexPath = inputPath;
        this.recordNum = recordNum;
        getSample();
    }

    public void getSample() {
        Reader in = null;
        Random ran = new Random();
        ArrayList<Integer> sizeList = new ArrayList<Integer>();
        ArrayList<String> found = new ArrayList<String>();
        try {
            in = new FileReader(indexPath);
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : parser) {
                sizeList.add(record.size());
            }
            Collections.sort(sizeList);
            ArrayList<Integer> medianNeighbor = new ArrayList<>();
            int half = sizeList.size()/2;
            int median = sizeList.get(half);
            for(int i = half - 20; i < half+20; i+=2) {
                medianNeighbor.add(sizeList.get(i));
            }
            System.out.println("The median of group size for collected data : " + median
                                + "\n\n" + "Getting the " + recordNum +  " random samples from data....");

            in = new FileReader(indexPath);
            CSVParser parser2 = CSVFormat.EXCEL.parse(in);
            List<CSVRecord> list = parser2.getRecords();

            for (CSVRecord record : list) {
                for (int check: medianNeighbor) {
                    if(record.size() == check)
                        found.add(record.toString());
                }
            }
            if(found.size() > recordNum) {
                for(int i = 0; i < recordNum; i++) {
                    String record = found.get(ran.nextInt(found.size()-1));
                    record = record.substring(record.indexOf("values") + 8, record.length()-2);
                    verifyInfo(record);
                }
            } else {
                for(String record : found) {
                    verifyInfo(record);
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void verifyInfo (String record) {
        ArrayList<String> actionRecord = new ArrayList<>();
        ArrayList<String[]> recordInfo = parseRecord(record);
        for (String[] recordData : recordInfo) {
            String projectName = recordData[0].trim().replaceAll("~", "/");
            String commitID = recordData[1].trim();
            String fileName = recordData[2].trim();
            CommitMiner commitMiner = new CommitMiner();
            RevCommit commit = commitMiner.getCommit(projectName, commitID);
            String projectPath = commitMiner.getFilePath() + projectName;
            if (commit!=null) {
                ChangeMiner changeMiner = new ChangeMiner();
                actionRecord.add(projectName + "\n\t" + changeMiner.collect(projectPath, fileName, commit, commitMiner.getRepo()));
            }
        }
        for (String action : actionRecord) printSampleAnalysis(action);
        printSampleAnalysis("\n");
    }


    private ArrayList<String[]> parseRecord (String record) {
        String[] contents = record.split(",");
        printSampleAnalysis(contents[0]);
        ArrayList<String[]> recordInfo = new ArrayList<>();
        for (int i=1; i<contents.length; i++) {
            String[] data = contents[i].split("&");
            recordInfo.add(data);
        }
        return recordInfo;
    }

    private void printSampleAnalysis(String content) {
        try {
            //BufferedWriter writer = new BufferedWriter(new FileWriter("/data/CGYW/ASTChangeAnalyzer/Statistic.txt", true));
            BufferedWriter writer = new BufferedWriter(new FileWriter("../../../../../Sample.txt", true));
            writer.write(content + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}
