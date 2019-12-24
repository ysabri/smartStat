package com.smartstat.exceptions;

public class ShellingOutFailed extends RuntimeException {

  public ShellingOutFailed(Exception e) {
    super(e);
  }

}
