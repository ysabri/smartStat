package com.smartstat.services;

import static com.smartstat.constants.Mode.COOL;
import static com.smartstat.constants.Mode.HEAT;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;

import com.google.gson.Gson;
import com.smartstat.constants.Mode;
import com.smartstat.dtos.InfoDto;
import com.smartstat.exceptions.TemperatureNotAllowedException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
  private Timer timer;
  private AtomicInteger wantedTemp = new AtomicInteger(INIT_TEMP);
  private AtomicBoolean override = new AtomicBoolean(false);
  private AtomicBoolean isOn = new AtomicBoolean(false);
  private AtomicReference<Mode> mode = new AtomicReference<>(HEAT);

  private LocalDateTime lastTunedOn = now();
  private LocalDateTime lastChecked = now();

  @Autowired
  public SmartStatService(ServoService servoService, TempService tempService) {
    this.servoService = servoService;
    this.tempService = tempService;

    timer = new Timer();
    TempSetTask timerTask = new TempSetTask();
    timer.scheduleAtFixedRate(timerTask, 0, TIMER_PERIOD);

    setOff();
  }

  public double getTemp() {
    return tempService.getTemp();
  }

  public int getSetTemp() {
    return wantedTemp.get();
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

    changeStateBasedOnTemp(tempService.getTemp());
  }

  public InfoDto getInfo() {
    var checkedMinutesAgo = MINUTES.between(lastChecked, now());
    return new InfoDto(isOn.get(), getTemp(), wantedTemp.get(), override.get(), checkedMinutesAgo, lastTunedOn, getMode().getString());
  }

  public void setMode(Mode newMode) {
    mode.set(newMode);
  }

  public Mode getMode() {
    return mode.get();
  }

  public boolean isOn() {
    return isOn.get();
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

  private void changeStateBasedOnTemp(double currTemp) {
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

  private boolean currTempIsOk(double currTemp) {
    if (mode.get()
        .equals(COOL)) {
      return wantedTemp.get() >= toExactInt(currTemp);
    }

    return wantedTemp.get() <= toExactInt(currTemp);
  }

  private int toExactInt(double num) {
    return (int) num;
  }

  private class TempSetTask extends TimerTask {

    @Override
    public void run() {
      lastChecked = now();
      var currTemp = tempService.getTemp();

      changeStateBasedOnTemp(currTemp);

      logger.info(new Gson().toJson(new InfoDto(isOn.get(), currTemp, wantedTemp.get(), override.get(), 0, lastTunedOn, getMode().getString())));
    }

  }

}
