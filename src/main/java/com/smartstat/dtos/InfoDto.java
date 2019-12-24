package com.smartstat.dtos;

public class InfoDto {

  private boolean isOn;
  private double currentTemp;
  private int setTemp;
  private boolean overrideSet;

  public InfoDto(boolean isOn, double currentTemp, int setTemp, boolean overrideSet) {
    this.isOn = isOn;
    this.currentTemp = currentTemp;
    this.setTemp = setTemp;
    this.overrideSet = overrideSet;
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

}
