package edu.handong.csee.isel.Main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Utils {
	public ArrayList<String> csvReader(String inputpath) throws IOException {
		ArrayList<String> name_URL = new ArrayList<String>();
		try {
			int URLColumnNumber = 0;
			int ProjectNameColumnNumber = 0;
			Reader in = new FileReader(inputpath);
			//Reader in = new FileReader(new File("").getAbsolutePath() +File.separator +"ProjectList.csv");

			CSVParser parser = CSVFormat.EXCEL.parse(in);

			for (CSVRecord record : parser) {

				if(record.getRecordNumber() == 1)
				{
					for(int i = 0; i < record.size(); i++) {
						if(record.get(i).contains("Github")) {
							URLColumnNumber = i;
						} else if(record.get(i).contains("name")) {
							ProjectNameColumnNumber = i;
						}

					}
				}

				String data = record.get( ProjectNameColumnNumber ) + "@" +record.get( URLColumnNumber );
				System.out.println(data);
				name_URL.add(data);



			}

		} catch( Exception e ){
			e.printStackTrace();
		}
		System.out.println(new File("").getAbsolutePath());
		return name_URL;
	}
}
