package com.ftpclient.util.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * Log manager, loguje greske u fajl
 * 
 * @author Djordje Velickovic
 *
 */
public class LogManager {

	private static LogType logType = LogType.StackTrace;

	public static LogType getLogType() {
		return logType;
	}
	
	public synchronized static void setLogType(LogType logType) {
		LogManager.logType = logType;
	}
	
	private static String logSeparator = "------------------------------------------------------------------";

	private static String logException = "%s"+System.lineSeparator()+"%s"+System.lineSeparator()+"%s"+System.lineSeparator();
	private static String content = "Stack Trace: %s";
	private static String headerFormat = "Error: %s"+System.lineSeparator()+"Method: %s"+System.lineSeparator()+"Class: %s";
	private static String footerFormat = "Date: %s"+System.lineSeparator()+logSeparator;



	private static LogWriter logWriter = new LogWriter();


	/**
	 * Belezi log
	 * 
	 * @param e
	 * @param message
	 */
	public static synchronized void addLog(Exception e, String message) {
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		
		switch (logType) {
			case StackTrace:
				String stackTrace = "";
				try(StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw)) {
					e.printStackTrace(pw);
					stackTrace = sw.toString();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				

				logWriter.write(
						String.format(
								logException,
								String.format(
										headerFormat,
										message,
										e.getStackTrace()[0].getMethodName(),
										e.getStackTrace()[0].getClassName()),
										String.format(content, stackTrace),
										String.format(footerFormat, dateFormat.format(date))
										)
						);
				break;
	
			case ErrorMessage:
				logWriter.write("[ "+dateFormat.format(date)+" ] Error: "+message);
				break;
			default:
		}
		
		
	}

}