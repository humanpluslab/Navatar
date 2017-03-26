/** 
 * Used for calling protoc to convert proto files to java files. Each Navatar class that can be
 * populated by GIS data is converted to a protobuf class. This file is the main file in the
 * protobuf projects that creates the protobuf converter java classes for Navatar.
 */

package com.navatar.protobufs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main class com.navatar.protobufs.
 * 
 * Used for calling protoc to convert proto files to java files. Each Navatar class that can be
 * populated by GIS data is converted to a protobuf class. This file is the main file in the
 * protobuf projects that creates the protobuf converter java classes for Navatar.
 */
public class Protobufs {
  /**
   * Function to create protobuf java classes. The function will exit early if a class fails to
   * compile and output a message indicating which class.
   * 
   * @param args
   *          cmd input
   * 
   * @throws ioexception
   * 
   * @throws interruptedexception
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    String os = System.getProperty("os.name");
    ProcessBuilder builder;
    if (os.startsWith("Windows"))
      builder = new ProcessBuilder("cmd", "/c", "DEL gen\\*Proto.java");
    else
      builder = new ProcessBuilder("sh", "-c", "rm -rf gen/*Proto.java");
    builder.redirectErrorStream(true);
    Process process = builder.start();
    printOutput(process);
    // TODO: fix for windows
    builder.directory(new File("src/com/navatar/protobufs/"));
    if (os.startsWith("Windows"))
      builder.command("cmd", "/c", "protoc.exe", "--java_out=..\\..\\..\\..\\gen", "*Proto.proto");
    else
      builder.command("sh", "-c", "protoc --java_out=../../../../gen *Proto.proto");
    process = builder.start();
    printOutput(process);
    builder.directory(new File("../../../../"));
    System.out.println("Exported protobufs.");

  }

  private static void printOutput(Process process) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String line;
    try {
      while ((line = reader.readLine()) != null)
        System.out.println("Stdout: " + line);
    } catch (IOException e) {
      System.err.println("Could not read output stream.");
    }
  }
}