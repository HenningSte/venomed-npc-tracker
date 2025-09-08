package com.venomnpctracker;

import lombok.Getter;

@Getter
public enum DisplayMode
{
	LEVEL("Level", 0),
	NAME("Name", 1),
	BOTH("Both", 2),
	ICONONLY("Icon Only", 3);

	private final String name;
	private final int id;

	DisplayMode(String name, int id)
	{
		this.name = name;
		this.id = id;
	}
}