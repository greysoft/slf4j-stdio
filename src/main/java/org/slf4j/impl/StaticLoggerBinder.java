/*
 * Copyright 2014-2021 Yusef Badri - All rights reserved.
 * grey-slf4j-stdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package org.slf4j.impl;

public class StaticLoggerBinder
	implements org.slf4j.spi.LoggerFactoryBinder
{
	private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

	private final org.slf4j.ILoggerFactory factory = new com.grey.loggers.slf4j_stdio.LoggerFactory();

	public static final StaticLoggerBinder getSingleton()
	{
		return SINGLETON;
	}

	private StaticLoggerBinder() {}

	@Override
	public org.slf4j.ILoggerFactory getLoggerFactory()
	{
		return factory;
	}

	@Override
	public String getLoggerFactoryClassStr()
	{
		return getLoggerFactory().getClass().getName();
	}
}
