package io.hydrolix.connectors.spark.utils

import java.nio.file.{FileSystems, Files, Path}
import scala.jdk.CollectionConverters._

object FileUtils {

  val DATA_FILE_EXTENSION = ".log"

  def listFiles(dirPath: String, fileExtension: String, recursively: Boolean = false): List[Path] = {
    val dir = FileSystems.getDefault.getPath(dirPath)
    val all = if (recursively) Files.walk(dir).iterator().asScala.toList else Files.walk(dir).iterator().asScala.toList
    all.filter(f => Files.isRegularFile(f) && f.getFileName.toString.endsWith(fileExtension))
  }

  def findNonEmptyDataFolders(allDataFiles: List[Path]): List[String] = {
    allDataFiles.map(_.getParent.toString).distinct
  }
}
