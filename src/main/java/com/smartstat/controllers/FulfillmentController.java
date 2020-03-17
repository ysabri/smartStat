package com.smartstat.controllers;

import com.smartstat.services.SmartHomeService;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/intents")
public class FulfillmentController {

  private SmartHomeService smartHomeService;

  @Autowired
  public FulfillmentController(SmartHomeService smartHomeService) {
    this.smartHomeService = smartHomeService;
  }

  @PostMapping("/fulfillment")
  public CompletableFuture<String> fulfillIntent(@RequestBody String intentRequest, @RequestHeader Map<?, ?> headers) {
    return smartHomeService.handleRequest(intentRequest, headers);
  }

}
