package edu.handong.csee.isel.RepoMiner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.SerializationUtils;
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
    private HashMap<String, ArrayList<String>> map = new HashMap<>();
    private HashMap<String, ArrayList<String>> projectList = new HashMap<>();

    public IssueMiner(String path, String readNum) {
        int [] nums = new int [2];
        int i = 0;
        for(String x : readNum.split(","))
            nums[i++] = Integer.parseInt(x.trim());

        readPartial(path,nums[0],nums[1]);
        makeIssueIndex(path, nums[0], nums[1]);
        mapToCsv(path,projectList,nums[0],nums[1]);
//        readIndex(path);
//        mapToCsv(path,takeOneWithIssues());

//        makeIssueIndex(takeOneWithIssues(readIndex(path)),path);
//        System.out.println("Result:" + "\n" + "Total Changes - " + total
//                + "\n" + "Changes with issues - " + withIssue
//                + "\n" + "Proportion - " + withIssue/total);
    }

    public void readPartial (String indexPath, int from, int to) {
        ArrayList<String> temp = null;
        String key = "";
        int cnt = 0;
        try {
            Reader in = new FileReader(indexPath);
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : parser) {
                if (cnt < from) continue;
                else if (cnt > to) break;
                cnt++;
                temp = new ArrayList<String>();
                for (String str : record) {
                    if(str.contains("~")) {
                        temp.add(str.replace("]]", "").trim());
                    } else {
                        key = str;
                    }
                }
                map.put(key,temp);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();}
    }

    public void readIndex (String indexPath) {
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
                    map.put(key,temp);
                }
                in.close();
            } catch (IOException e) {
            e.printStackTrace();}



    }

    public void makeIssueIndex(String path, int from, int to) {
        String newPath = path.replace(".csv", from + "~" + to+".csv");

        try {
            FileOutputStream fos = new FileOutputStream(newPath);
            PrintWriter out = new PrintWriter(fos);

            for (String key : map.keySet()) {
                if(map.get(key).size() == 0)
                    continue;
                out.print(key);
                for (String contents : map.get(key)) {
                    String [] temp = contents.split("&");
                    String issueNum = getIssueNum(temp[0].replace("~","/").trim(),temp[1].trim());
                    if (issueNum == null)
                        continue;
                    out.print("," + contents.trim() + issueNum);
                    countIssuePerProject(temp[0],issueNum);
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

    public void countIssuePerProject (String projectName, String issueNum) {
        String [] arr = issueNum.split("~");

        if (projectList.containsKey(projectName.trim())) {
            for (String x : arr)
                projectList.get(projectName).add(x);
        } else {
            ArrayList<String> temp = new ArrayList<>();
            for (String x : arr)
                temp.add(x);
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

    public void mapToCsv (String path, HashMap<String, ArrayList<String>> temp, int from, int to) {
//        String newPath = path.replace(".csv","_issue.csv");
        String newPath = path.replace(".csv", from + "~" + to +"_issuePerProject.csv");
        try {
            FileOutputStream fos = new FileOutputStream(newPath);
            PrintWriter out = new PrintWriter(fos);

            for (String key : temp.keySet()) {
                out.print(key);
                for (String contents : temp.get(key)) {

                    out.print("," + contents.trim());
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

    public String getIssueNum (String projectName, String ID) {
        String IssueNum = "";

//        Pattern pattern = Pattern.compile("(#\\d+)");
        Pattern pattern = Pattern.compile("([a-zA-Z]+-\\d+)");
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
                        while(matcher.find()) {;
                            if(matcher.group(1).toUpperCase().contains(projectName.toUpperCase()))
                                IssueNum += "~" + matcher.group(1);
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
}
