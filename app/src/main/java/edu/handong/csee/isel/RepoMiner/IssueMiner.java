package edu.handong.csee.isel.RepoMiner;

import edu.handong.csee.isel.ChangeAnalysis.NumberCounter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.SerializationUtils;
import org.checkerframework.checker.units.qual.A;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssueMiner {

    private int total;
    private int withIssue;
    private int csvLines;
    private String path;
    private HashMap<String, ArrayList<String>> map = new HashMap<>();
    private ArrayList<HashMap<String, ArrayList<String>>> divided = new ArrayList<>();
    private ArrayList<HashMap<String, ArrayList<String>>> newDivided = new ArrayList<>();
    private HashMap<String, ArrayList<String>> projectList = new HashMap<>();
    private HashMap<String, String> projectKey = new HashMap<>();
    private HashMap<String, ArrayList<String>> newMap = new HashMap<>();

    public IssueMiner(String path, String cliInput) {
        this.path = path;
        ThreadRunner threads[] = new ThreadRunner[5];


        NumberCounter counter = new NumberCounter(path,"lines");
        csvLines = counter.countLines()-1;
        int divisor = csvLines/5;
        for (int i = 1; i <6;i++) {
            divided.add(readPartial(path,(divisor*(i-1)+1),divisor*i));
        }
        for (int i = 0; i<5;i++) {
            threads[i] = new ThreadRunner(i);
            threads[i].start();
        }

       // readPartial(path,map);

//        readIssueKeys();
//        int [] nums = new int [2];
//        int i = 0;
//        for(String x : cliInput.split(","))
//            nums[i++] = Integer.parseInt(x.trim());

        //selectElementsWithNoIssue(cliInput);
//        combineProjectWithIssueCSV();
//        readPartial(path,nums[0],nums[1]);
//        makeIssueIndex();
//        mapToCsv(path, "_" + nums[0] + "~" + nums[1], newMap);
//        mapToCsv(path,"_" + nums[0] + "~" + nums[1]+"_issuePerProject", projectList);
//        readIndex(path);
//        mapToCsv(path,takeOneWithIssues());

//        makeIssueIndex(takeOneWithIssues(readIndex(path)),path);
//        System.out.println("Result:" + "\n" + "Total Changes - " + total
//                + "\n" + "Changes with issues - " + withIssue:
//                + "\n" + "Proportion - " + withIssue/total);
    }

    public void selectElementsWithNoIssue(String inputFiles) {
        String[] files = inputFiles.split(",");
        HashMap<String, ArrayList<String>> srcCSV = new HashMap<>();
        readIndex(files[0], srcCSV);
        HashMap<String, ArrayList<String>> dstCSV = new HashMap<>();
        readIndex(files[1], dstCSV);
        if (srcCSV.size() > dstCSV.size()) {
            mapTwoHashMaps(dstCSV, srcCSV); // dstCSV < srcCSV
        } else mapTwoHashMaps(srcCSV, dstCSV); // srcCSV < dstCSV
    }

    public void mapTwoHashMaps(HashMap<String, ArrayList<String>> small, HashMap<String, ArrayList<String>> big) {
        String newPath = "/data/CGYW/javachg/removedElementsBetweenTwoCSVs.csv";
        try {
            FileOutputStream fos = new FileOutputStream(newPath);
            PrintWriter out = new PrintWriter(fos);
            for (String key : big.keySet()) {
                if (!small.containsKey(key)) {
                    out.print(key);
                    boolean check = false;
                    for (String contents : big.get(key)) {
                        if (contents != null) {
                            check = true;
                            out.print("," + contents.trim());
                        }
                    }
                    if (check)
                        out.print("\n");
                    continue;
                }
                ArrayList<String> smallList = small.get(key);
                ArrayList<String> removedList = new ArrayList<>();
                for (String smallValue : smallList) {
                    for (String value : big.get(key)) {
                        if (!removedList.contains(value) && !smallValue.contains(value)) removedList.add(value);
                    }
                }
                out.print(key);
                boolean check = false;
                for (String contents :removedList) {
                    if (contents != null) {
                        check = true;
                        out.print("," + contents.trim());
                    }
                }
                if (check)
                    out.print("\n");
            }
            out.flush();
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void combineCSV() {
        HashMap<String, ArrayList<String>> csv = new HashMap<>();
        for (int i=0; i<1000001; i+=100000) {
            if (i==0) {
                i++;
                readIndex("/data/CGYW/javachg/index_java_" + 0 + "~" + 100000 +".csv", csv);
            }
            else {
			int j = i + 99999;
			 readIndex("/data/CGYW/javachg/index_java_" + i + "~" + j +".csv", csv);
        	}
	}
        readIndex("/data/CGYW/javachg/" + 1000001 + "~" + 1156754 +".csv", csv);
        readIndex("/data/CGYW/javachg/project_commit_file_issue" +".csv", csv);
        removeDuplicate(csv);

//        readIndex("/data/CGYW/javachg/index_java_" + 0 + "~" + 50000 +".csv", csv);
//        readIndex("/data/CGYW/javachg/index_java_" + 50001 + "~" + 100000 +".csv", csv);
//        mapToCsv("/data/CGYW/javachg/.csv", "index_java_0~100000", csv);
    }

    public void combineProjectWithIssueCSV() {
        HashMap<String, ArrayList<String>> csv = new HashMap<>();
        for (int i=0; i<1000001; i+=100000) {
            if (i==0) {
                i++;
                readIndex2("/data/CGYW/javachg/index_java_" + 0 + "~" + 100000 +"_issuePerProject.csv", csv);
            }
            else {
			int j = i + 99999;
			 readIndex2("/data/CGYW/javachg/index_java_" + i + "~" + j +"_issuePerProject.csv", csv);
       		
		}
	 }
//        readIndex("/data/CGYW/javachg/index_java_" + 1000001 + "~" + 1156755 +"_issuePerProject.csv", csv);
        mapToCsv("/data/CGYW/javachg/.csv", "project_issue", csv);

//        readIndex2("/data/CGYW/javachg/index_java_" + 0 + "~" + 50000 +"_issuePerProject.csv", csv);
//        readIndex2("/data/CGYW/javachg/index_java_" + 50001 + "~" + 100000 +"_issuePerProject.csv", csv);
//        mapToCsv("/data/CGYW/javachg/.csv", "index_java_0~100000" + "_issuePerProject", csv);

    }
    public HashMap<String, ArrayList<String>> removeDuplicate (HashMap<String, ArrayList<String>> map) {
        HashMap<String, ArrayList<String>> temp = new HashMap<>();
        HashMap<String, ArrayList<String>> removedElements = new HashMap<>();
        for (String key : map.keySet()) {
            ArrayList<String> tmpList= new ArrayList<>();
            ArrayList<String> removedElementsList = new ArrayList<>();
            for (String contents : map.get(key)) {
                if(!tmpList.contains(contents))
                    tmpList.add(contents);
                else removedElementsList.add(contents);
            }
            temp.put(key,tmpList);
            if (removedElementsList.size() > 2) removedElements.put(key, removedElementsList);
        }
        if (removedElements.size() > 0)
            mapToCsv("/data/CGYW/javachg/.csv", "removed_elements", removedElements);
        return temp;
    }
    public void readIssueKeys () {
        int URLColumnNumber = 0;
        int keyColumn = 0;
        try {
            Reader in = new FileReader("/data/CGYW/ASTChangeAnalyzer/data/apacheURLList.csv");
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : parser) {
		if (record.getRecordNumber() == 1 ) {
                    for(int i = 0; i < record.size(); i++) {
                        if(record.get(i).contains("Github")) {
                            URLColumnNumber = i;
                        } else if(record.get(i).contains("KEY")) {
                            keyColumn = i;
                        }
                        continue;
                    }
                }
                projectKey.put(record.get(URLColumnNumber),record.get(keyColumn));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public HashMap<String,ArrayList<String>> readPartial (String indexPath, int from, int to) {
        ArrayList<String> temp = null;
        HashMap <String,ArrayList<String>> dividedMap = new HashMap<>();
        int cnt = 0;
        try {
            Reader in = new FileReader(indexPath);
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : parser) {
                if (cnt < from) {
                    cnt++;
                    continue;
                }
                else if (cnt > to) break;
                cnt++;
                temp = new ArrayList<String>();
                String key = "";
                for (String str : record) {
                    if(str.contains("~")) {
                        temp.add(str.replace("]]", "").trim());
                    } else {
                        key = str;
                    }
                }
                if(!key.equals(""))
                    dividedMap.put(key,temp);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();}
        return dividedMap;
    }

    public void readIndex (String indexPath, HashMap<String, ArrayList<String>> csv) {
        //HashMap<String, ArrayList<String>> file = new HashMap<String, ArrayList<String>>();
        ArrayList<String> temp = null;
        String key = "";
        try {
                Reader in = new FileReader(indexPath);
                CSVParser parser = CSVFormat.EXCEL.parse(in);
                for (CSVRecord record : parser) {
                    temp = new ArrayList<String>();
                    for (String str : record) {
                        if(str.contains("~")) {
                            temp.add(str.replace("]]", "").trim());
                        } else {
                            key = str;
                        }
                    }
                    csv.put(key,temp);
                }
                in.close();
            } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void readIndex2 (String indexPath, HashMap<String, ArrayList<String>> csv) {
        //HashMap<String, ArrayList<String>> file = new HashMap<String, ArrayList<String>>();
        ArrayList<String> temp = null;
        String key = "";
        try {
            Reader in = new FileReader(indexPath);
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : parser) {
                temp = new ArrayList<String>();
                for (String str : record) {
                    if(str.contains("-")) {
                        temp.add(str.replace("]]", "").trim());
                    } else {
                        key = str;
                    }
                }
                csv.put(key,temp);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeIssueIndex() {
        for (String key : map.keySet()) {
            ArrayList<String> issues = new ArrayList<>();
            for (String contents : map.get(key)) {
                String [] temp = contents.split("&");
                String issueNum = getIssueNum(temp[0].replace("~","/").trim(),temp[1].trim());
                if (issueNum == null || issueNum.length() == 0)
                    continue;
                issues.add(contents.trim() + issueNum);
                countIssuePerProject(temp[0],issueNum);
            }
            if (issues.size()>0)
                newMap.put(key, issues);
        }
    }

    public void countIssuePerProject (String projectName, String issueNum) {
        String [] arr = issueNum.split("~");

        if (projectList.containsKey(projectName.trim())) {
	    int cnt = 0;
            for (String x : arr) {
		if (cnt++ == 0) {
		    continue;
		}
                projectList.get(projectName).add(x);
	    }
        } else {
            ArrayList<String> temp = new ArrayList<>();
	    int cnt = 0;
            for (String x : arr){
		if (cnt++ == 0) continue;
                temp.add(x);
	    }
	    projectList.put(projectName,temp);
        }
    }

    public void makeIssueIndex(HashMap<String, ArrayList<String>> map, String path) {
        String newPath = path.replace(".csv","_issue.csv");


        try {
            FileOutputStream fos = new FileOutputStream(newPath);
            PrintWriter out = new PrintWriter(fos);

            for (String key : map.keySet()) {
                out.print(key);
                for (String contents : map.get(key)) {
                    String [] temp = contents.split("&");
                    out.print("," + contents.trim() + getIssueNum(temp[0].replace("~","/").trim(),temp[1].trim()));
                }
                out.print("\n");
            }
            out.flush();
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mapToCsv (String path, String name, HashMap<String, ArrayList<String>> temp) {
//        String newPath = path.replace(".csv","_issue.csv");
        String newPath = path.replace(".csv", name + ".csv");
        try {
            FileOutputStream fos = new FileOutputStream(newPath);
            PrintWriter out = new PrintWriter(fos);

            for (String key : temp.keySet()) {
                out.print(key);
                boolean check = false;
                for (String contents : temp.get(key)) {
                    if (contents != null) {
                        check = true;
                        out.print("," + contents.trim());
                    }

                }
                if (check)
                    out.print("\n");
            }
            out.flush();
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getIssueNum (String projectName, String ID) {
        String IssueNum = "";

//        Pattern pattern = Pattern.compile("(#\\d+)");
        Pattern pattern = Pattern.compile("([a-zA-Z]+-\\d+)|([a-zA-Z]+\\d+-\\d+)");
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = null;
        try {
            repo = builder.setGitDir(new File("/data/CGYW/clones/"+projectName+"/.git/.git")).setMustExist(true).build();

        Git git = new Git(repo);
            Iterable<RevCommit> log = null;
            try {
                log = git.log().call();
                for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();) {
                    RevCommit rev = iterator.next();
                    if(ID.equals(rev.getName())) {
//                    System.out.println(projectName + "/" + ID + ":" + rev.getFullMessage());
//                    break;
                        String msg = rev.getFullMessage();
                        Matcher matcher = pattern.matcher(msg);
                        while(matcher.find()) {

			                if (projectKey.get("https://github.com/" + projectName)!=null) {
                                if(matcher.group(1).toUpperCase().contains(projectKey.get("https://github.com/" + projectName).toUpperCase())) {
                                    IssueNum += "~" + matcher.group(1);
                                } else if (matcher.group(0).toUpperCase().contains(projectKey.get("https://github.com/" + projectName).toUpperCase())) {
                                    IssueNum += "~" + matcher.group(0);
                                }

                            }

                        }
                        break;
	    	        }
                }
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return IssueNum;
    }

    public HashMap<String, ArrayList<String>> takeOneWithIssues () {
        HashMap<String, ArrayList<String>> temp = SerializationUtils.clone(new HashMap<>(map));
        for (String key : map.keySet()) {
            int i = 0;
            for (String contents : map.get(key)) {
                if (!contents.contains("#")) {
                    temp.get(key).remove(i);
                    i--;
                }
                if (temp.get(key).size() == 0) {
                    temp.remove(key);
                    break;
                }
                i++;
            }
        }
        return temp;
    }

    public ArrayList<HashMap<String, ArrayList<String>>> getDivided() { return divided; }
    public void addToNewDivided(HashMap<String, ArrayList<String>> a) { this.newDivided.add(a); }
    public String getPath() { return path; }

    private class ThreadRunner extends Thread {
        private HashMap<String,ArrayList<String>> map = new HashMap<>();
        private HashMap<String,String> keyList = new HashMap<>();
        private int index;
        public ThreadRunner (int index) {
            map = getDivided().get(index);
            this.index = index;
        }
        public void run() {
            readKeyList();
            makeIssueIndexSub(map,getPath());



        }
        public void makeIssueIndexSub(HashMap<String, ArrayList<String>> map, String path) {
            String newPath = path.replace(".csv","_issue_"+index+".csv");

            try {
                FileOutputStream fos = new FileOutputStream(newPath);
                PrintWriter out = new PrintWriter(fos);

                for (String key : map.keySet()) {
                    out.print(key);
                    for (String contents : map.get(key)) {
                        String [] temp = contents.split("&");
                        out.print("," + contents.trim() + getIssueNumSub(temp[0].replace("~","/").trim(),temp[1].trim()));
                    }
                    out.print("\n");
                }
                out.flush();
                out.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getIssueNumSub (String projectName, String ID) {
            String IssueNum = "";

//        Pattern pattern = Pattern.compile("(#\\d+)");
            Pattern pattern = Pattern.compile("([a-zA-Z]+-\\d+)|([a-zA-Z]+\\d+-\\d+)");
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repo = null;
            try {
                repo = builder.setGitDir(new File("/data/CGYW/clones/"+projectName+"/.git/.git")).setMustExist(true).build();

                Git git = new Git(repo);
                Iterable<RevCommit> log = null;
                try {
                    log = git.log().call();
                    for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();) {
                        RevCommit rev = iterator.next();
                        if(ID.equals(rev.getName())) {
//                    System.out.println(projectName + "/" + ID + ":" + rev.getFullMessage());
//                    break;
                            String msg = rev.getFullMessage();
                            Matcher matcher = pattern.matcher(msg);
                            while(matcher.find()) {
                                String key = keyList.get("https://github.com/" + projectName);
                                if (key == null)
                                    continue;
                                String key_1 = "";
                                if (key.contains("LUCENE")) key_1 = "SOLR";
                                else if (key.contains("SYSTEMML")) key_1 = "SYSTEMDS";

                                if (key!=null) {
                                    if(matcher.group(1)!=null && matcher.group(1).toUpperCase().contains(key)) {
                                        IssueNum += "~" + matcher.group(1);
                                    } else if (matcher.group(0).toUpperCase().contains(key)) {
                                        IssueNum += "~" + matcher.group(0);
                                    }
                                }
                                if (key_1.length() > 2) {
                                    if(matcher.group(1)!=null &&matcher.group(1).toUpperCase().contains(key_1)) {
                                        IssueNum += "~" + matcher.group(1);
                                    } else if (matcher.group(0).toUpperCase().contains(key_1)) {
                                        IssueNum += "~" + matcher.group(0);
                                    }
                                }

                            }
                            break;
                        }
                    }
                } catch (GitAPIException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return IssueNum;
        }

        public void readKeyList () {
            try {
                Pattern pattern = Pattern.compile("(git@|ssh|https://)github.com/()(.*?)$");

                Reader in = new FileReader("/data/CGYW/YW/ACA1/ASTChangeAnalyzer/data/apacheURLList.csv");
//            Reader in = new FileReader("/Users/leechanggong/Projects/ASTChangeAnalyzer/ASTChangeAnalyzer/data/apacheURLList.csv");
                CSVParser parser = CSVFormat.EXCEL.parse(in);
                boolean a = false;

                for (CSVRecord record : parser) {
                    //ArrayList<String> temp = new ArrayList<>();
                    int b = 0;
                    String temp = "";
                    for (String content:record) {

                        if (!a) {
                            a = true;
                            break;

                        }
                        if (b == 1) {
                            temp = content;
                        } else if(b == 2) {
                            keyList.put(content,temp);
                        }
                        b++;
                    }
//                listRead.add(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
