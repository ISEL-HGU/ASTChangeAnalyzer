package edu.handong.csee.isel.ChangeAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class IndexParser {
    private String path;
    private HashMap<String, HashMap<String, ArrayList<String>>> coreMap;

    public String getPath() { return path; }
    public HashMap<String, HashMap<String, ArrayList<String>>> getCoreMap() { return coreMap; }

    public IndexParser(String path, HashMap<String, HashMap<String, ArrayList<String>>> coreMap) {
        this.path = path;
        this.coreMap = coreMap;
        File file = new File(this.path + "/index.csv");
        if (file.exists()) {
            appendIndex(file);
        } else {
            makeIndex(file);
        }
    }

    public void makeIndex(File file) {

        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter out = new PrintWriter(fos);

            for(String fKey : this.coreMap.keySet()) {
                for(String hKey : this.coreMap.get(fKey).keySet()) {
                    out.print(fKey + "-" + hKey + ",");
                    for(String content : this.coreMap.get(fKey).get(hKey)) {
                        out.print(content + ",");
                    }
                    out.print("\n");
                }
            }


            out.flush();
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void appendIndex(File file) {
        String thisLine = "";

        try {
            File outFile = null;
            FileInputStream fis;

            int fileCounter = 0;
            boolean found = false, found2 = false;

            for(String fKey : this.coreMap.keySet()) {
                for(String hKey : this.coreMap.get(fKey).keySet()) {
                    String hashCode = fKey + "-" + hKey;
                    for(String content : this.coreMap.get(fKey).get(hKey)) {
                        if(fileCounter == 0)
                            fis = new FileInputStream(file);
                        else
                            fis = new FileInputStream(outFile);
                        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                        outFile = new File(path+"/$" +Integer.toString(fileCounter)+ ".tmp");
                        FileOutputStream fos = new FileOutputStream(outFile);
                        PrintWriter out = new PrintWriter(fos);

                        while ((thisLine = in.readLine()) != null) {
                            String [] row = thisLine.split(",");
                            if (row[0].equals(hashCode)) {
                                thisLine = thisLine + content + ",";
                                found = true;
                            }
                            out.println(thisLine);
                        }
                        if(!found)
                            out.println(hashCode + "," + content + ",");
                        found = false;
                        out.flush();
                        out.close();
                        in.close();
                        fis = new FileInputStream(outFile);
                        fileCounter++;
                    }
                }
            }
            for(int z = 0; z < fileCounter-1; z++) {
                new File(path+"/$"+Integer.toString(z)+".tmp").delete();
            }
            file.delete();
            outFile.renameTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sortIndex(file);
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
}
