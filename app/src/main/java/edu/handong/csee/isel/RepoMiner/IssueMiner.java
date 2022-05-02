package edu.handong.csee.isel.RepoMiner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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

    public IssueMiner(String path) {
        makeIssueIndex(readIndex(path),path);
    } 

    public HashMap<String, ArrayList<String>> readIndex (String indexPath) {
        HashMap<String, ArrayList<String>> file = new HashMap<String, ArrayList<String>>();
        ArrayList<String> temp = null;
        String key = "";
        try {
            Reader in = new FileReader(indexPath);
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : parser) {
                for (String str : record) {
                    temp = new ArrayList<String>();
                    if(str.contains("~")) {
                        temp.add(str);
                    } else {
                        key = str;
                    }
                }
                file.put(key,temp);
            }
            } catch (IOException e) {
            e.printStackTrace();}


        return file;
    }

    public void makeIssueIndex(HashMap<String, ArrayList<String>> map, String path) {
        String newPath = path.replace(".csv","_issue.csv");

        for (String key : map.keySet()) {
            for (String contents : map.get(key)) {
                String [] temp = contents.split("&");
                getIssueNum(temp[0].replace("~","/").trim(),temp[1]);
            }
        }



//        try {
//            FileOutputStream fos = new FileOutputStream(newPath);
//            PrintWriter out = new PrintWriter(fos);
//
//            for (String key : map.keySet()) {
//                out.print(key);
//                for (String contents : map.get(key)) {
//                    out.print("," + contents + getIssueNum(contents.split("&")[1]));
//                }
//
//                out.print("\n");
//            }
//            out.flush();
//            out.close();
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public String getIssueNum (String projectName, String ID) {
        String IssueNum = "";

        Pattern pattern = Pattern.compile("(#\\d+)");
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
        System.out.println(projectName + "~" + ID + ":" + IssueNum);
        return IssueNum;
    }
}
