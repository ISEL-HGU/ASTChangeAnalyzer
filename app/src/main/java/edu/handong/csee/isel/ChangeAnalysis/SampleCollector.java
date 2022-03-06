package edu.handong.csee.isel.ChangeAnalysis;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Random;

public class SampleCollector {
    private String inputPath;
    private int recordNum;

    public SampleCollector (String inputPath, int recordNum) {
        this.inputPath = inputPath;
        this.recordNum = recordNum;
    }

    public void getSample() {
        Reader in = null;
        Random ran = new Random();
        ArrayList<String> found = new ArrayList<String>();
        try {
            in = new FileReader("");
            CSVParser parser = CSVFormat.EXCEL.parse(in);

            for (CSVRecord record : parser) {
                if(record.size() == Integer.valueOf(recordNum))
                    found.add(record.toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < 10; i++)
        {
            System.out.println(found.get(ran.nextInt(found.size()-1)));
        }
    }
}
