package com.smartstat.services;

import static com.smartstat.constants.Directions.L;
import static com.smartstat.constants.Directions.R;

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
public class ServoService {

  private static final Logger logger = LoggerFactory.getLogger(ServoService.class);
  private static final String HOME_DIRECTORY = System.getProperty("user.home");
  private final String directionCommand = "sh -c python " + HOME_DIRECTORY + "/Desktop/test.py -D ";

  public ServoService() {
  }

  public void setRight() {
    runScript(R);
  }

  public void setLeft() {
    runScript(L);
  }

  private void runScript(Directions direction) {
    try {
      var process = Runtime.getRuntime()
          .exec(directionCommand + direction.name());
      StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
      Executors.newSingleThreadExecutor()
          .submit(streamGobbler);
      int exitCode = process.waitFor();
      assert exitCode == 0;
    } catch (Exception e) {
      logger.debug("change direction failed with exception: {}", e.getMessage());
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
