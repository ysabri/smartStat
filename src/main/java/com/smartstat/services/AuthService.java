package com.smartstat.services;

import static java.util.Optional.ofNullable;

import com.smartstat.dtos.TokenResponse;
import com.smartstat.exceptions.InvalidMatchException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 * NOTE: This code is not secure by any means. I would be using Java JWT or any other library if this was not a single use internal application.
 * This is not going to any production nor should anyone take inspiration from it in their production code.
 */
@Service
@PropertySource("classpath:credentials.properties")
public class AuthService {

  private final static String TOKEN_TYPE = "Bearer";
  private final static String EXPIRES_IN = "3600";

  @Value("${google.client.id}")
  private String clientId;

  @Value("${google.client.secret}")
  private String clientSecret;

  @Value("${google.code}")
  private String code;

  private final String redirectUri;

  private final String refreshToken;

  private final byte[] byteArray;

  public AuthService(@Value("${google.redirect.base}") String redirectUriBase, @Value("${google.project.id}") String projectId, @Value("${token.bytes}")
      int tokenBytes) {
    this.redirectUri = redirectUriBase + projectId;
    byteArray = new byte[tokenBytes];
    refreshToken = getNewToken();
  }

  public RedirectView handleOAuth(String passedClientId, String passedRedirectUri, String state, String response, RedirectAttributes attributes) {
    assertMatch(passedClientId, clientId);

    assertMatch(passedRedirectUri, redirectUri);

    attributes.addAttribute("code", code);
    attributes.addAttribute("token_type", TOKEN_TYPE.toLowerCase());
    attributes.addAttribute("state", state);
    attributes.addAttribute("response_type", response);

    return new RedirectView(redirectUri);
  }

  public TokenResponse getToken(String passedClientId, String clientSecret, String grantType, String code) {
    assertMatch(passedClientId, clientId);

    assertMatch(clientSecret, clientSecret);

    return new TokenResponse(TOKEN_TYPE, getNewToken(), refreshToken, EXPIRES_IN);
  }

  public TokenResponse getRefreshToken(String passedClientId, String passedRefreshToken) {
    assertMatch(passedClientId, clientId);

    assertMatch(passedRefreshToken, refreshToken);

    return new TokenResponse(TOKEN_TYPE, getNewToken(), "", EXPIRES_IN);
  }

  private String getNewToken() {
    new Random().nextBytes(byteArray);
    return new String(byteArray, StandardCharsets.UTF_8);
  }

  private void assertMatch(String a, String b) {
    ofNullable(a).filter(b::equalsIgnoreCase)
        .orElseThrow(InvalidMatchException::new);
  }

}
