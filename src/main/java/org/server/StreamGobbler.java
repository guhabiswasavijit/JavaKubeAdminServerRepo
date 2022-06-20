package org.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class StreamGobbler implements Runnable {
    private InputStream inputStream;

    public StreamGobbler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
    	final StringBuilder outputJson = new StringBuilder();
        new BufferedReader(new InputStreamReader(inputStream)).lines()
          .forEach(line ->{
        	  outputJson.append(line);
          });
        log.info("Got output from process {}",outputJson.toString());
    }
}