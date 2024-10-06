package io.hydrolix.connectors.spark.partition

import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.{InputPartition, PartitionReader, PartitionReaderFactory}
import org.apache.spark.sql.sources.Filter
import org.apache.spark.sql.types.StructType

class DataPartitionReaderFactory(schema: StructType, filters: List[Filter])
    extends PartitionReaderFactory
    with Logging {

  override def createReader(partition: InputPartition): PartitionReader[InternalRow] = {
    val dirPartition = partition.asInstanceOf[DataDirInputPartition]
    log.info(s"Partition reader is created for partition: ${dirPartition.path}")
    new DataDirPartitionReader(dirPartition, schema, filters)
  }
}
