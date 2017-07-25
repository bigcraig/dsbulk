/*
 * Copyright (C) 2017 DataStax Inc.
 *
 * This software can be used solely with DataStax Enterprise. Please consult the license at
 * http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.loader.tests.ccm.annotations;

import com.datastax.loader.tests.ccm.CCMCluster;
import com.datastax.loader.tests.ccm.DefaultCCMCluster;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that marks a method as being a factory for {@link CCMCluster} instances.
 *
 * <p>Such methods must be static and their return type must be {@link DefaultCCMCluster.Builder}.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface CCMFactory {}