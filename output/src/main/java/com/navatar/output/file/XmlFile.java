package com.navatar.output.file;

public class XmlFile extends TextFile
{
	public XmlFile(String filename)
	{
		super(filename);
		this.text.append("<?xml version=\"1.0\" encoding='UTF-8'?>\n");
	}
}
