package com.smartstat.controllers;

import com.smartstat.services.SmartStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/smartStat/")
public class SmartStatController {

  private SmartStatService smartStatService;

  @Autowired
  public SmartStatController(SmartStatService smartStatService) {
    this.smartStatService = smartStatService;
  }

  @GetMapping("temp")
  public double getTemp() {
    return smartStatService.getTemp();
  }

  @PostMapping("on")
  public void setOn() {
    smartStatService.turnOn();
  }

  @PostMapping("off")
  public void setOff() {
    smartStatService.turnOff();
  }

  @PostMapping("temp/{temp}")
  public void setTemp(@PathVariable int temp) {
    smartStatService.setTemp(temp);
  }

}
