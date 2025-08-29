/*
 * Copyright 2025 Yusef Badri - All rights reserved.
 * grey-slf4j-logstdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationTest {
	private static final Logger Log = LoggerFactory.getLogger(ApplicationTest.class);

	// This is a manual test - just verify that the logs come out as expected
	@Test
	public void testBasicLogging() {
		assertEquals(LoggerAdapter.class, Log.getClass());
		Log.info("Hello {}", "world");
		Log.error("Failure in {}", "logic", new Exception("Dummy Error"));
	}
}
