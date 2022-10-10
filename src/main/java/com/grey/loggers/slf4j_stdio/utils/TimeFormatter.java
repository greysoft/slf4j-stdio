/*
 * Copyright 2014-2022 Yusef Badri - All rights reserved.
 * grey-slf4j-logstdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio.utils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeFormatter {
	private enum FORMAT {UTC, LOCAL, MILLISECONDS, SECONDS}

	private final FORMAT timeFormat;
	private final ZoneId localZone;

	public TimeFormatter(String fmt) {
		FORMAT format = null;
		ZoneId zone = null;
		try {
			format = FORMAT.valueOf(fmt.toUpperCase());
			if (format == FORMAT.LOCAL) {
				zone = ZoneId.systemDefault();
			}
		} catch (IllegalArgumentException ex) {
			//assume it's a Java timezone
			zone = ZoneId.of(fmt);
		}
		this.timeFormat = format;
		this.localZone = zone;
	}

	public ZoneId getTimeZone() {
		return localZone;
	}

	public String getTime(Clock clock) {
		long millis = clock.millis();

		if (timeFormat == FORMAT.MILLISECONDS) {
			return String.valueOf(millis);
		}
		if (timeFormat == FORMAT.SECONDS) {
			return String.valueOf(millis / 1000);
		}
		Instant instant = Instant.ofEpochMilli(millis);

		if (timeFormat == FORMAT.UTC) {
			// Instant.toString omits milliseconds fraction if zero, which leads to irregular looking timestamp strings
			String s = instant.toString();
			if (millis % 1000 == 0) s = s.substring(0, s.length() - 1)+".000Z";
			return s;
		}
		ZonedDateTime dt = instant.atZone(localZone);
		String s = dt.toLocalDate()+" "+dt.toLocalTime();
		if (millis % 1000 == 0) s = s+".000";
		return s;
	}

	@Override
	public String toString() {
		return super.toString()+"/"+timeFormat+"-"+localZone;
	}
}
