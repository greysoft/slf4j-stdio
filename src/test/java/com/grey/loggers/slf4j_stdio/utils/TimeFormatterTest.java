/*
 * Copyright 2014-2022 Yusef Badri - All rights reserved.
 * grey-slf4j-logstdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio.utils;

import java.time.Clock;
import java.time.ZoneId;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TimeFormatterTest {
	private static final long TIME1 = 1608562414887L; //Dec 21st 2020, 14:53 GMT
	private static final long TIME2_DST = 1598194414887L; //Aug 23rd 2020, 15:53 BST
	private static final long TIME3_SECS = TIME1 - TIME1 % 1000; //TIME1 with non-fractional seconds

	@Test
	public void testUTC() {
		Clock clock = Mockito.mock(Clock.class);
		Mockito.when(clock.millis()).thenReturn(TIME1, TIME2_DST, TIME3_SECS);
		TimeFormatter fmt = new TimeFormatter("UTC");
		String timestamp = fmt.getTime(clock);
		Assert.assertEquals("2020-12-21T14:53:34.887Z", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("2020-08-23T14:53:34.887Z", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("2020-12-21T14:53:34.000Z", timestamp);
	}

	// This is based on UK/Ireland time
	@Test
	public void testLocalTime() {
		Clock clock = Mockito.mock(Clock.class);
		Mockito.when(clock.millis()).thenReturn(TIME1, TIME2_DST, TIME3_SECS);
		TimeFormatter fmt = new TimeFormatter("local");
		String timestamp = fmt.getTime(clock);
		org.junit.Assume.assumeTrue(fmt.getTimeZone().equals(ZoneId.of("Europe/London")));
		Assert.assertEquals("2020-12-21 14:53:34.887", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("2020-08-23 15:53:34.887", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("2020-12-21 14:53:34.000", timestamp);
	}

	@Test
	public void testLondon() {
		Clock clock = Mockito.mock(Clock.class);
		Mockito.when(clock.millis()).thenReturn(TIME1, TIME2_DST, TIME3_SECS);
		TimeFormatter fmt = new TimeFormatter("Europe/London");
		String timestamp = fmt.getTime(clock);
		Assert.assertEquals("2020-12-21 14:53:34.887", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("2020-08-23 15:53:34.887", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("2020-12-21 14:53:34.000", timestamp);
	}

	@Test
	public void testBerlin() {
		Clock clock = Mockito.mock(Clock.class);
		Mockito.when(clock.millis()).thenReturn(TIME1, TIME2_DST);
		TimeFormatter fmt = new TimeFormatter("Europe/Berlin");
		String timestamp = fmt.getTime(clock);
		Assert.assertEquals("2020-12-21 15:53:34.887", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("2020-08-23 16:53:34.887", timestamp);
	}

	@Test
	public void testMilliSeconds() {
		Clock clock = Mockito.mock(Clock.class);
		Mockito.when(clock.millis()).thenReturn(TIME1, TIME2_DST, TIME3_SECS);
		TimeFormatter fmt = new TimeFormatter("milliseconds");
		String timestamp = fmt.getTime(clock);
		Assert.assertEquals("1608562414887", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("1598194414887", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("1608562414000", timestamp);
	}

	@Test
	public void testSeconds() {
		Clock clock = Mockito.mock(Clock.class);
		Mockito.when(clock.millis()).thenReturn(TIME1, TIME2_DST, TIME3_SECS);
		TimeFormatter fmt = new TimeFormatter("seconds");
		String timestamp = fmt.getTime(clock);
		Assert.assertEquals("1608562414", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("1598194414", timestamp);
		timestamp = fmt.getTime(clock);
		Assert.assertEquals("1608562414", timestamp);
	}
}
