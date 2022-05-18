package edu.handong.csee.isel.ChangeAnalysis;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

public class NumberCounter {
    private String opt;
    private String csvPath;
    private HashMap<String, ArrayList<String>> csv = new HashMap<>();
    private int count;

    public NumberCounter(String path, String opt) {
        this.opt = opt;
        this.csvPath = path;
        readCSV();

        if (this.opt.equals("commit")) {
            readCommit();
            System.out.println(this.count);
        }
    }

    public void readCommit () {
        for (String keys : csv.keySet()) {
            this.count += csv.get(keys).size();
        }
    }
    public void readCSV () {
        //HashMap<String, ArrayList<String>> file = new HashMap<String, ArrayList<String>>();
        ArrayList<String> temp = null;
        String key = "";
        try {
            Reader in = new FileReader(this.csvPath);
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : parser) {
                temp = new ArrayList<String>();
                int i = 0;
                for (String str : record) {
                    if(i != 0) {
                        temp.add(str.replace("]]", "").trim());
                    } else {
                        key = str;
                    }
                    i++;
                }
                csv.put(key,temp);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
