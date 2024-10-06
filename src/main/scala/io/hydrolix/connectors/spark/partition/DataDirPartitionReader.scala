package io.hydrolix.connectors.spark.partition

import io.hydrolix.connectors.spark.parse.TextRowParser
import io.hydrolix.connectors.spark.utils.FileUtils
import io.hydrolix.connectors.spark.utils.FileUtils.DATA_FILE_EXTENSION
import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.PartitionReader
import org.apache.spark.sql.sources.{Filter, IsNotNull, StringContains}
import org.apache.spark.sql.types.{StringType, StructType}

import scala.io.Source

class DataDirPartitionReader(partition: DataDirInputPartition, schema: StructType, filters: List[Filter])
    extends PartitionReader[InternalRow]
    with Logging {

  private val files = FileUtils.listFiles(partition.path, DATA_FILE_EXTENSION)

  if (files.isEmpty)
    throw new IllegalArgumentException(
      s"Partition error: no .log files are found in the partition folder, path: ${partition.path}"
    )

  if (files.length > 1)
    throw new IllegalArgumentException(
      s"Partition error: more than one .log file in the partition folder, path: ${partition.path}"
    )

  private val source                  = Source.fromFile(files.head.toString)
  private val rowParser               = new TextRowParser(partition)
  private val rowsIterator            = parseAndFilter(source.getLines())
  private var currentRow: InternalRow = _

  private def parseAndFilter(linesIterator: Iterator[String]): Iterator[InternalRow] = {
    linesIterator.flatMap { line =>
      rowParser.parse(line).flatMap { parsed =>
        Some(parsed).filter(passFilters)
      }
    }
  }

  private def passFilters(row: InternalRow): Boolean = {
    for (filter <- filters)
      filter match {
        case IsNotNull(x) =>
          if (row.get(schema.fieldIndex(x), StringType) == null) return false
        case StringContains(attribute, value) =>
          val index = schema.fieldIndex(attribute)
          if (!contains(row.getString(index), value)) return false
        case _ =>
      }
    true
  }

  private def contains(str: String, predicate: String): Boolean = {
    val pattern = """.*%s.*$""".format(predicate).r
    pattern.findFirstMatchIn(str) match {
      case Some(_) => true
      case None => false
    }
  }

  override def next(): Boolean = {
    if (rowsIterator.hasNext) {
      currentRow = rowsIterator.next()
      true
    } else {
      false
    }
  }

  override def get(): InternalRow = {
    currentRow
  }

  override def close(): Unit = source.close()
}
