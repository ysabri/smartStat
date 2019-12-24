package com.smartstat.services;

import static java.lang.Math.toIntExact;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;

import com.google.gson.Gson;
import com.smartstat.dtos.InfoDto;
import com.smartstat.exceptions.TemperatureNotAllowedException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmartStatService {

  private static final Logger logger = LoggerFactory.getLogger(SmartStatService.class);

  public static final int MAX_TEMP = 80;
  private static final int INIT_TEMP = 70;
  private static final int TIMER_PERIOD = 1200000;

  private ServoService servoService;
  private TempService tempService;
  private AtomicInteger wantedTemp = new AtomicInteger(INIT_TEMP);
  private AtomicBoolean override = new AtomicBoolean(false);
  private AtomicBoolean isOn = new AtomicBoolean(false);

  private LocalDateTime lastTunedOn;
  private LocalDateTime lastChecked = now();

  @Autowired
  public SmartStatService(ServoService servoService, TempService tempService) {
    this.servoService = servoService;
    this.tempService = tempService;

    var timer = new Timer();
    TempSetTask timerTask = new TempSetTask();
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
    var checkedMinutesAgo = MINUTES.between(lastChecked, now());
    return new InfoDto(isOn.get(), getTemp(), wantedTemp.get(), override.get(), checkedMinutesAgo, lastTunedOn);
  }

  private void setOn() {
    servoService.toggleFirstServo();
    isOn.set(true);
    lastTunedOn = now();
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
    } else if (!currTempIsOk(currTemp) && !isOn.get()) {
      setOn();
    }
    logger.info(new Gson().toJson(new InfoDto(isOn.get(), currTemp, wantedTemp.get(), override.get(), 0, lastTunedOn)));
  }

  private boolean currTempIsOk(double currTemp) {
    return wantedTemp.get() <= toExactInt(currTemp);
  }

  private int toExactInt(double num) {
    return (int) num;
  }

  private class TempSetTask extends TimerTask {

    @Override
    public void run() {
      lastChecked = now();
      changeStateBasedOnTemp();
    }

  }

}
