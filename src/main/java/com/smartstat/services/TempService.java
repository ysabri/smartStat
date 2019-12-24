package com.smartstat.services;

import static java.lang.Double.parseDouble;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TempService {

  private PythonCmdBuilder pythonCmdBuilder;

  private ShellOutService shellOutService;

  @Value("${ds18b20.script.filename}")
  private String DS18B20ScriptFileName;

  @Autowired
  public TempService(PythonCmdBuilder pythonCmdBuilder, ShellOutService shellOutService) {
    this.pythonCmdBuilder = pythonCmdBuilder;
    this.shellOutService = shellOutService;
  }

  public synchronized double getTemp() {
    var tempCmd = pythonCmdBuilder.buildCommand(DS18B20ScriptFileName);
    return parseDouble(shellOutService.runScript(tempCmd));
  }

}
