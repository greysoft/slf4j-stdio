/*
 * Copyright 2014-2021 Yusef Badri - All rights reserved.
 * grey-slf4j-stdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.yblog_slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grey.loggers.slf4j_stdio.Defs;
import com.grey.loggers.slf4j_stdio.LoggerAdapter;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;

public class LoggerAdapterTest {
	@Test
	public void testAutoInstantiation() {
		Logger logger = LoggerFactory.getLogger(getClass());
		Assert.assertSame(logger.getClass(), LoggerAdapter.class);
		logger.info("Random test message"); //sanity check, verify manually
	}

	// Test the default logging level of Info
	@Test
	public void testLevelInfo() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LoggerAdapter logger = new LoggerAdapter("testLevels", pstrm, Defs.LOGLEVEL.INFO);
		Assert.assertTrue(logger.isErrorEnabled());
		Assert.assertTrue(logger.isWarnEnabled());
		Assert.assertTrue(logger.isInfoEnabled());
		Assert.assertFalse(logger.isDebugEnabled());
		Assert.assertFalse(logger.isTraceEnabled());

		logger.warn("msg1");
		String s = bstrm.toString();
		int prevlen = s.length();
		Assert.assertTrue(s, s.endsWith("msg1\n"));

		logger.info("msg2");
		s = bstrm.toString();
		Assert.assertTrue(s, s.endsWith("msg2\n"));
		Assert.assertTrue(s, s.length() > prevlen);
		prevlen = s.length();

		logger.debug("msg3");
		s = bstrm.toString();
		Assert.assertTrue(s, s.endsWith("msg2\n"));
		Assert.assertEquals(s, prevlen, s.length());
	}

	// ensure that Error-level logging only prints errors
	@Test
	public void testLevelError() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LoggerAdapter logger = new LoggerAdapter("testLevelDebug", pstrm, Defs.LOGLEVEL.ERROR);
		Assert.assertTrue(logger.isErrorEnabled());
		Assert.assertFalse(logger.isWarnEnabled());
		Assert.assertFalse(logger.isInfoEnabled());
		Assert.assertFalse(logger.isDebugEnabled());
		Assert.assertFalse(logger.isTraceEnabled());

		logger.error("msg1");
		String s = bstrm.toString();
		int prevlen = s.length();
		Assert.assertTrue(s, s.endsWith("msg1\n"));

		logger.warn("msg2");
		s = bstrm.toString();
		Assert.assertTrue(s, s.endsWith("msg1\n"));
		Assert.assertEquals(s, prevlen, s.length());
	}

	// ensure that Warn-level logging excludes Info
	@Test
	public void testLevelWarn() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LoggerAdapter logger = new LoggerAdapter("testLevelDebug", pstrm, Defs.LOGLEVEL.WARN);
		Assert.assertTrue(logger.isErrorEnabled());
		Assert.assertTrue(logger.isWarnEnabled());
		Assert.assertFalse(logger.isInfoEnabled());
		Assert.assertFalse(logger.isDebugEnabled());
		Assert.assertFalse(logger.isTraceEnabled());

		logger.error("msg1");
		String s = bstrm.toString();
		int prevlen = s.length();
		Assert.assertTrue(s, s.endsWith("msg1\n"));

		logger.warn("msg2");
		s = bstrm.toString();
		Assert.assertTrue(s, s.endsWith("msg2\n"));
		Assert.assertTrue(s, s.length() > prevlen);
		prevlen = s.length();

		logger.info("msg3");
		s = bstrm.toString();
		Assert.assertTrue(s, s.endsWith("msg2\n"));
		Assert.assertEquals(s, prevlen, s.length());
	}

	// ensure that Debug outranks Trace
	@Test
	public void testLevelDebug() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LoggerAdapter logger = new LoggerAdapter("testLevelDebug", pstrm, Defs.LOGLEVEL.DEBUG);
		Assert.assertTrue(logger.isErrorEnabled());
		Assert.assertTrue(logger.isWarnEnabled());
		Assert.assertTrue(logger.isInfoEnabled());
		Assert.assertTrue(logger.isDebugEnabled());
		Assert.assertFalse(logger.isTraceEnabled());

		logger.debug("msg1");
		String s = bstrm.toString();
		int prevlen = s.length();
		Assert.assertTrue(s, s.endsWith("msg1\n"));

		logger.trace("msg2");
		s = bstrm.toString();
		Assert.assertTrue(s, s.endsWith("msg1\n"));
		Assert.assertEquals(s, prevlen, s.length());
	}

	// Ensure that Trace-level logging prints everything
	@Test
	public void testLevelTrace() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LoggerAdapter logger = new LoggerAdapter("testLevelTrace", pstrm, Defs.LOGLEVEL.TRACE);
		Assert.assertTrue(logger.isErrorEnabled());
		Assert.assertTrue(logger.isWarnEnabled());
		Assert.assertTrue(logger.isInfoEnabled());
		Assert.assertTrue(logger.isDebugEnabled());
		Assert.assertTrue(logger.isTraceEnabled());

		logger.debug("msg1");
		String s = bstrm.toString();
		int prevlen = s.length();
		Assert.assertTrue(s, s.endsWith("msg1\n"));

		logger.trace("msg2");
		s = bstrm.toString();
		Assert.assertTrue(s, s.endsWith("msg2\n"));
		Assert.assertTrue(s, s.length() > prevlen);
	}

	@Test
	public void testLoggingOff() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LoggerAdapter logger = new LoggerAdapter("testLevelOff", pstrm, Defs.LOGLEVEL.OFF);
		Assert.assertFalse(logger.isErrorEnabled());
		Assert.assertFalse(logger.isWarnEnabled());
		Assert.assertFalse(logger.isInfoEnabled());
		Assert.assertFalse(logger.isDebugEnabled());
		Assert.assertFalse(logger.isTraceEnabled());

		logger.error("error1");
		logger.warn("warn1");
		logger.info("info1");
		logger.debug("debug1");
		logger.trace("trace1");
		String s = bstrm.toString();
		Assert.assertTrue(s, s.isEmpty());
	}
}
