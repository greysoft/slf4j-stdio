/*
 * Copyright 2014-2021 Yusef Badri - All rights reserved.
 * grey-slf4j-logstdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package org.slf4j.impl;

public class StaticMarkerBinder
	implements org.slf4j.spi.MarkerFactoryBinder
{
	public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

	private final org.slf4j.IMarkerFactory markerFactory = new org.slf4j.helpers.BasicMarkerFactory();

	private StaticMarkerBinder() {}

	@Override
	public org.slf4j.IMarkerFactory getMarkerFactory()
	{
		return markerFactory;
	}

	@Override
	public String getMarkerFactoryClassStr()
	{
		return getMarkerFactory().getClass().getName();
	}
}
