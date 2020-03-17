package com.smartstat.services;

import static com.smartstat.services.SmartStatService.MAX_TEMP;
import static java.lang.Math.ceil;
import static java.util.Arrays.stream;

import com.google.actions.api.smarthome.DisconnectRequest;
import com.google.actions.api.smarthome.ExecuteRequest;
import com.google.actions.api.smarthome.ExecuteResponse;
import com.google.actions.api.smarthome.ExecuteResponse.Payload.Commands;
import com.google.actions.api.smarthome.QueryRequest;
import com.google.actions.api.smarthome.QueryResponse;
import com.google.actions.api.smarthome.SmartHomeApp;
import com.google.actions.api.smarthome.SyncRequest;
import com.google.actions.api.smarthome.SyncResponse;
import com.google.actions.api.smarthome.SyncResponse.Payload.Device;
import com.google.home.graph.v1.DeviceProto;
import com.smartstat.exceptions.ActionNotFoundException;
import com.smartstat.exceptions.SetPointTypeNotSupportedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmartHomeService extends SmartHomeApp {

  private final static String ON = "on";
  private final static String OFF = "off";
  private final static String ON_OFF = ON + "," + OFF;
  private final static double CONVERSION_FRACTION = (9.0 / 5.0);
  private final static int CONVERSION_OFFSET = 32;

  private static final Logger logger = LoggerFactory.getLogger(SmartHomeService.class);

  private SmartStatService smartStatService;

  @Autowired
  public SmartHomeService(SmartStatService smartStatService) {
    this.smartStatService = smartStatService;
  }

  private static final String ID = "1";

  @NotNull
  @Override
  public SyncResponse onSync(@NotNull SyncRequest syncRequest, @Nullable Map<?, ?> map) {
    var payload = new SyncResponse.Payload();
    payload.setAgentUserId("1836.15267389");
    payload.setDevices(new Device[] { new Device.Builder().setId(ID)
                                          .setType("action.devices.types.THERMOSTAT")
                                          .setTraits(List.of("action.devices.traits.OnOff", "action.devices.traits.TemperatureSetting"))
                                          .setName(DeviceProto.DeviceNames.newBuilder()
                                              .addDefaultNames("Smart Stat")
                                              .setName("Smart stat")
                                              .addNicknames("Thermostat")
                                              .build())
                                          .setWillReportState(true)
                                          .setAttributes(new JSONObject().put("availableThermostatModes", ON_OFF + "," + smartStatService.getMode()
                                              .getString())
                                              .put("thermostatTemperatureRange", "65," + MAX_TEMP)
                                              .put("thermostatTemperatureUnit", "F"))
                                          .setDeviceInfo(DeviceProto.DeviceInfo.newBuilder()
                                              .setManufacturer("Yazeed Sabra")
                                              .setModel("TT")
                                              .setHwVersion("2.5")
                                              .setSwVersion("3.0")
                                              .build())
                                          .setRoomHint("Living room").build() });

    return new SyncResponse(syncRequest.getRequestId(), payload);
  }

  @Override
  public void onDisconnect(@NotNull DisconnectRequest disconnectRequest, @Nullable Map<?, ?> map) {
  }

  @NotNull
  @Override
  public ExecuteResponse onExecute(@NotNull ExecuteRequest executeRequest, @Nullable Map<?, ?> map) {
    var commands = ((ExecuteRequest.Inputs) executeRequest.inputs[0]).payload.commands;

    stream(commands).forEach(currCommand -> stream(currCommand.getExecution()).forEach(handleCommand()));

    var payload = new ExecuteResponse.Payload();

    payload.setCommands(new Commands[] { new Commands(new String[] { ID }, "SUCCESS", new HashMap<>() {
      {
        put("thermostatMode", getMode());
        put("thermostatTemperatureSetpoint", getSetTemp());
      }
    }, null, null) });

    return new ExecuteResponse(executeRequest.getRequestId(), payload);
  }

  private Consumer<? super ExecuteRequest.Inputs.Payload.Commands.Execution> handleCommand() {
    return command -> {
      Map<String, Object> params = command.getParams();

      switch (command.getCommand()) {
      case "action.devices.commands.ThermostatTemperatureSetpoint":
        var setPoint = parseSetPoint(command);
        setTemp(setPoint);
        logger.info("temp is set to {}", setPoint);
        break;
      case "action.devices.commands.ThermostatSetMode":
        var thermoMode = (String) params.get("thermostatMode");
        setMode(thermoMode);
        logger.info("Thermostat is set to {}", thermoMode);
        break;
      case "action.devices.commands.OnOff":
        var isOn = (boolean) params.get("on");
        setMode(isOn);
        logger.info("Thermostat is set to {}", isOn);
        break;
      default:
        throw new ActionNotFoundException(command.getCommand());
      }
    };
  }

  private double parseSetPoint(ExecuteRequest.Inputs.Payload.Commands.Execution command) {
    var setPoint = command.getParams()
        .get("thermostatTemperatureSetpoint");
    if (setPoint instanceof Double) {
      return (Double) setPoint;
    } else if (setPoint instanceof Integer) {
      return (Integer) setPoint;
    }
    throw new SetPointTypeNotSupportedException();
  }

  @NotNull
  @Override
  public QueryResponse onQuery(@NotNull QueryRequest queryRequest, @Nullable Map<?, ?> map) {
    QueryResponse.Payload payload = new QueryResponse.Payload();
    payload.setDevices(new HashMap<String, Map<String, Object>>() {
      {
        put(ID, new HashMap<String, Object>() {
          {
            put("online", true);
            put("thermostatMode", getMode());
            put("thermostatTemperatureSetpoint", getSetTemp());
            put("status", "SUCCESS");
          }
        });
      }
    });

    logger.info("onQuery for {} and {}", getMode(), getSetTemp());

    return new QueryResponse(queryRequest.getRequestId(), payload);
  }

  private void setMode(boolean isOn) {
    var setTemp = smartStatService.getSetTemp();
    if (isOn) {
      smartStatService.setTemp(setTemp);
    } else {
      smartStatService.turnOff();
    }
  }

  private void setMode(String newMode) {
    var mode = smartStatService.getMode()
        .getString();

    setMode(mode.equals(newMode));
  }

  private String getMode() {
    var mode = smartStatService.getMode()
        .getString();
    var isOff = !smartStatService.isOn() && smartStatService.isOverride();

    return isOff ? OFF : mode;
  }

  private void setTemp(double setTempC) {
    smartStatService.setTemp(toF(setTempC));
  }

  private Double getSetTemp() {
    return ceil(toC(smartStatService.getSetTemp()));
  }

  private Double toC(double fTemp) {
    return (fTemp - CONVERSION_OFFSET) / CONVERSION_FRACTION;
  }

  private int toF(Double cTemp) {
    return (int) ceil((cTemp * CONVERSION_FRACTION) + CONVERSION_OFFSET);
  }

}