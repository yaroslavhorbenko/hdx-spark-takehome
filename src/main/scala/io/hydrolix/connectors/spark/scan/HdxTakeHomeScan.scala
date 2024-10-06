package io.hydrolix.connectors.spark.scan

import io.hydrolix.connectors.spark.partition.{DataDirInputPartition, DataPartitionReaderFactory}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.connector.read._
import org.apache.spark.sql.sources.Filter
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

class HdxTakeHomeScan(dataDirs: List[String], filters: List[Filter]) extends Scan with Batch with Logging {

  override def readSchema(): StructType = StructType(
    Seq(
      StructField("id", IntegerType, nullable = true),
      StructField("data", StringType, nullable = true)
    )
  )

  override def planInputPartitions(): Array[InputPartition] = {
    dataDirs.map(DataDirInputPartition).toArray
  }

  override def createReaderFactory(): PartitionReaderFactory = new DataPartitionReaderFactory(readSchema(), filters)

  override def toBatch: Batch = this
}
