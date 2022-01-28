package edu.handong.csee.isel.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandLineExecutor {
	
	private static Process process = null;
    private static Runtime runtime = Runtime.getRuntime();
    private static StringBuffer successOutput;
    private static StringBuffer errorOutput;
    private static BufferedReader successBufferReader = null; // 성공 버퍼
    private static BufferedReader errorBufferReader = null; // 오류 버퍼
    private static String msg = null; // 메시지
    private static List<String> cmdList;
    
    public CommandLineExecutor() {
    	
    	cmdList = new ArrayList<String>();
    	if (System.getProperty("os.name").indexOf("Windows") > -1) {
            cmdList.add("cmd");
            cmdList.add("/c");
        } else {
            cmdList.add("/bin/sh");
            cmdList.add("-c");
        }
    }
	
	public void executeSettings() {
    	
        // Setting commands
			cmdList.add("pip3 install -r " + new File("").getAbsolutePath()
                    + File.separator + "app"
                    + File.separator + "pythonparser"
                    + File.separator + "requirements.txt");
	        String[] array = cmdList.toArray(new String[cmdList.size()]);
	        
	        try {
	            process = runtime.exec(array);
	            
	            successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));
	 
	            while ((msg = successBufferReader.readLine()) != null) {
	            	if (successOutput!=null)
	            		successOutput.append(msg + System.getProperty("line.separator"));
	            }
	 
	            errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "EUC-KR"));
	            while ((msg = errorBufferReader.readLine()) != null) {
	            	if (errorOutput!=null)
	            		errorOutput.append(msg + System.getProperty("line.separator"));
	            }

	            process.waitFor();
	 
	            if (process.exitValue() == 0) {
	                System.out.println("\nSettings Completed\n");
	                if (successOutput!=null)
	                	System.out.println(successOutput.toString());
	                
	            } else {
	                System.out.println("\nSettings Failed\n");
	                if (errorOutput!=null)
	                	System.out.println(errorOutput.toString());
	            }
	            
	        } catch (IOException | InterruptedException e) {
	            e.printStackTrace();
	        }  finally {
	            try {
	                process.destroy();
	                if (successBufferReader != null) successBufferReader.close();
	                if (errorBufferReader != null) errorBufferReader.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	        }
		}
	
	public void executeDeletion(File file) {
        
        // Setting commands
        cmdList.add("rm -rf " + file);
        String[] array = cmdList.toArray(new String[cmdList.size()]);
        
        try {
            process = runtime.exec(array);
            
            successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));
 
            while ((msg = successBufferReader.readLine()) != null) {
            	if (successOutput!=null)
            		successOutput.append(msg + System.getProperty("line.separator"));
            }
            
            errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "EUC-KR"));
            while ((msg = errorBufferReader.readLine()) != null) {
            	if (errorOutput!=null)
            		errorOutput.append(msg + System.getProperty("line.separator"));
            }
            
            process.waitFor();
 
            if (process.exitValue() == 0) {
                System.out.println("\nFile Deletion Completed\n");
                if (successOutput!=null)
                	System.out.println(successOutput.toString());
                
            } else {
                // when shell execution fails with exceptions
                System.out.println("\nFile Deletion Failed\n");
                if (errorOutput!=null)
                	System.out.println(errorOutput.toString());
            }
 
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }  finally {
            try {
                process.destroy();
                if (successBufferReader != null) successBufferReader.close();
                if (errorBufferReader != null) errorBufferReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
	}
	
	public String executeParser(File file) {
        
        // Setting commands
        cmdList.add(new File("").getAbsolutePath() + "/app/cgum/cgum /Users/nayeawon/HGU/ISEL/Code/cgum/standard.h");
        String[] array = cmdList.toArray(new String[cmdList.size()]);
 
        try {
            process = runtime.exec(array);
 
            successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));
 
            while ((msg = successBufferReader.readLine()) != null) {
                if (successOutput!=null)
                    successOutput.append(msg + System.getProperty("line.separator"));
            }
            
            errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "EUC-KR"));
            while ((msg = errorBufferReader.readLine()) != null) {
                if (errorOutput!=null)
                    errorOutput.append(msg + System.getProperty("line.separator"));
            }
            
            process.waitFor();
 
            if (process.exitValue() == 0) {
                System.out.println("Parsing Completed\n");
                if (successOutput!=null)
                	msg = successOutput.toString();
                
            } else {
                System.out.println("Parsing Failed\n");
                if (errorOutput!=null)
                	System.out.println(errorOutput.toString());
                msg = "";
            }
 
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }  finally {
            try {
                process.destroy();
                if (successBufferReader != null) successBufferReader.close();
                if (errorBufferReader != null) errorBufferReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return msg;
    }
}
