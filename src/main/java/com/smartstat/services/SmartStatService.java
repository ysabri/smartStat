package com.smartstat.services;

import com.smartstat.Exceptions.TempratureNotAllowedException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmartStatService {

  public static final int MAX_TEMP = 80;
  private static final int INIT_TEMP = 70;
  private static final int TIMER_PERIOD = 600000;

  private ServoService servoService;
  private TempService tempService;
  private TempSetTask timerTask;
  private AtomicBoolean override = new AtomicBoolean(false);
  private AtomicBoolean isOn = new AtomicBoolean(false);

  @Autowired
  public SmartStatService(ServoService servoService, TempService tempService) {
    this.servoService = servoService;
    this.tempService = tempService;

    var timer = new Timer();
    this.timerTask = new TempSetTask(INIT_TEMP);
    timer.scheduleAtFixedRate(timerTask, 0, TIMER_PERIOD);

    setOff();
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
    if (temp >= MAX_TEMP) {
      throw new TempratureNotAllowedException();
    }
    timerTask.setTemp(temp);
    override.set(false);
  }

  private void setOn() {
    servoService.toggleFirstServo();
    isOn.set(true);
  }

  private void setOff() {
    servoService.toggleSecondServo();
    isOn.set(false);
  }

  private class TempSetTask extends TimerTask {
    private AtomicInteger wantedTemp = new AtomicInteger();

    public TempSetTask(int initTemp) {
      wantedTemp.set(initTemp);
    }

    @Override
    public void run() {
      var currTemp = tempService.getTemp();

      // this most likely means something is wrong, thus the override
      if (currTemp >= MAX_TEMP) {
        setOff();
        override.set(true);
      }

      if (override.get()) {
        return;
      }

      if (currTempIsOk(currTemp) && isOn.get()) {
        setOff();
      } else if (!currTempIsOk(currTemp) && !isOn.get()) {
        setOn();
      }
    }

    public void setTemp(int newTemp) {
      wantedTemp.set(newTemp);
    }

    private boolean currTempIsOk(double currTemp) {
      int exactTemp = (int) currTemp;
      return wantedTemp.get() <= exactTemp;
    }

  }

}
