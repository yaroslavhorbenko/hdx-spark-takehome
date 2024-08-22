package io.hydrolix.connectors.spark

import org.apache.spark.sql.catalyst.util.quoteIfNeeded
import org.apache.spark.sql.connector.catalog.{SupportsRead, Table, TableCapability}
import org.apache.spark.sql.connector.read.{
  Batch,
  InputPartition,
  PartitionReader,
  PartitionReaderFactory,
  Scan,
  ScanBuilder
}
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.{util => ju}

class HdxTakeHomeScan extends Scan with Batch {

  override def readSchema(): StructType = StructType(
    Seq(
      StructField("id", IntegerType, nullable = true),
      StructField("data", StringType, nullable = true)
    )
  )

  override def planInputPartitions(): Array[InputPartition] = Array.empty

  override def createReaderFactory(): PartitionReaderFactory = new PartitionReaderFactory {
    override def createReader(partition: InputPartition): PartitionReader[InternalRow] =
      new PartitionReader[InternalRow] {
        override def next(): Boolean = false
        override def get(): InternalRow = null
        override def close(): Unit = {}
      }
  }

  override def toBatch: Batch = this
}
