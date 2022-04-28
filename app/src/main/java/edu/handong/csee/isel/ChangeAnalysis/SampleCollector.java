package edu.handong.csee.isel.ChangeAnalysis;

import edu.handong.csee.isel.RepoMiner.ChangeMiner;
import edu.handong.csee.isel.RepoMiner.CommitMiner;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.*;
import java.util.*;

public class SampleCollector {
    private String indexPath;
    private int recordNum;
    private String hashcode;
    private HashMap<String, ArrayList<String>> hashMap;

    public SampleCollector (String inputPath, int recordNum) {
        this.indexPath = inputPath;
        this.recordNum = recordNum;
        getSample();
//        countCSV();
//        addPyToMLDL();
    }

    public SampleCollector (String inputPath, String hashcode) {
        this.indexPath = inputPath;
        this.hashcode = hashcode;
        getSample(hashcode);
    }

    public void getSample(String hashcode) {
        try {
            Reader in = new FileReader(indexPath);
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : parser) {
                String rec = record.toString();
                if (rec.contains(hashcode)) {
                    rec = rec.substring(rec.indexOf("values") + 8, rec.length()-2);
                    verifyInfo(rec);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                sizeList.add(record.size()-1);
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

//            int max = sizeList.size()-1;
//            for(int i = max; i >= max-40; i= i-2) {
//                medianNeighbor.add(sizeList.get(i));
//            }
//            System.out.println("The maximum of group size for collected data : " + max
//                                + "\n\n" + "Getting the " + recordNum +  " random samples from data....");

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
                for (String record : found) {
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
                actionRecord.add("# " + projectName + "/" + fileName + "\n   " + changeMiner.collect(projectPath, fileName, commit, commitMiner.getRepo()));
            }
        }
        for (String action : actionRecord) printSampleAnalysis(action);
        printSampleAnalysis("\n");
    }


    private ArrayList<String[]> parseRecord (String record) {
        String[] contents = record.split(",");
        contents[2] = contents[2].replace("values=[", "");
        contents[contents.length-1] = contents[contents.length-1].replace("]]", "");
        printSampleAnalysis(contents[2]);
        ArrayList<String[]> recordInfo = new ArrayList<>();
        for (int i=3; i<contents.length; i++) {
            String[] data = contents[i].split("&");
            recordInfo.add(data);
        }
        return recordInfo;
    }

    private void printSampleAnalysis(String content) {
        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("/data/CGYW/ASTChangeAnalyzer/Statistic.txt", true));
//            BufferedWriter writer = new BufferedWriter(new FileWriter("../../../../../Sample.txt", true));
            BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/nayeawon/HGU/ISEL/Code/ASTChangeAnalyzer/server_test/Sample_paper.txt", true));
            writer.write(content.replace("]", "") + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private void countCSV () {
        int largest=0;
        int numGroup = 0;
        int count;
        ArrayList<Integer> sizeList = new ArrayList<Integer>();
        try {
            Reader in = new FileReader(indexPath);
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : parser) {
                count = 0;
                numGroup++;
                for (String str : record)
                    if(str.contains("~") && str.length() >= 5) count++;
                sizeList.add(count);
                if (count > largest) largest = count;
            }
            System.out.println("Number of Groups: " + numGroup);
            System.out.println("The biggest size of Group: " + largest);

            int sum=0;
            for (int i=0; i< sizeList.size(); i++) sum += sizeList.get(i);
            System.out.println("Total sum of group sizes: " + sum);
            System.out.println("Average size of Groups: " + sum/numGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPyToMLDL() {
        hashMap = new HashMap<String, ArrayList<String>>();
        try {
            // make HashMap with rMD and add it to rPY
            Reader rMD = new FileReader(indexPath);
            CSVParser parseMD = CSVFormat.EXCEL.parse(rMD);
            for (CSVRecord recordMD : parseMD) {
                String[] contents = recordMD.toString().split(",");
                contents[2] = contents[2].replace("values=[", "");
                if (hashMap.containsKey(contents[2])) {
                    ArrayList<String> tmp = hashMap.get(contents[2]);
                    for (int i=3; i<contents.length; i++) {
                        if (contents[i].length() < 5) break;
                        tmp.add(contents[i].replace("]]", ""));
                    }
                    hashMap.put(contents[2], tmp);
                }
                else {
                    ArrayList<String> files = new ArrayList<>();
                    for (int i=3; i<contents.length; i++) {
                        if (contents[i].length() < 5) break;
                        files.add(contents[i].replace("]]", ""));
                    }
                    hashMap.put(contents[2], files);
                }
            }

            Reader rPy = new FileReader("/Users/nayeawon/HGU/ISEL/Code/ASTChangeAnalyzer/python_0422.csv");
            CSVParser parsePy = CSVFormat.EXCEL.parse(rPy);
            for (CSVRecord recordPy : parsePy) {
                String[] contents = recordPy.toString().split(",");
                contents[2] = contents[2].replace("values=[", "");
                if (hashMap.containsKey(contents[2])) {
                    ArrayList<String> tmp = hashMap.get(contents[2]);
                    for (int i=3; i<contents.length; i++) {
                        if (contents[i].length() < 5) break;
                        tmp.add(contents[i].replace("]]", ""));
                    }
                    hashMap.put(contents[2], tmp);
                }
                else {
                    ArrayList<String> files = new ArrayList<>();
                    for (int i=3; i<contents.length; i++) {
                        if (contents[i].length() < 5) break;
                        files.add(contents[i].replace("]]", ""));
                    }
                    hashMap.put(contents[2], files);
                }
            }

            IndexParser indexParser = new IndexParser("/Users/nayeawon/HGU/ISEL/Code/ASTChangeAnalyzer", hashMap);
            indexParser.generateIndex();

        } catch (IOException e) { e.printStackTrace();}
    }
}
