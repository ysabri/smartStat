package com.smartstat.services;

import static java.lang.Double.parseDouble;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TempService {

  private PythonCmdBuilder pythonCmdBuilder;

  private ShellOutService shellOutService;

  @Value("${dht22.script.filename}")
  private String dht22ScriptFileName;

  @Autowired
  public TempService(PythonCmdBuilder pythonCmdBuilder, ShellOutService shellOutService) {
    this.pythonCmdBuilder = pythonCmdBuilder;
    this.shellOutService = shellOutService;
  }

  public double getTemp() {
    var tempCmd = pythonCmdBuilder.buildCommand(dht22ScriptFileName, "-D T");
    return parseDouble(shellOutService.runScript(tempCmd));
  }

}
