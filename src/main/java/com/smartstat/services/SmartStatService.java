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
    servoService.setRight();
  }

  public void setOff() {
    servoService.setLeft();
  }

  public void setTemp(String temp) {
    // logic to set global temp var
  }


}
