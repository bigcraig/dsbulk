/*
 * Copyright (C) 2017 DataStax Inc.
 *
 * This software can be used solely with DataStax Enterprise. Please consult the license at
 * http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.driver.core;

/** */
public class DriverCoreTestHooks {

  public static PreparedId newPreparedId(ColumnDefinitions cd, ProtocolVersion version) {
    return new PreparedId(null, cd, null, null, version);
  }
}