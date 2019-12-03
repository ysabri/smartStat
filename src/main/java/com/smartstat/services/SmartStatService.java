package com.smartstat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmartStatService {

  private ServoService servoService;
  private TempService tempService;

  @Autowired
  public SmartStatService(ServoService servoService, TempService tempService) {
    this.servoService = servoService;
    this.tempService = tempService;
  }

  public double getTemp() {
    return tempService.getTemp();
  }

  public void setOn() {
    servoService.toggleFirstServo();
  }

  public void setOff() {
    servoService.toggleSecondServo();
  }

  public void setTemp(String temp) {
    // logic to set global temp var
  }

}
