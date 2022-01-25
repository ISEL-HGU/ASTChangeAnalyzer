package edu.handong.csee.isel.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandLineExecuter {
	
	public static void execute() {
        Process process = null;
        Runtime runtime = Runtime.getRuntime();
        StringBuffer successOutput = new StringBuffer(); // 성공 스트링 버퍼
        StringBuffer errorOutput = new StringBuffer(); // 오류 스트링 버퍼
        BufferedReader successBufferReader = null; // 성공 버퍼
        BufferedReader errorBufferReader = null; // 오류 버퍼
        String msg = null; // 메시지
 
        List<String> cmdList = new ArrayList<String>();
 
        //distinguish Operating System (window, not window then always linux)
        if (System.getProperty("os.name").indexOf("Windows") > -1) {
            cmdList.add("cmd");
            cmdList.add("/c");
        } else {
            cmdList.add("/bin/sh");
            cmdList.add("-c");
        }
        // Setting commands
//        cmdList.add("pip3 install -r /Users/nayeawon/HGU/ISEL/Code/ASTChangeAnalyzer/ASTChangeAnalyzer/app/pythonparser/requirements.txt");
        cmdList.add("/Users/nayeawon/HGU/ISEL/Code/ASTChangeAnalyzer/ASTChangeAnalyzer/app/pythonparser/pythonparser /Users/nayeawon/VisualStudioCode/etc/algo_0118/2.py");
//        cmdList.add("/Users/nayeawon/VisualStudioCode/etc/algo_0118/2.py");
        String[] array = cmdList.toArray(new String[cmdList.size()]);
 
        try {
 
            // Execute the command
            process = runtime.exec(array);
 
            // when shell executes properly
            successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));
 
            while ((msg = successBufferReader.readLine()) != null) {
                successOutput.append(msg + System.getProperty("line.separator"));
            }
 
            // error occurred while executing shell
            errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "EUC-KR"));
            while ((msg = errorBufferReader.readLine()) != null) {
                errorOutput.append(msg + System.getProperty("line.separator"));
            }
 
            // wait until process finish
            process.waitFor();
 
            // when shell execution completes
            if (process.exitValue() == 0) {
                System.out.println("completed");
                System.out.println(successOutput.toString());
            } else {
                // when shell execution fails with exceptions
                System.out.println("failed");
                System.out.println(errorOutput.toString());
            }
 
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                process.destroy();
                if (successBufferReader != null) successBufferReader.close();
                if (errorBufferReader != null) errorBufferReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
