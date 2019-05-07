package mujava.op.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;

public class LogReduction {
  public static final String LOGGER_NAME = "nimrod";
  public static final String DEFAULT_PATH = ".";
  public static final boolean AVOID = true;


  /**
   * Log the reduction. That is, the mutants that were not generated thanks to heuristics
   * Eg.: Utils.logAppend(safeRefactorModel.getSession().getPath(), "equivalent", line);
   *
   * @param path
   * @param fileName
   * @param lines
   * @throws IOException
   */
  public static void logAppend(String path, String fileName, List<String> lines) throws IOException {
	File f = new File(path + "/" + LOGGER_NAME + "_" + fileName + ".log");
	Files.write(f.toPath(), lines, UTF_8, APPEND, CREATE);
  }

  public static void logWrite(String path, String fileName, List<String> lines) throws IOException {
	File f = new File(path + "/" + LOGGER_NAME + "_" + fileName + ".log");
	Files.write(f.toPath(), lines, UTF_8, WRITE, CREATE);
  }


}
