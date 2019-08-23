package xyz.pixelatedw.bizarremod.api;

import java.lang.management.ManagementFactory;

public class WyDebug
{
	public static boolean isDebug()
	{
		return ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}
}