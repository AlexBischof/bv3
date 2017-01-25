package de.siteos.les;

import java.io.Serializable;

@javax.inject.Named
@javax.faces.view.ViewScoped
@SuppressWarnings("serial")
public class Version implements Serializable
{
	private static final String VERSION = "${project.version}";
	private static final String TIMESTAMP = "${maven.build.timestamp}";

	public String getVersion()
	{
		return VERSION;
	}

	public String getTimestamp()
	{
		return TIMESTAMP;
	}
}