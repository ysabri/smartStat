package com.smartstat.dtos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InfoDto {

  private boolean isOn;
  private double currentTemp;
  private int setTemp;
  private boolean overrideSet;
  private long lastCheckedInMinutes;
  private String lastTurnedOn;
  private String mode;

  public InfoDto(boolean isOn, double currentTemp, int setTemp, boolean overrideSet, long lastCheckedInMinutes, LocalDateTime lastTurnedOn,
      String mode) {
    this.isOn = isOn;
    this.currentTemp = currentTemp;
    this.setTemp = setTemp;
    this.overrideSet = overrideSet;
    this.lastCheckedInMinutes = lastCheckedInMinutes;
    this.lastTurnedOn = lastTurnedOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:m a"));
    this.mode = mode;
  }

  public boolean isOn() {
    return isOn;
  }

  public double getCurrentTemp() {
    return currentTemp;
  }

  public int getSetTemp() {
    return setTemp;
  }

  public boolean isOverrideSet() {
    return overrideSet;
  }

  public long getLastCheckedInMinutes() {
    return lastCheckedInMinutes;
  }

  public String getLastTurnedOn() {
    return lastTurnedOn;
  }

  public String getMode() {
    return mode;
  }

}
