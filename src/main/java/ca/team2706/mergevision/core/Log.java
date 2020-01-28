package ca.team2706.mergevision.core;

import ca.team2706.mergevision.main.CLI;

public class Log {
	
	public static void i(String message,boolean logToCLI){
		if(logToCLI){
			CLI.log("Info: "+message);
		}
		System.out.println("Info: "+message);
	}
	public static void v(String message,boolean logToCLI){
		if(logToCLI){
			CLI.log("Verbose: "+message);
		}
		System.out.println("Verbose: "+message);
	}
	public static void e(String message,boolean logToCLI){
		if(logToCLI){
			CLI.log("Error: "+message);
		}
		System.err.println("Error: "+message);
	}
	public static void d(String message,boolean logToCLI){
		if(logToCLI){
			CLI.log("Debug: "+message);
		}
		System.out.println("Debug: "+message);
	}
}
