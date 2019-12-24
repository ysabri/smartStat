package com.smartstat.services;

import com.smartstat.exceptions.ShellingOutFailed;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShellOutService {

  private static final Logger logger = LoggerFactory.getLogger(ShellOutService.class);

  public ShellOutService() {
  }

  public String runScript(String command) throws ShellingOutFailed {
    try {
      var process = Runtime.getRuntime()
          .exec(command);

      logger.debug("Shelling out using command {} ", command);

      AtomicReference<String> output = new AtomicReference<>("");
      Consumer<String> captureOutput = output::set;

      var inputGobbler = new StreamGobbler(process.getInputStream(), captureOutput);

      Executors.newSingleThreadExecutor()
          .submit(inputGobbler);

      var errorGobbler = new StreamGobbler(process.getErrorStream(), System.out::println);
      Executors.newSingleThreadExecutor()
          .submit(errorGobbler);

      // this will pause the thread until the process terminates, so output is guaranteed to have a value
      process.waitFor();

      return output.get();
    } catch (Exception e) {
      logger.error("Shelling out failed with exception: {}", e.getMessage());
      throw new ShellingOutFailed(e);
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
      try (var ioStream = new BufferedReader(new InputStreamReader(inputStream))) {
        ioStream.lines()
            .forEach(consumer);
      } catch (IOException e) {
        logger.error("consuming io stream failed: {}", e.getMessage());
      }
    }

  }

}
