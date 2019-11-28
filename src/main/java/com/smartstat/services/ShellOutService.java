package com.smartstat.services;

import com.smartstat.constants.Directions;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShellOutService {

  private static final Logger logger = LoggerFactory.getLogger(ShellOutService.class);

  public ShellOutService() {
  }

  public void runScript(String command) {
    try {
      var process = Runtime.getRuntime()
          .exec(command);

      logger.debug("Shelling out using command {} ", command);

      var inputGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
      Executors.newSingleThreadExecutor()
          .submit(inputGobbler);

      var errorGobbler = new StreamGobbler(process.getErrorStream(), System.out::println);
      Executors.newSingleThreadExecutor()
          .submit(errorGobbler);

      process.waitFor();
    } catch (Exception e) {
      logger.error("Shelling out failed with exception: {}", e.getMessage());
    }
  }

  private static class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
      this.inputStream = inputStream;
      this.consumer = consumer;
    }

    @Override
    public void run() {
      new BufferedReader(new InputStreamReader(inputStream)).lines()
          .forEach(consumer);
    }

  }

}
