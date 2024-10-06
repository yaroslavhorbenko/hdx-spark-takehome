package io.hydrolix.connectors.spark.parse

import io.hydrolix.connectors.spark.partition.DataDirInputPartition
import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.unsafe.types.UTF8String

class TextRowParser(partition: DataDirInputPartition) extends Logging {

  private val pattern = """(\d+)\|(.+)$""".r

  def parse(row: String): Option[InternalRow] = {
    try {
      val res = pattern.findFirstMatchIn(row).flatMap { parsed =>
        val ts = parsed.group(1)
        if (ts.forall(_.isDigit)) {
          Some(InternalRow.apply(ts.toInt, UTF8String.fromString(parsed.group(2))))
        } else None
      }
      if (res.isEmpty)
        log.warn(s"Parsing failed - invalid row format, partition:${partition.path}, row:$row")
      res
    } catch {
      case e: Error =>
        log.error(s"Unexpected parsing failure for partition: ${partition.path}, row: $row, error: ${e.getMessage}", e)
        None
    }
  }

}
