package Main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {
	public Options options;
	public String CommonCLI(String[] args) {
		options = new Options();
		
		Option help = Option.builder("h")
				.longOpt("help")
				.desc("").build();
		options.addOption(help);
		
		Option url = Option.builder("u").longOpt("url")
				.argName("url")
				.hasArg(true)
				.required(true)
				.desc("set github url").build();
		options.addOption(url);
		
		CommandLineParser cli_parser = new DefaultParser();
        try {
        	CommandLine line = cli_parser.parse(options, args);
        	if (line.hasOption("h")) {
        		HelpFormatter formatter = new HelpFormatter();
        		formatter.printHelp("ASTChangeAnalyzer", options);
        	}
        	else if (line.hasOption("u")) {
        		return(line.getOptionValue("u"));
        	}
        	else {
        		System.out.println("unknown option: " );
                System.out.println();
            }
        	
        } catch (ParseException e) {
        	System.err.println(e.getMessage());
        }
		return null;
	}
}
