package common;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerFactory {

	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT %4$s %2$s] %5$s%6$s%n");
		// LogManager.getLogManager().reset();
	}
	

	public static Logger createLogger(String logFolder, String identifier) throws Exception {
		return createLogger(logFolder, identifier, identifier);
	}
	
	public static Logger createLogger(String logFolder, String file, String identifier) throws Exception {
		
    	new File("./" + logFolder).mkdirs();
    	
    	String logFilePath = String.format("./%s/%s.log", logFolder, file);
    	Logger logger = Logger.getLogger(identifier);
    	
    	FileHandler fileHandler = new FileHandler(logFilePath);
    	fileHandler.setFormatter(new SimpleFormatter());
		fileHandler.setLevel(Level.ALL);
		
		logger.addHandler(fileHandler);
		logger.setLevel(Level.ALL);
    	
        return logger;
	}
	
}
