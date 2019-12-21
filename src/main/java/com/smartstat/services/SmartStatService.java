package com.smartstat.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmartStatService {

  private ServoService servoService;
  private TempService tempService;
  private TempSetTask timerTask;
  private AtomicBoolean override = new AtomicBoolean(false);

  @Autowired
  public SmartStatService(ServoService servoService, TempService tempService) {
    this.servoService = servoService;
    this.tempService = tempService;
    var timer = new Timer();
    this.timerTask = new TempSetTask(70);
    timer.scheduleAtFixedRate(timerTask, 0, 300000);
  }

  public double getTemp() {
    return tempService.getTemp();
  }

  public void turnOn() {
    setOn();
    override.set(true);
  }

  public void turnOff() {
    setOff();
    override.set(true);
  }

  public void setTemp(int temp) {
    timerTask.setTemp(temp);
    override.set(false);
  }

  private void setOn() {
    servoService.toggleFirstServo();
  }

  private void setOff() {
    servoService.toggleSecondServo();
  }

  private class TempSetTask extends TimerTask {
    private AtomicInteger wantedTemp = new AtomicInteger();

    public TempSetTask(int initTemp) {
      wantedTemp.set(initTemp);
    }

    @Override
    public void run() {
      if (override.get()) {
        return;
      }

      var currTemp = tempService.getTemp();
      if (currTempIsOk(currTemp)) {
        setOff();
      } else {
        setOn();
      }
    }

    public void setTemp(int newTemp) {
      wantedTemp.set(newTemp);
    }

    private boolean currTempIsOk(double currTemp) {
      int exactTemp = (int) currTemp;
      return wantedTemp.get() < exactTemp;
    }

  }

}
