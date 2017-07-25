/*
 * Copyright (C) 2017 DataStax Inc.
 *
 * This software can be used solely with DataStax Enterprise. Please consult the license at
 * http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.loader.engine.internal.settings;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public class SettingsManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(SettingsManager.class);

  private static final Config REFERENCE =
      ConfigFactory.defaultReference().getConfig("datastax-loader");

  private static final Config DEFAULT = ConfigFactory.load().getConfig("datastax-loader");

  private final String[] args;
  private final String operationId;

  private Config config;
  private DriverSettings driverSettings;
  private ConnectorSettings connectorSettings;
  private SchemaSettings schemaSettings;
  private BatchSettings batchSettings;
  private ExecutorSettings executorSettings;
  private LogSettings logSettings;
  private CodecSettings codecSettings;

  public SettingsManager(String[] args, String operationId) {
    this.args = args;
    this.operationId = operationId;
  }

  public void loadConfiguration() throws Exception {
    config = parseUserSettings(args).withFallback(DEFAULT);
    config.checkValid(REFERENCE);
    // TODO check unrecognized config
    logSettings = new LogSettings(config.getConfig("log"), operationId);
    driverSettings = new DriverSettings(config.getConfig("driver"));
    connectorSettings = new ConnectorSettings(config.getConfig("connector"));
    schemaSettings = new SchemaSettings(config.getConfig("schema"));
    batchSettings = new BatchSettings(config.getConfig("batch"));
    executorSettings = new ExecutorSettings(config.getConfig("executor"));
    codecSettings = new CodecSettings(config.getConfig("codec"));
  }

  public void logEffectiveSettings() {
    ConfigRenderOptions renderOptions =
        ConfigRenderOptions.defaults().setJson(false).setOriginComments(false).setComments(false);
    LOGGER.info("Loader effective settings:\n" + config.root().render(renderOptions));
  }

  public DriverSettings getDriverSettings() {
    return driverSettings;
  }

  public ConnectorSettings getConnectorSettings() {
    return connectorSettings;
  }

  public SchemaSettings getSchemaSettings() {
    return schemaSettings;
  }

  public BatchSettings getBatchSettings() {
    return batchSettings;
  }

  public ExecutorSettings getExecutorSettings() {
    return executorSettings;
  }

  public LogSettings getLogSettings() {
    return logSettings;
  }

  public CodecSettings getCodecSettings() {
    return codecSettings;
  }

  private static Config parseUserSettings(String[] args) {
    Config userSettings = ConfigFactory.empty();
    for (String arg : args) {
      userSettings = ConfigFactory.parseString(arg).withFallback(userSettings);
    }
    return userSettings;
  }
}