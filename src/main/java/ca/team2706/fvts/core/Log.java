package ca.team2706.fvts.core;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import ca.team2706.fvts.main.CLI;

public class Log {
	
	private static boolean silence = false;
	public static void silence() {
		silence = true;
	}
	public static void unsilence() {
		silence = false;
	}
	
	public static void i(String message,boolean logToCLI){
		if(silence) return;
		if(logToCLI){
			CLI.log("Info: "+message);
		}
		System.out.println("Info: "+message);
	}
	public static void v(String message,boolean logToCLI){
		if(silence) return;
		if(logToCLI){
			CLI.log("Verbose: "+message);
		}
		System.out.println("Verbose: "+message);
	}
	public static void e(String message,boolean logToCLI){
		if(silence) return;
		if(logToCLI){
			CLI.log("Error: "+message);
		}
		System.err.println("Error: "+message);
	}
	public static void d(String message,boolean logToCLI){
		if(silence) return;
		if(logToCLI){
			CLI.log("Debug: "+message);
		}
		System.out.println("Debug: "+message);
	}
	public static void logData(File csvFile, List<String> data) throws Exception {
		if(!csvFile.exists())
			csvFile.createNewFile();
		CSVPrinter printer = new CSVPrinter(new FileWriter(csvFile,true), CSVFormat.DEFAULT);
		printer.printRecord(data);
		printer.close(true);
	}
}
