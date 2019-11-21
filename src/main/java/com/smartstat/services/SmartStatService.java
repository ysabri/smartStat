package com.smartstat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmartStatService {

  private ServoService servoService;

  @Autowired
  public SmartStatService(ServoService servoService) {
    this.servoService = servoService;
  }

  public String getTemp() {
    // logic to call python temp script
    return "";
  }

  public void setOn() {
    //logic to call python script with R
    servoService.setRight();
  }

  public void setOff() {
    servoService.setLeft();
    // logic to call python script with L
  }

  public void setTemp(String temp) {
    // logic to set global temp var
  }


}
