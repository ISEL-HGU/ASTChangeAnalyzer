package edu.handong.csee.isel.ChangeAnalysis;

import edu.handong.csee.isel.Main.CommandLineExecutor;
import org.checkerframework.checker.units.qual.C;

public class GraphGenerator {

    public GraphGenerator() {
        CommandLineExecutor cli = new CommandLineExecutor();
        cli.addCmdList("pip install plotext");
        cli.executeGraph("python3 /data/CGYW/ASTChangeAnalyzer/app/src/main/java/edu/handong/csee/isel/ChangeAnalysis/graph.py");
        //cli.executeGraph("python3 graph.py");
    }

}
