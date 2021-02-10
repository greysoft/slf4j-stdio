/*
 * Copyright 2014-2021 Yusef Badri - All rights reserved.
 * grey-slf4j-stdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio.json;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.grey.loggers.slf4j_stdio.Defs;
import com.grey.loggers.slf4j_stdio.LogPrinter;

import org.junit.Assert;
import org.junit.Test;

public class LogPrinterJsonTest {

	private static final ObjectReader Deserialiser;
	static {
		ObjectMapper om = new ObjectMapper();
		Deserialiser = om.readerFor(Map.class);
	}

	@Test
	public void testBasic() throws Exception {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LogPrinter printer = new LogPrinterJson(pstrm);

		String logname = "logname1";
		Defs.LOGLEVEL lvl = Defs.LOGLEVEL.DEBUG;
		String timestamp = "time1";
		String msg = "random { \" text";
		printer.renderLog(logname, timestamp, lvl, msg, null);

		String json = bstrm.toString();
		Map<String, ?> log = Deserialiser.readValue(json);
		Thread thrd = Thread.currentThread();
		Assert.assertEquals(json, timestamp, log.get("time"));
		Assert.assertEquals(json, lvl.name(), log.get("level"));
		Assert.assertEquals(json, (int)thrd.getId(), log.get("tid"));
		Assert.assertEquals(json, thrd.getName(), log.get("tname"));
		Assert.assertEquals(json, logname, log.get("logname"));
		Assert.assertEquals(json, msg, log.get("msg"));
		Assert.assertFalse(json, log.containsKey("error"));
	}

	@Test
	public void testBlankMessage() throws Exception {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LogPrinter printer = new LogPrinterJson(pstrm);

		String logname = "logname1";
		Defs.LOGLEVEL lvl = Defs.LOGLEVEL.DEBUG;
		String timestamp = "time1";
		String msg = "";
		printer.renderLog(logname, timestamp, lvl, msg, null);

		String json = bstrm.toString();
		Map<String, ?> log = Deserialiser.readValue(json);
		Thread thrd = Thread.currentThread();
		Assert.assertEquals(json, timestamp, log.get("time"));
		Assert.assertEquals(json, lvl.name(), log.get("level"));
		Assert.assertEquals(json, (int)thrd.getId(), log.get("tid"));
		Assert.assertEquals(json, thrd.getName(), log.get("tname"));
		Assert.assertEquals(json, logname, log.get("logname"));
		Assert.assertFalse(json, log.containsKey("msg"));
		Assert.assertFalse(json, log.containsKey("error"));
	}

	@Test
	public void testException() throws Exception {
		ByteArrayOutputStream bstrm = new ByteArrayOutputStream();
		PrintStream pstrm = new PrintStream(bstrm);
		LogPrinter printer = new LogPrinterJson(pstrm);

		String logname = "logname1";
		Defs.LOGLEVEL lvl = Defs.LOGLEVEL.DEBUG;
		String timestamp = "time1";
		String msg = "random text";
		Exception error = new java.io.IOException("Simulated exception");
		printer.renderLog(logname, timestamp, lvl, msg, error);

		String json = bstrm.toString();
		Map<String, ?> log = Deserialiser.readValue(json);
		Thread thrd = Thread.currentThread();
		Assert.assertEquals(json, timestamp, log.get("time"));
		Assert.assertEquals(json, lvl.name(), log.get("level"));
		Assert.assertEquals(json, (int)thrd.getId(), log.get("tid"));
		Assert.assertEquals(json, thrd.getName(), log.get("tname"));
		Assert.assertEquals(json, logname, log.get("logname"));
		Assert.assertEquals(json, msg, log.get("msg"));

		String exmsg = (String)log.get("error");
		Assert.assertTrue(json, log.containsKey("error"));
		Assert.assertTrue(json, exmsg.startsWith("java.io.IOException: Simulated exception"));
		Assert.assertTrue(json, exmsg.contains("at "+getClass().getName()+".testException"));
	}
}
