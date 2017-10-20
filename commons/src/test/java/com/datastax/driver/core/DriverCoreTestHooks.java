/*
 * Copyright (C) 2017 DataStax Inc.
 *
 * This software can be used solely with DataStax Enterprise. Please consult the license at
 * http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.driver.core;

import java.util.Arrays;

/** This is a copy of DriverCoreTestHooks from the engine module. */
public class DriverCoreTestHooks {

  public static PreparedId newPreparedId(ColumnDefinitions cd, ProtocolVersion version) {
    return new PreparedId(null, cd, null, null, version);
  }

  public static TupleType newTupleType(DataType... types) {
    return newTupleType(ProtocolVersion.NEWEST_SUPPORTED, CodecRegistry.DEFAULT_INSTANCE, types);
  }

  public static TupleType newTupleType(
      ProtocolVersion protocolVersion, CodecRegistry codecRegistry, DataType... types) {
    return new TupleType(Arrays.asList(types), protocolVersion, codecRegistry);
  }

  public static UserType newUserType(UserType.Field... fields) {
    return newUserType(
        "ks", "udt", ProtocolVersion.NEWEST_SUPPORTED, CodecRegistry.DEFAULT_INSTANCE, fields);
  }

  public static UserType newUserType(CodecRegistry codecRegistry, UserType.Field... fields) {
    return newUserType("ks", "udt", ProtocolVersion.NEWEST_SUPPORTED, codecRegistry, fields);
  }

  public static UserType newUserType(
      String keyspace,
      String typeName,
      ProtocolVersion protocolVersion,
      CodecRegistry codecRegistry,
      UserType.Field... fields) {
    return new UserType(
        keyspace, typeName, true, Arrays.asList(fields), protocolVersion, codecRegistry);
  }

  public static UserType.Field newField(String name, DataType type) {
    return new UserType.Field(name, type);
  }

  public static ColumnDefinitions.Definition newDefinition(String name, DataType type) {
    return newDefinition("ks", "t", name, type);
  }

  public static ColumnDefinitions.Definition newDefinition(
      String keyspace, String table, String name, DataType type) {
    return new ColumnDefinitions.Definition(keyspace, table, name, type);
  }

  public static ColumnDefinitions newColumnDefinitions(ColumnDefinitions.Definition... cols) {
    return new ColumnDefinitions(cols, CodecRegistry.DEFAULT_INSTANCE);
  }
}