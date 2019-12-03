package com.smartstat.services;

import static com.smartstat.constants.Directions.L;
import static com.smartstat.constants.Directions.R;
import static com.smartstat.constants.Directions.U;

import com.smartstat.constants.Directions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServoService {

  private static final String DIR_ARG = "-D ";
  private static final String PIN_ARG = "-P ";

  private static final int FIRST_SERVO_PIN = 7;
  private static final int SECOND_SERVO_PIN = 11;

  private PythonCmdBuilder pythonCmdBuilder;

  private ShellOutService shellOutService;

  @Value("${servo.script.filename}")
  private String servoScriptFilename;

  @Autowired
  public ServoService(PythonCmdBuilder pythonCmdBuilder, ShellOutService shellOutService) {
    this.pythonCmdBuilder = pythonCmdBuilder;
    this.shellOutService = shellOutService;
  }

  public void toggleFirstServo() {
    shellOutService.runScript(getCmd(U, FIRST_SERVO_PIN));
    shellOutService.runScript(getCmd(L, FIRST_SERVO_PIN));
  }

  public void toggleSecondServo() {
    shellOutService.runScript(getCmd(U, SECOND_SERVO_PIN));
    shellOutService.runScript(getCmd(R, SECOND_SERVO_PIN));
  }

  private String getCmd(Directions direction, int pin) {
    return pythonCmdBuilder.buildCommand(servoScriptFilename, DIR_ARG + direction.name() + " " + PIN_ARG + pin);
  }

}
