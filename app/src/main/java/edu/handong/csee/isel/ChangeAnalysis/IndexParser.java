package edu.handong.csee.isel.ChangeAnalysis;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class IndexParser {
    private String path;
    private HashMap<String, ArrayList<String>> hashMap;

    public IndexParser(String path, HashMap<String, ArrayList<String>> hashMap) {
        this.path = path;
        this.hashMap = hashMap;
    }


    public void generateIndex() {
        File file = new File(this.path + "/index.csv");
        //File file = new File(this.path + "/merge.csv");
        if (file.exists()) {
            appendIndex(file);
        } else {
            makeIndex(file);
        }
        if (file.exists())
            sortIndex(file);
    }

    public void makeIndex(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter out = new PrintWriter(fos);
            for (String key : hashMap.keySet()) {
                out.print(key);
                for (String contents : hashMap.get(key)){
                    out.print("," + contents);
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

    public void makeIndex(File file, HashMap<String,ArrayList<String>> map) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter out = new PrintWriter(fos);

            for (String key : map.keySet()) {
                out.print(key);
                for (String contents : map.get(key))
                    out.print("," + contents);
                out.print("\n");
            }
            out.flush();
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void appendIndex(File file) {

        HashMap<String, ArrayList<String>> csvMap = new HashMap<String, ArrayList<String>>();
//        String thisLine = "";
//
        try {
//            File outFile = null;
            Reader in = new FileReader(file);
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : parser) {
                ArrayList<String> temp = new ArrayList<String>();
                if (csvMap.containsKey(record.get(0))) {
                    csvMap.get(record.get(0)).add(record.get(1));
                } else {
                    try {
                        temp.add(record.get(1));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println(record);
                        System.out.println("ArrayIndexOutOfBoundsException occured, " + record.get(0));
                        continue;
                    }
                    csvMap.put(record.get(0),temp);
                }

                for (int i = 2; i < record.size(); i++) {
                    if (!record.get(i).equals(record.get(i-1)))
                        csvMap.get(record.get(0)).add(record.get(i));
                }
            }
            in.close();


        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

        File outFile =  new File(this.path + "/temp.csv");
        makeIndex(outFile ,merge(csvMap,hashMap));
        file.delete();
        outFile.renameTo(file);
    }





    public void sortIndex(File file) {
        String thisLine = "";
        try {
            File outFile = new File(path + "/temp.tmp");
            FileInputStream fis;
            fis = new FileInputStream(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            FileOutputStream fos = new FileOutputStream(outFile);
            PrintWriter out = new PrintWriter(fos);
            ArrayList<String> lines = new ArrayList<String>();
            while ((thisLine = in.readLine()) != null) {
                lines.add(thisLine);
            }
            Collections.sort(lines);
            for(String line : lines) {
                out.println(line);
            }
            out.flush();
            out.close();
            in.close();
            file.delete();
            outFile.renameTo(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, ArrayList<String>> merge(HashMap<String, ArrayList<String>> list_1, HashMap<String, ArrayList<String>> list_2) {
        HashMap<String, ArrayList<String>> l1 = new HashMap<>();
        l1.putAll(list_1);
        for (String keys_2 : list_2.keySet()) {
            if (l1.containsKey(keys_2)) l1.get(keys_2).addAll(list_2.get(keys_2));
            else l1.put(keys_2, list_2.get(keys_2));
        }
//        for (String keys_1 : list_1.keySet()) {
//            for (String keys_2 : list_2.keySet()) {
//                if(keys_1.equals(keys_2)) {
//                    l1.get(keys_1).addAll(list_2.get(keys_2));
//                    System.out.println(keys_1);
//                }
//                else
//                    l1.put(keys_2,list_2.get(keys_2));
//            }
//        }
        if (list_1.size() ==0)
            return list_2;
        return l1;
    }
}
