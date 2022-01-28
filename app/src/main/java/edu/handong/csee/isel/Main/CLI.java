package edu.handong.csee.isel.Main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CLI {
	
	private boolean path;
	private boolean inputCsv;
	private boolean lang;
	public boolean isLocal=true;
	private boolean help=false;
	private ArrayList<String> address;
	private String language;

	
	public ArrayList<String> CommonCLI (String[] args) {
		
		Options options = createOptions();
		address = new ArrayList<>();

		if(parseOptions(options, args)){
			
			if (help) {
				printHelp(options);
			}
		}
		return address;
	}
	public ArrayList<String> getAddress() {
		return address;
	}
	public String getLanguage() {
		return language;
	}
	
	public boolean isLocalPath() {
		return isLocal;
	}
	
	private boolean parseOptions(Options options, String[] args) {
		CommandLineParser parser = new DefaultParser();

		try {

			CommandLine cmd = parser.parse(options, args);
			
			path = cmd.hasOption("p");
			if (path)
				address.add(cmd.getOptionValue("p"));

			inputCsv = cmd.hasOption("i");
			if (inputCsv) {
				Utils utils = new Utils();
				address = utils.csvReader(cmd.getOptionValue("i"));
			}

			lang = cmd.hasOption("lang");
			if (lang)
				language = cmd.getOptionValue("lang");

			help = cmd.hasOption("h");

		} catch (Exception e) {
			e.printStackTrace();
			printHelp(options);
			return false;
		}

		return true;
	}
	
	private Options createOptions() {
		
		Options options = new Options();

		options.addOption(Option.builder("p").longOpt("path")
				.desc("Set a path of a directory or a file to display")
				.hasArg()
				.argName("Git repository path")
				.build());
		
		options.addOption(Option.builder("i").longOpt("inputCSV")
				.desc("Set a path of csv file that contains urls")
				.hasArg()
				.argName("csv path")
				.build());
		
		options.addOption(Option.builder("lang").longOpt("language")
				.desc("Set a language of a directory or a file")
				.hasArg()
				.argName("Expected programming language")
				.required()
				.build());
		
		options.addOption(Option.builder("h").longOpt("help")
		        .desc("Help")
		        .build());


		
		return options;
	}
	
	private void printHelp(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		String header = "AST change analyzer";
		String footer ="\nPlease report issues at https://github.com/lifove/CLIExample/issues";
		formatter.printHelp("ASTChangeAnalyzer", header, options, footer, true);
	}
}
