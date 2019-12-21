package com.smartstat.services;

import org.springframework.stereotype.Service;

@Service
public class PythonCmdBuilder {

  private static final String PYTHON_SCRIPTS_DIR = System.getProperty("user.dir") + "/pyScripts/";
  private static final String SUDO_PYTHON = "sudo python " + PYTHON_SCRIPTS_DIR;

  public String buildCommand(String fileName) {
    return SUDO_PYTHON + fileName;
  }

  public String buildCommand(String fileName, String args) {
    return buildCommand(fileName) + " " + args;
  }

}

