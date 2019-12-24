package com.smartstat.services;

import com.smartstat.dtos.InfoDto;
import com.smartstat.exceptions.TemperatureNotAllowedException;
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
  private static final int TIMER_PERIOD = 1200000;

  private ServoService servoService;
  private TempService tempService;
  private TempSetTask timerTask;
  private AtomicInteger wantedTemp = new AtomicInteger(INIT_TEMP);
  private AtomicBoolean override = new AtomicBoolean(false);
  private AtomicBoolean isOn = new AtomicBoolean(false);

  @Autowired
  public SmartStatService(ServoService servoService, TempService tempService) {
    this.servoService = servoService;
    this.tempService = tempService;

    var timer = new Timer();
    this.timerTask = new TempSetTask();
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

  public void setTemp(int givenTemp) {
    if (givenTemp >= MAX_TEMP) {
      throw new TemperatureNotAllowedException();
    }

    wantedTemp.set(givenTemp);
    override.set(false);

    changeStateBasedOnTemp();
  }

  public InfoDto getInfo() {
    return new InfoDto(isOn.get(), getTemp(), wantedTemp.get(), override.get());
  }

  private void setOn() {
    servoService.toggleFirstServo();
    isOn.set(true);
  }

  private void setOff() {
    servoService.toggleSecondServo();
    isOn.set(false);
  }

  private void changeStateBasedOnTemp() {
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
    } else if (currTempIsNotOk(currTemp) && !isOn.get()) {
      setOn();
    }
  }

  private boolean currTempIsOk(double currTemp) {
    return wantedTemp.get() <= toExactInt(currTemp);
  }

  private boolean currTempIsNotOk(double currTemp) {
    return wantedTemp.get() >= toExactInt(currTemp);
  }

  private int toExactInt(double num) {
    return (int) num;
  }

  private class TempSetTask extends TimerTask {

    @Override
    public void run() {
      changeStateBasedOnTemp();
    }

  }

}
