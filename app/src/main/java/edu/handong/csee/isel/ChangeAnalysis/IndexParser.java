package edu.handong.csee.isel.ChangeAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class IndexParser {
    private String path;
    private HashMap<String, ArrayList<String>> hashMap;

    public String getPath() { return path; }

    public IndexParser(String path, HashMap<String, ArrayList<String>> hashMap) {
        this.path = path;
        this.hashMap = hashMap;
    }

    public void makeIndex(File file) {

        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter out = new PrintWriter(fos);

            for(String key : hashMap.keySet()) {
                out.print(key);
                for(String contents : hashMap.get(key))
                    out.print(","+hashMap.get(key));
                out.print("\n");
            }
            out.flush();
            out.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void generateIndex() {
        File file = new File(this.path + "/index.csv");
        if (file.exists()) {
            appendIndex(file);
        } else {
            makeIndex(file);
        }
        if(file.exists())
            sortIndex(file);
    }

    public void appendIndex(File file) {
        String thisLine = "";

        try {
            File outFile = null;
            FileInputStream fis;

            int fileCounter = 0;
            boolean found = false;

            for(String key : this.hashMap.keySet()) {
                for(String contents : this.hashMap.get(key)) {
                    if(fileCounter == 0)
                        fis = new FileInputStream(file);
                    else
                        fis = new FileInputStream(outFile);
                    BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                    outFile = new File(path+"/$" +Integer.toString(fileCounter)+ ".tmp");
                    FileOutputStream fos = new FileOutputStream(outFile);
                    PrintWriter out = new PrintWriter(fos);

                    while ((thisLine = in.readLine()) != null) {
                        try {
                            String [] row = thisLine.split(",");
                            if (row[0].equals(key)) {
                                thisLine = thisLine + contents + ",";
                                found = true;
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                        out.println(thisLine);
                    }
                    if(!found)
                        out.println(key + "," + contents + ",");
                    found = false;
                    try {
                        out.flush();
                        out.close();
                        in.close();
                        fis = new FileInputStream(outFile);
                        fileCounter++;
                    } catch (FileNotFoundException e ) {
                        continue;
                    }

                }
            }

            try {
                if(outFile == null)
                    outFile = file;
                file.delete();
                outFile.renameTo(file);
                for(int z = 0; z < fileCounter-1; z++) {
                    new File(path + "/$" + Integer.toString(z) + ".tmp").delete();
                }
            } catch (NullPointerException e) {
                System.out.println("There is a problem with a repo. Skipping indexing");
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

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
