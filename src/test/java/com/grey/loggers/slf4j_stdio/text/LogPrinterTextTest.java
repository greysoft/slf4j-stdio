/*
 * Copyright 2014-2021 Yusef Badri - All rights reserved.
 * grey-slf4j-stdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio.text;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import com.grey.loggers.slf4j_stdio.Defs;

public class LogPrinterTextTest {
	@Test
	public void testDefaultFormat() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LogPrinterText printer = new LogPrinterText(pstrm);
		String logname = "logname1";
		Defs.LOGLEVEL lvl = Defs.LOGLEVEL.DEBUG;
		String timestamp = "time1";
		String text = "random msg";
		Thread thrd = Thread.currentThread();
		String expected = timestamp+" "+lvl.name()+" TID-"+thrd.getId()+"/"+thrd.getName()+" "+logname+" "+text;
		printer.renderLog(logname, timestamp, lvl, text, null);
		String logmsg = bstrm.toString().trim();
		Assert.assertEquals(expected, logmsg);
	}

	@Test
	public void testMinimalFormat() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LogPrinterText printer = new LogPrinterText(pstrm, "{MSG}");
		String text = "random msg";
		printer.renderLog("logname1", "time1", Defs.LOGLEVEL.INFO, text, null);
		String logmsg = bstrm.toString().trim();
		Assert.assertEquals(text, logmsg);
	}

	@Test
	public void testException() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LogPrinterText printer = new LogPrinterText(pstrm, "{MSG}");
		String text = "random msg";
		Exception error = new java.io.IOException("Simulated exception");
		printer.renderLog("logname1", "time1", Defs.LOGLEVEL.INFO, text, error);
		String logmsg = bstrm.toString().trim();
		Assert.assertTrue(text, logmsg.startsWith(text));
		Assert.assertTrue(text, logmsg.contains("java.io.IOException: Simulated exception"));
		Assert.assertTrue(text, logmsg.contains("at "+getClass().getName()+".testException"));
	}

	@Test
	public void testEscapedFormat() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		String fmt = "Intro {{ {{{MSG} {{{{ end";
		String text = "random msg";
		String expected = "Intro { {"+text+" {{ end";
		LogPrinterText printer = new LogPrinterText(pstrm, fmt);
		printer.renderLog("logname1", "time1", Defs.LOGLEVEL.INFO, text, null);
		String logmsg = bstrm.toString().trim();
		Assert.assertEquals(expected, logmsg);
	}

	// Test an escape sequence which unwittingly hides a token, but does not result in an any errors
	@Test
	public void testEscapedToken() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		String fmt = "Intro {{ {{MSG} end";
		String expected = "Intro { {MSG} end";
		LogPrinterText printer = new LogPrinterText(pstrm, fmt);
		printer.renderLog("logname1", "time1", Defs.LOGLEVEL.INFO, "random msg", null);
		String logmsg = bstrm.toString().trim();
		Assert.assertEquals(expected, logmsg);
	}

	@Test
	public void testRedundantTokenClose() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		String fmt = "Intro {MSG}} }} end";
		String text = "random msg";
		String expected = "Intro "+text+"} }} end";
		LogPrinterText printer = new LogPrinterText(pstrm, fmt);
		printer.renderLog("logname1", "time1", Defs.LOGLEVEL.INFO, text, null);
		String logmsg = bstrm.toString().trim();
		Assert.assertEquals(expected, logmsg);
	}

	@Test
	public void testUnterminatedToken() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		String fmt = "Intro {MSG end";
		try {
			new LogPrinterText(pstrm, fmt);
			Assert.fail("Expected to fail on invalid format: "+fmt);
		} catch (IllegalArgumentException ex) {} //ok
	}

	@Test
	public void testInvalidToken() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		String fmt = "Intro {badtoken} {MSG} end";
		try {
			new LogPrinterText(pstrm, fmt);
			Assert.fail("Expected to fail on invalid format: "+fmt);
		} catch (IllegalArgumentException ex) {} //ok
	}

	@Test
	public void testFormatCase() {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LogPrinterText printer = new LogPrinterText(pstrm, "{msg}");
		String text = "random msg";
		printer.renderLog("logname1", "time1", Defs.LOGLEVEL.INFO, text, null);
		String logmsg = bstrm.toString().trim();
		Assert.assertEquals(text, logmsg);
	}
}
