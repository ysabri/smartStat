package com.smartstat.Exceptions;

public class ShellingOutFailed extends RuntimeException {

  public ShellingOutFailed(Exception e) {
    super(e);
  }

}
