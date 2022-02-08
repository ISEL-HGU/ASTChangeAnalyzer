package edu.handong.csee.isel.Main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CLI {
	
	private boolean path;
	private boolean lev;
	private boolean help=false;
	private ArrayList<String> address;
	private String language;
	private String DiffTool;
	private String optionValueP;

	
	public ArrayList<String> CommonCLI (String[] args) {
		Options options = createOptions();
		address = new ArrayList<String>();
		if(parseOptions(options, args)){
			if (help) {
				printHelp(options);
			}
		}
		return address;
	}

	public String getLanguage() {
		return language;
	}
	public boolean getLevel() { return lev; }
	public String getDiffTool() { return DiffTool; }
	public String getOptionValueP() { return optionValueP; }
	
	private boolean parseOptions(Options options, String[] args) {
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);

			path = cmd.hasOption("p");
			if (path) {
				optionValueP = cmd.getOptionValue("p");
				if (optionValueP.endsWith(".csv")) {
					Utils utils = new Utils();
					address = utils.csvReader(optionValueP);
				} else if (optionValueP.startsWith("https")) {
					address.add(optionValueP);
				} else {
					try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(optionValueP))) {
						for (Path path : stream) {
							if (Files.isDirectory(path)) {
								address.add(path.getFileName().toString());
							}
						}
					}
				}
			}

			if (cmd.hasOption("python")) language = "PYTHON";
			else if (cmd.hasOption("c")) language = "C";
			else language = "JAVA";

			if (cmd.hasOption("java"))
				DiffTool = cmd.getOptionValue("java").toUpperCase();

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
				.argName("Local path")
				.build());
		
		options.addOption(Option.builder("java").longOpt("java")
				.desc("Set a language of a directory or a file")
				.hasArg()
				.argName("Expected programming language")
				.build());

		options.addOption(Option.builder("python").longOpt("python")
				.desc("Set a language of a directory or a file")
				.argName("Expected programming language")
				.build());

		options.addOption(Option.builder("c").longOpt("c")
				.desc("Set a language of a directory or a file")
				.argName("Expected programming language")
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
