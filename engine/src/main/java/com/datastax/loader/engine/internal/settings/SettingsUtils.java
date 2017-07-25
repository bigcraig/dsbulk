/*
 * Copyright (C) 2017 DataStax Inc.
 *
 * This software can be used solely with DataStax Enterprise. Please consult the license at
 * http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.loader.engine.internal.settings;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsUtils {

  public static int parseNumThreads(String maxThreads) {
    int threads;
    try {
      threads = Integer.parseInt(maxThreads);
    } catch (NumberFormatException e) {
      Pattern pattern = Pattern.compile("(\\d+)\\s*C");
      Matcher matcher = pattern.matcher(maxThreads);
      if (matcher.find()) {
        threads = Runtime.getRuntime().availableProcessors() * Integer.parseInt(matcher.group(1));
      } else {
        throw new IllegalArgumentException("Invalid number of threads: " + maxThreads);
      }
    }
    return threads;
  }

  public static Path parseAbsolutePath(String url) {
    URI uri;
    try {
      uri = new URL(url).toURI();
      if (!uri.isAbsolute()) {
        uri = uri.resolve(System.getProperty("user.dir"));
      } else if (uri.isOpaque() && uri.getScheme().equals("file")) {
        // handle file:./ relative paths
        uri =
            new URI("file:" + new File(uri.getSchemeSpecificPart()).getAbsolutePath()).normalize();
      }
      return Paths.get(uri).normalize().toAbsolutePath();
    } catch (URISyntaxException | MalformedURLException e) {
      throw new IllegalArgumentException("Invalid URL: " + url, e);
    }
  }
}