package com.smartstat.services;

import static com.smartstat.constants.Directions.L;
import static com.smartstat.constants.Directions.R;

import com.smartstat.Exceptions.ShellingOutFailed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServoService {

  private static final String DIR_ARG = "-D ";

  private PythonCmdBuilder pythonCmdBuilder;

  private ShellOutService shellOutService;

  @Value("${servo.script.filename}")
  private String servoScriptFilename;

  @Autowired
  public ServoService(PythonCmdBuilder pythonCmdBuilder, ShellOutService shellOutService) {
    this.pythonCmdBuilder = pythonCmdBuilder;
    this.shellOutService = shellOutService;
  }

  public void setRight() {
    var rightDirCmd = pythonCmdBuilder.buildCommand(servoScriptFilename, DIR_ARG + R.name());
    shellOutService.runScript(rightDirCmd);
  }

  public void setLeft() {
    var leftDirCmd = pythonCmdBuilder.buildCommand(servoScriptFilename, DIR_ARG + L.name());
    shellOutService.runScript(leftDirCmd);
  }

}
