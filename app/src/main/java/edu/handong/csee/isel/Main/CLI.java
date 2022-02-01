package edu.handong.csee.isel.Main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class CLI {
	
	private boolean path;
	private boolean inputCsv;
	private boolean lang;
	private boolean lev;
	private boolean help=false;
	private ArrayList<String> address;
	private String language;
	private String DiffTool;

	
	public ArrayList<String> CommonCLI (String[] args) {
		
		Options options = createOptions();
		address = new ArrayList<String>();

		if(parseOptions(options, args)){
			
			if (help) {
				printHelp(options);
			}
			if (lang) {
				Scanner scanner = new Scanner(System.in);
				System.out.print("\nChoose the differencing tool for Java :\n"
						+ "  1: gumtree (https://github.com/GumTreeDiff/gumtree)\n"
						+ "  2: gumtree-spoon (https://github.com/SpoonLabs/gumtree-spoon-ast-diff)\n"
						+ "  3: LAS (https://github.com/thwak/LAS)\n"
						+ "Enter selection (default: gumtree) [1..3] ");
				int opt = scanner.nextInt();
				switch (opt) {
					case 2:
						DiffTool = "SPOON";
						break;
					case 3:
						DiffTool = "LAS";
						break;
					default:
						DiffTool = "GUMTREE";
						break;
				}
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
	public boolean getLevel() { return lev; }
	public String getDiffTool() { return DiffTool; }
	
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

			lev = cmd.hasOption("level");

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

		options.addOption(Option.builder("level").longOpt("level")
				.desc("Set a level of data classification")
				.argName("file or hunk")
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
