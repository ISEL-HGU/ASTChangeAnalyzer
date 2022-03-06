package edu.handong.csee.isel.ChangeAnalysis;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
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
                if(!sizeList.contains(record.size()))
                    sizeList.add(record.size());
            }
            Collections.sort(sizeList);
            ArrayList<Integer> medianNeighbor = new ArrayList<>();
            int half = sizeList.size()/2;
            int median = sizeList.indexOf(sizeList.size()/2);
            for(int i = half - 10; i < half+10; i++) {
                medianNeighbor.add(sizeList.indexOf(i));
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
            System.out.println(found.size());
            if(found.size()>recordNum) {
                for(int i = 0; i < recordNum; i++)
                {
                    System.out.println(found.get(ran.nextInt(found.size()-1)));
                }
            } else {
                for(String record:found) {
                    System.out.println(record);
                }
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
