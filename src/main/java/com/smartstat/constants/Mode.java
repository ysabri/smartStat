package com.smartstat.constants;

public enum Mode {
  HEAT,
  COOL;

  public String getString() {
    return this.name().toLowerCase();
  }
}
