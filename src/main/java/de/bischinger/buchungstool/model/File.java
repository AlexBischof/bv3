package de.bischinger.buchungstool.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Alex Bischof on 23.01.2017.
 */
@Entity
public class File extends RootPojo
{
	private static final long serialVersionUID = 7601741731159213692L;

	@Basic(optional = false)
	@Column(nullable = false)
	private String file;

	//	@OneToMany(fetch = EAGER)
	//private List<Warnung> warnungList;

	public String getFile()
	{
		return file;
	}

	public void setFile(String file)
	{
		this.file = file;
	}

	@Override public String toString()
	{
		return "File{" +
				"file='" + file + '\'' +
				'}';
	}
}
