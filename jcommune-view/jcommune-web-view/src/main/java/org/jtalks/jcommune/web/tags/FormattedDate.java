/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.web.tags;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.contrib.jsptag.FormatTag;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * This tag replaces the existing Joda Time format tag to take
 * into account the local time offset set in cookies.
 * Although the library has out-of-box timezone support,
 * we cannot use it since we don't know the timezone.
 * Automatic timezone detection is a complicated task:
 * - ip-based detection is inaccurate for some regions
 * - bare time offset cannot be properly mapped on the timezone,
 * 'cause timezones have different DST settings
 * <p/>
 * We also could ask the user himself for the timezone, but it's
 * as boring, as irritating.
 *
 * @author Evgeniy Naumenko
 */
public class FormattedDate extends FormatTag {

    /**
     * Serializable class should define it
     */
    private static final long serialVersionUID = 34588L;

    public static final String GMT_COOKIE_NAME = "GMT";

    public static final String DATE_FORMAT_PATTERN = "dd MMM yyyy HH:mm";

    private int offset = DEFAULT_OFFSET;

    public static final int DEFAULT_OFFSET = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPageContext(PageContext pageContext) {
        Cookie[] cookies = ((HttpServletRequest) pageContext.getRequest()).getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(GMT_COOKIE_NAME)) {
                    offset = this.convertTimeZoneOffset(cookie.getValue());
                    break;
                }
            }
        }
        super.setPageContext(pageContext);
        this.setDateFormattingOptions();
    }

    /**
     * This method adds the corresponding offset to make the date and time
     * match local user's timezone. If value is not a DateTime it's
     * formatted as is
     *
     * @param value value to be formatted as date
     * @throws JspTagException only from super() call
     */
    @Override
    public void setValue(Object value) throws JspTagException {
        DateTime time = (DateTime) value;
        DateTimeZone zone = time.getZone();
        long utcTime = zone.convertLocalToUTC(time.getMillis(), true);
        time = new DateTime(utcTime + offset);
        super.setValue(time);
    }

    /**
     * Converts timezone offset representation to millisecond offset.
     * If timezone offset representation is incorrect, then GMT
     * timezone value is used.
     *
     * @param jsRepresentation time difference between GMT and
     *                         local time, in minutes. Example: "-120"
     * @return signed millisecond timezone offset
     */
    private int convertTimeZoneOffset(String jsRepresentation) {
        try {
            final int min = 60;
            int millisec = 1000;
            return -Integer.parseInt(jsRepresentation) * min * millisec;
        } catch (NumberFormatException e) {
            // someone has passed wrong GMT in cookie, use GMT
            return DEFAULT_OFFSET;
        }
    }

    /**
     * Figures out the the current locale and defines the date formatting pattern.
     * Change the pattern here to affect all the dates formatting on pages.
     */
    private void setDateFormattingOptions() {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            this.setLocale(localeResolver.resolveLocale(request));
            this.setPattern(DATE_FORMAT_PATTERN);
        } catch (JspTagException e) {
            throw new IllegalStateException("Error while rendering the date", e);
        }
    }
}
