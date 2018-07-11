package com.blogggr.filters;

import com.blogggr.config.AppConfig;
import com.blogggr.security.UserPrincipal;
import com.cloudinary.utils.StringUtils;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

/**
 * Created by Daniel Sunnen on 10.06.18.
 */
public class InternationalizationFilter extends GenericFilterBean {

  private static final String ACCEPT_LANG = "Accept-Language";

  private String getBestLocale(List<LanguageRange> languageRanges) {
    int index;
    for (LanguageRange langRange : languageRanges) {
      index = AppConfig.LANGUAGES.indexOf(langRange.getRange());
      if (index != -1) {
        return AppConfig.LANGUAGES.get(index);
      }
    }
    return AppConfig.LANGUAGES.get(0);
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    if (SecurityContextHolder.getContext().getAuthentication() != null && !(SecurityContextHolder
        .getContext()
        .getAuthentication() instanceof AnonymousAuthenticationToken)) {
      UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      Locale locale = new Locale(userPrincipal.getUser().getLang());
      WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,
          locale);
    } else if (!StringUtils.isBlank(request.getHeader(ACCEPT_LANG))) {
      List<LanguageRange> list = Locale.LanguageRange.parse(request.getHeader(ACCEPT_LANG));
      WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,
          new Locale(getBestLocale(list)));
    }
    chain.doFilter(request, response);
  }
}
