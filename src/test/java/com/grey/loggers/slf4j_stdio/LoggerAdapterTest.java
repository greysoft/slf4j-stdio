/*
 * Copyright 2014-2022 Yusef Badri - All rights reserved.
 * grey-slf4j-logstdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerAdapterTest {
	@Before
	public void setup() {
		LoggerAdapter.setDefaultStream(null);
	}

	@Test
	public void testAutoInstantiation() {
		Logger log = LoggerFactory.getLogger(getClass());
		Assert.assertSame(log.getClass(), LoggerAdapter.class);
		Logger log2 = LoggerFactory.getLogger(getClass());
		Assert.assertSame(log2.getClass(), LoggerAdapter.class);
		log2 = LoggerFactory.getLogger("AnotherLogger");
		Assert.assertSame(log2.getClass(), LoggerAdapter.class);
		Assert.assertNotSame(log2, log);

		//we just verify the output manually here
		log.debug("Test debug msg"); //shouldn't come out
		log.info("Test msg");
		log2.info("Test msg from logger2");
		log.warn("Test msg with param1={}", "val1");
		log.info("Test msg with param1={}, param2={}", "val1", "val2");
		log.error("Test msg with param1={}, param2={}, param3={}", "val1", "val2", "val3");
		log.info("Test msg with param1={} and Throwable", "val1", new Exception("Dumy Exception"));
		log.info("Test msg with no params and Throwable", new Exception("Dumy Exception"));
	}

	@Test
	public void testPositionalParams() {
		String logprefix = getClass().getName()+" ";
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LoggerAdapter.setDefaultStream(pstrm);
		Logger logger = LoggerFactory.getLogger(getClass());
		Assert.assertSame(logger.getClass(), LoggerAdapter.class);

		logger.info("Message with no params");
		String s = bstrm.toString().trim();
		Assert.assertTrue(s, s.endsWith(logprefix+"Message with no params"));
		bstrm.reset();

		logger.info("Intro {} end", "p1");
		s = bstrm.toString().trim();
		Assert.assertTrue(s, s.endsWith(logprefix+"Intro p1 end"));
		bstrm.reset();

		logger.info("Intro {} middle {} end", "p1", "p2");
		s = bstrm.toString().trim();
		Assert.assertTrue(s, s.endsWith(logprefix+"Intro p1 middle p2 end"));
		bstrm.reset();

		logger.info("Intro {} middle {} more {} end", "p1", "p2", "p3");
		s = bstrm.toString().trim();
		Assert.assertTrue(s, s.endsWith(logprefix+"Intro p1 middle p2 more p3 end"));
		bstrm.reset();

		logger.info("Intro {} middle {} more {} more2 {} end", "p1", "p2", "p3", "p4");
		s = bstrm.toString().trim();
		Assert.assertTrue(s, s.endsWith(logprefix+"Intro p1 middle p2 more p3 more2 p4 end"));
		bstrm.reset();

		logger.info("Extra param", "p99");
		s = bstrm.toString().trim();
		Assert.assertTrue(s, s.endsWith(logprefix+"Extra param"));
		bstrm.reset();

		Exception ex = new Exception("Dummy Exception");
		logger.info("With error {}", "p1", ex);
		s = bstrm.toString().trim();
		Assert.assertTrue(s, s.contains(logprefix+"With error p1\n"+ex));
		Assert.assertTrue(s, s.contains("at "+getClass().getName()+".testPositionalParams("));
		bstrm.reset();

	}

	// Test the default logging level of Info
	@Test
	public void testLevelInfo() {
		String lname = "testLevels";
		String logprefix = lname+" ";
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LoggerAdapter logger = new LoggerAdapter(lname, pstrm, Defs.LOGLEVEL.INFO, null);
		Assert.assertTrue(logger.isErrorEnabled());
		Assert.assertTrue(logger.isWarnEnabled());
		Assert.assertTrue(logger.isInfoEnabled());
		Assert.assertFalse(logger.isDebugEnabled());
		Assert.assertFalse(logger.isTraceEnabled());

		logger.warn("msg1");
		String s = bstrm.toString();
		int prevlen = s.length();
		Assert.assertTrue(s, s.endsWith(logprefix+"msg1\n"));

		logger.info("msg2");
		s = bstrm.toString();
		Assert.assertTrue(s, s.endsWith(logprefix+"msg2\n"));
		Assert.assertTrue(s, s.length() > prevlen);
		prevlen = s.length();

		logger.debug("msg3");
		s = bstrm.toString();
		Assert.assertTrue(s, s.endsWith(logprefix+"msg2\n"));
		Assert.assertEquals(s, prevlen, s.length());
	}

	// ensure that Error-level logging only prints errors
	@Test
	public void testLevelError() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LoggerAdapter logger = new LoggerAdapter("testLevelDebug", pstrm, Defs.LOGLEVEL.ERROR, null);
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
		LoggerAdapter logger = new LoggerAdapter("testLevelDebug", pstrm, Defs.LOGLEVEL.WARN, null);
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
		LoggerAdapter logger = new LoggerAdapter("testLevelDebug", pstrm, Defs.LOGLEVEL.DEBUG, null);
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
		LoggerAdapter logger = new LoggerAdapter("testLevelTrace", pstrm, Defs.LOGLEVEL.TRACE, null);
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
		LoggerAdapter logger = new LoggerAdapter("testLevelOff", pstrm, Defs.LOGLEVEL.OFF, null);
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
