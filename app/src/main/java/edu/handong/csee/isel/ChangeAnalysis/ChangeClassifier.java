package edu.handong.csee.isel.ChangeAnalysis;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
;

public class ChangeClassifier {

	public ArrayList<String> csvReader() throws IOException {
		Reader in = new FileReader("path/to/file.csv");
		
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
		
		for (CSVRecord record : records) {
		    String lastName = record.get("Last Name");
		    String firstName = record.get("First Name");
	}
		return null;
	}
}
