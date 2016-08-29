package com.navatar.output.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextFile
{
	StringBuilder text;
	String filename;
	
	public TextFile(String filename)
	{
		this.filename = filename;
		this.text = new StringBuilder();
	}
	
	public boolean exists() {
		
		File file = new File(filename);
		return file.exists();
	}

	/**
	 * It appends the string to the previous text appended. 
	 * 
	 * <p>This function <b>DOES NOT</b> write the string in the file. It is only used to
	 * build the content of the file. The reason for doing so is to avoid open, write, 
	 * and close the file multiple time, which could lead to major performance overhead.
	 * For writing the content in the file use {@link writeFile} after calling append.
	 * 
	 * @param text The text to be added to the file.
	 * @see writeFile
	 */
	public void append(String text){
		this.text.append(text);
	}
	
	/**
	 * Writes the text in the file. 
	 * 
	 * <p>This method handles the writing of the content in the actual file.
	 * 
	 * @param append Declares if the changes should be appended in the file or not.
	 * @throws IOException
	 */
	public void writeFile(boolean append) throws IOException
	{
		FileWriter fout = new FileWriter(filename, append);
		fout.write(text.toString());
		fout.close();

		this.text = new StringBuilder();
	}
}