package com.smartstat.controllers;

import com.smartstat.dtos.TokenResponse;
import com.smartstat.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController()
@RequestMapping("/user")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  private AuthService authService;

  @Autowired
  public UserController(AuthService authService) {
    this.authService = authService;
  }

  @GetMapping("/oauth")
  public RedirectView authenticateUser(@RequestParam("client_id") String clientId, @RequestParam("redirect_uri") String redirectUri,
      @RequestParam("state") String state, @RequestParam("response_type") String response, @RequestParam("user_locale") String locale,
      RedirectAttributes attributes) {
    logger.info("Authenticating client {} ", clientId);
    return authService.handleOAuth(clientId, redirectUri, state, response, attributes);
  }

  @PostMapping("/token")
  public TokenResponse authorizeUser(@RequestParam("client_id") String clientId, @RequestParam(value = "client_secret") String clientSecret,
      @RequestParam("grant_type") String grantType, @RequestParam(required = false) String code, @RequestParam(value = "refresh_token", required = false) String refreshToken) {
    if ("refresh_token".equalsIgnoreCase(grantType)) {
      return authService.getRefreshToken(clientId, refreshToken);
    }
    return authService.getToken(clientId, clientSecret, grantType, code);
  }

}
