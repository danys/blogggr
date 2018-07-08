package com.blogggr.utilities;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;

/**
 * Created by Daniel Sunnen on 11.06.18.
 */
public class SimpleBundleMessageSource extends ResourceBundleMessageSource {

  public String getMessage(String code) {
    return getMessage(code, null, LocaleContextHolder.getLocale());
  }

  public String getMessage(String code, @Nullable Object[] args) {
    return getMessage(code, args, LocaleContextHolder.getLocale());
  }

  public String getMessage(String[] codes) {
    if (codes == null || codes.length < 1) {
      return "";
    }
    return getMessage(codes[0], null, LocaleContextHolder.getLocale());
  }
}
