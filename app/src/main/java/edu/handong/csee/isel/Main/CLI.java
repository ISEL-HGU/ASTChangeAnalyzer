package edu.handong.csee.isel.Main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CLI {
	private boolean help=false;
	private boolean thread = true;
	private boolean changeCount;
	private boolean save;
	private boolean gitClone;
	private boolean readBinary;
	private String issueMine;
	private ArrayList<String> address;
	private String language;
	private String DiffTool;
	private String inputPath;
	private String chgPath;
	private String indexPath;
	private String hashcode;
	private String count;
	private Utils utils;

	
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

	public String getLanguage() { return language; }
	public String getDiffTool() { return DiffTool; }
	public String getInputPath() { return inputPath; }
	public String getChgPath() { return chgPath; }
	public String getIndexPath() { return indexPath; }
	public String getHashcode() { return hashcode; }
	public boolean activateThread() {return thread; }
	public boolean isChangeCount() { return changeCount; }
	public boolean isGitClone() { return gitClone; }
	public boolean isReadBinary() { return readBinary; }
	public String getIssueMine() { return issueMine; }
	public Utils getUtils() { return utils; }
	public String getCount() { return count;}
	
	private boolean parseOptions(Options options, String[] args) {
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("rb")) {
				chgPath = cmd.getOptionValue("rb");
				readBinary = true;
				return true;
			}
			if (cmd.hasOption("p")) {
				inputPath = cmd.getOptionValue("p");
				if (inputPath.startsWith("https")) {
					address.add(inputPath);
					thread = false;
				} else {
					if (inputPath.endsWith(".csv")) {
						utils = new Utils();
						address = utils.csvReader(inputPath);
					}
					else {
						address.add(inputPath);
//						try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(inputPath))) {
//							for (Path path : stream) {
//								if (Files.isDirectory(path)) {
//									address.add(inputPath + "/" + path.getFileName().toString());
//								}
//							}
//						}
					}
				}
			}
			if (cmd.hasOption("python")) language = "PYTHON";
			else if (cmd.hasOption("c")) language = "C";
			else language = "JAVA";

			if (cmd.hasOption("java"))
				DiffTool = cmd.getOptionValue("java").toUpperCase();

			changeCount = cmd.hasOption("changeCount");

			gitClone = cmd.hasOption("gitClone");

			save = cmd.hasOption("save");
			if (save)
				chgPath = cmd.getOptionValue("save");
			else chgPath = "/data/CGYW/chg";

			if (cmd.hasOption("sample"))
				indexPath = cmd.getOptionValue("sample");
			else indexPath = "/data/CGYW/chg/index.csv";

			if (cmd.hasOption("hashcode"))
				hashcode = cmd.getOptionValue("hashcode");
			if (cmd.hasOption("issueMine"))
				issueMine = cmd.getOptionValue("issueMine");
			if (cmd.hasOption("count"))
				count = cmd.getOptionValue("count");

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
				.desc("Set a path of a directory of a cloned project, a URL, or a path with a csv file")
				.hasArg()
				.argName("Local path")
				.build());
		
		options.addOption(Option.builder("java")
				.desc("LAS or GumTree")
				.hasArg()
				.argName("Code differencing tool")
				.build());

		options.addOption(Option.builder("python")
				.desc("Set a language of a directory or a file")
				.argName("Expected programming language")
				.build());

		options.addOption(Option.builder("c")
				.desc("Set a language of a directory or a file")
				.argName("Expected programming language")
				.build());

		options.addOption(Option.builder("gitClone")
				.desc("Clone a project from a path provided at -p option")
				.argName("URL path")
				.build());

		options.addOption(Option.builder("rb").longOpt("readBin")
				.desc("read a binary file")
				.argName("binary path")
				.build());

		options.addOption(Option.builder("changeCount")
				.desc("Count the number of total changes of a project")
				.argName("Number of Change")
				.build());

		options.addOption(Option.builder("save")
				.desc("Set a path to .chg, default: /data/CGYW/chg. This option provides a index.csv, Statistics.txt, and .chg binary file")
				.hasArg()
				.argName("Expected absolute path")
				.build());

		options.addOption(Option.builder("sample")
				.desc("get 20 samples group sized as median from index.csv")
				.hasArg()
				.argName("path for index.csv")
				.build());

		options.addOption(Option.builder("hashcode")
				.desc("get scope of given hashcode")
				.hasArg()
				.argName("computed hashcode")
				.build());

		options.addOption(Option.builder("issueMine")
				.desc("generates index_issue that concats issue number of each elements commit to the original data")
				.hasArg()
				.argName("num,num")
				.build());

		options.addOption(Option.builder("count")
				.desc("count commit, change, and lines")
				.hasArg()
				.argName("what do you want to count?")
				.build());
		
		options.addOption(Option.builder("h").longOpt("help")
		        .desc("Help")
		        .build());

		return options;
	}
	
	private void printHelp(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		String header = "AST Change Analyzer";
		String footer ="\nPlease report issues at https://github.com/ISEL-HGU/ASTChangeAnalyzer";
		formatter.printHelp("ASTChangeAnalyzer", header, options, footer, true);
	}
}
