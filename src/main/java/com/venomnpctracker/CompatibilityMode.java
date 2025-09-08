package com.venomnpctracker;

import lombok.Getter;

@Getter
public enum CompatibilityMode
{
	INTEGRATE("Integrate", 0),
	OVERRIDE("Override", 1);

	private final String name;
	private final int id;

	CompatibilityMode(String name, int id)
	{
		this.name = name;
		this.id = id;
	}
}
