package com.smartstat.controllers;

import com.smartstat.constants.Mode;
import com.smartstat.dtos.InfoDto;
import com.smartstat.services.SmartStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/smartStat/")
public class SmartStatController {

  private SmartStatService smartStatService;

  @Autowired
  public SmartStatController(SmartStatService smartStatService) {
    this.smartStatService = smartStatService;
  }

  @GetMapping("info")
  public InfoDto getInfo() {
    return smartStatService.getInfo();
  }

  @PostMapping("mode")
  public void setMode(Mode mode) {
    smartStatService.setMode(mode);
  }

  @PostMapping("temperature/{temp}")
  public void setTemp(@PathVariable int temp) {
    smartStatService.setTemp(temp);
  }

  @PostMapping("on")
  public void setOn() {
    smartStatService.turnOn();
  }

  @PostMapping("off")
  public void setOff() {
    smartStatService.turnOff();
  }

}
