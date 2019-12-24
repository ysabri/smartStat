package com.smartstat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.smartstat.services.ServoService;
import com.smartstat.services.SmartStatService;
import com.smartstat.services.TempService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SmartStatServiceTest {

  private static final int INIT_TEMP = 70;

  private SmartStatService smartStatService;

  @Mock
  private TempService tempService;

  @Mock
  private ServoService servoService;

  @BeforeEach
  public void setup() {
    when(tempService.getTemp()).thenReturn((double) INIT_TEMP);
    smartStatService = new SmartStatService(servoService, tempService);
  }

  @Test
  public void testTempRange() {
    smartStatService.setTemp(INIT_TEMP + 2);
    var info = smartStatService.getInfo();
    assertTrue(info.isOn());

    smartStatService.setTemp(INIT_TEMP - 1);
    info = smartStatService.getInfo();
    assertFalse(info.isOn());

    smartStatService.setTemp(INIT_TEMP);
    info = smartStatService.getInfo();
    assertFalse(info.isOn());

    smartStatService.setTemp(INIT_TEMP + 1);
    info = smartStatService.getInfo();
    assertTrue(info.isOn());
  }

}
