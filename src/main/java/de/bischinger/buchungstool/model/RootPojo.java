package de.bischinger.buchungstool.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by bischofa on 05/12/16.
 */
@MappedSuperclass
public class RootPojo implements Serializable
{
	private static final long serialVersionUID = 4123679033767127757L;

	@Id
	@GeneratedValue
	protected long id;

	public long getId()
	{
		return id;
	}
}
