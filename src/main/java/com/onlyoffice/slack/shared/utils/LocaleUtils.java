package com.onlyoffice.slack.shared.utils;

import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class LocaleUtils {
  public Locale toLocale(String localeString) {
    if (localeString == null || localeString.isBlank()) return Locale.ENGLISH;

    try {
      if (localeString.contains("-")) {
        var parts = localeString.split("-", 2);
        if (parts.length == 2) return Locale.of(parts[0], parts[1]);
      }

      return Locale.of(localeString);
    } catch (Exception e) {
      return Locale.ENGLISH;
    }
  }
}
