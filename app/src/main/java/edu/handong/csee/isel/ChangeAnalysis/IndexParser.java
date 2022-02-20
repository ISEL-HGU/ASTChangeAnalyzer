package edu.handong.csee.isel.ChangeAnalysis;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

public class IndexParser {
    private String path;
    private HashMap<String, HashMap<String, ArrayList<String>>> coreMap;

    public String getPath() { return path; }
    public HashMap<String, HashMap<String, ArrayList<String>>> getCoreMap() { return coreMap; }

    public IndexParser(String path, HashMap<String, HashMap<String, ArrayList<String>>> coreMap) {
        this.path = path + "/index.csv";
        this.coreMap = coreMap;
        File file = new File(this.path);
        if (file.exists()) {
            appendIndex(file);
        } else {
            makeIndex(file);
        }
    }

    public void makeIndex(File file) {

        try {
            FileOutputStream fos = new FileOutputStream(file);
            System.out.println("2");
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

            FileInputStream fis = new FileInputStream(file);

            File outFile = null;


            int i = 1, fileCounter = 0;
            boolean found = false;

            for(String fKey : this.coreMap.keySet()) {
                for(String hKey : this.coreMap.get(fKey).keySet()) {
                    String hashCode = fKey + "-" + hKey;
                    for(String content : this.coreMap.get(fKey).get(hKey)) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                        outFile = new File(path+"/$" +Integer.toString(fileCounter)+ ".tmp");
                        FileOutputStream fos = new FileOutputStream(outFile);
                        PrintWriter out = new PrintWriter(fos);
                        while ((thisLine = in.readLine()) != null) {
                            String [] row = thisLine.split(",");
                            if (row[0].equals(hashCode)) {

                                thisLine = thisLine +","+ content + ",";
                                found = true;
                                break;

                            }
                            out.println(thisLine);
                            if(!found && i == row.length) {
                                i = 1;
                                out.println(hashCode + "," + content + ",");
                                break;
                            }
                            i++;
                        }
                        out.flush();
                        out.close();
                        in.close();
                        fis = new FileInputStream(outFile);
                        System.out.println(Integer.toString(fileCounter)+"@"+Integer.toString(coreMap.size()));
                        if(fileCounter!=609)
                            outFile.delete();
                        fileCounter++;
                    }

                }

            }
            file.delete();
            outFile.renameTo(file);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
