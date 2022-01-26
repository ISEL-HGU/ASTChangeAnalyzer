package edu.handong.csee.isel.Main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CLI {
	
	private boolean path;
	private boolean url;
	private boolean lang;
	public boolean isLocal=true;
	private boolean help=false;
	public boolean input=false;
	private String address = "";
	private String language;
	private String inputCsv;
	
	public String CommonCLI (String[] args) {
		
		Options options = createOptions();
		
		if(parseOptions(options, args)){
			
			if (help) {
				printHelp(options);
			}
		}
		return address;
	}
	public String getInputCsv() {
		return inputCsv;
	}
	public String getAddress() {
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
				address = cmd.getOptionValue("p");
			
			url = cmd.hasOption("u");
			if (url)
				address = cmd.getOptionValue("u");
			
			lang = cmd.hasOption("lang");
			if (lang)
				language = cmd.getOptionValue("lang");
			input = cmd.hasOption("i");
			if (input)
				inputCsv = cmd.getOptionValue("i");
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
		
		options.addOption(Option.builder("u").longOpt("url")
				.desc("Set a url of a Git respository")
				.hasArg()
				.argName("Git repository url")
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

		options.addOption(Option.builder("i").longOpt("input")
				.hasArg()
				.argName("inputcsv")
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
