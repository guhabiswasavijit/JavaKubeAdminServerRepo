package org.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private File outputFile;
    private InputStream errorStream;

    public StreamGobbler(InputStream inputStream,File i_outputFIle,InputStream i_errorStream) {
        this.inputStream = inputStream;
        this.outputFile = i_outputFIle;
        this.errorStream = i_errorStream;
    }

    @Override
    public void run() {
    	final StringBuilder outputJson = new StringBuilder();  
    	final StringBuilder errorLog = new StringBuilder(); 
        try (BufferedReader error = new BufferedReader(new InputStreamReader(errorStream));
        	 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        	 BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
	        	
        	reader.lines().forEach(line -> {
        		outputJson.append(line);
        	});
        	error.lines().forEach(line -> {
        		errorLog.append(line);
        	});
        	log.info("Got output from process {}",outputJson.toString());
            writer.write(outputJson.toString());
            writer.flush();
        } catch (IOException ex) {
        	log.info("Got error from process {}",errorLog.toString());
			log.error("Error writing command output {}",ex);
		}
    }
}