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

	public void addCmdList(String cmd) {
		this.cmdList.add(cmd);
	}


	public void executeSettings(String cmd) {
    	
        // Setting commands
            cmdList.add(cmd);
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
	                	System.out.println(successOutput);

	            } else {
	                System.err.println("\nSettings Failed\n");
	                if (errorOutput!=null)
	                	System.out.println(errorOutput);
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
		cmdList.add("rm -rf " + file.getPath());
		String[] array = cmdList.toArray(new String[cmdList.size()]);

		try {
			process = runtime.exec(array);

			successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));

			while ((msg = successBufferReader.readLine()) != null) {
				if (successOutput != null)
					successOutput.append(msg + System.getProperty("line.separator"));
			}

			errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "EUC-KR"));
			while ((msg = errorBufferReader.readLine()) != null) {
				if (errorOutput != null)
					errorOutput.append(msg + System.getProperty("line.separator"));
			}

			process.waitFor();

			if (process.exitValue() == 0) {
				System.out.println("\nFile Deletion Completed\n");
				if (successOutput != null)
					System.out.println(successOutput.toString());

			} else {
				// when shell execution fails with exceptions
				System.out.println("\nFile Deletion Failed\n");
				if (errorOutput != null)
					System.out.println(errorOutput.toString());
			}

		} catch (IOException | InterruptedException e) {
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
	public void executeGraph(String cmd1) {


		// Setting commands
		cmdList.add(cmd1);
		String[] array = cmdList.toArray(new String[cmdList.size()]);
		//String[] array2 = cmdList.toArray(new String[1]);
		try {
			process = runtime.exec(array);
			//System.out.println(array[0]+'|'+array[1]+'|'+array[2]+'|'+array[3]);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((msg = input.readLine()) != null) {
				System.out.println(msg);
				System.out.println("1");
			}
			input.close();
			ProcessBuilder pb = new ProcessBuilder(array[3]);
			pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			pb.redirectError(ProcessBuilder.Redirect.INHERIT);
			Process p = pb.start();
//			successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));
//
//			while ((msg = successBufferReader.readLine()) != null) {
//				if (successOutput!=null)
//					successOutput.append(msg + System.getProperty("line.separator"));
//			}
//
//			errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "EUC-KR"));
//			while ((msg = errorBufferReader.readLine()) != null) {
//				if (errorOutput!=null)
//					errorOutput.append(msg + System.getProperty("line.separator"));
//			}

			process.waitFor();

			if (process.exitValue() == 0) {
				System.out.println("\nSettings Completed\n");
				if (successOutput!=null)
					System.out.println(successOutput);

			} else {
				System.err.println("\nSettings Failed\n");
				if (errorOutput!=null)
					System.out.println(errorOutput);
			}
		System.out.println((successOutput));
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
}
