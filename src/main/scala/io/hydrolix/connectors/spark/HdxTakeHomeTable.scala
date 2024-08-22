package io.hydrolix.connectors.spark

import org.apache.spark.sql.catalyst.util.quoteIfNeeded
import org.apache.spark.sql.connector.catalog.{SupportsRead, Table, TableCapability}
import org.apache.spark.sql.connector.read.{Batch, InputPartition, PartitionReader, PartitionReaderFactory, Scan, ScanBuilder}
import org.apache.spark.sql.types.{StructType, StructField, IntegerType, StringType}
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.{util => ju}

case class HdxTakeHomeTable()
  extends Table
     with SupportsRead {

  override def name(): String = "hdx.takehome.logs"

  override def schema(): StructType = StructType(Seq(
    StructField("id", IntegerType, nullable = true),
    StructField("data", StringType, nullable = true)
  ))

  override def capabilities(): ju.Set[TableCapability] = ju.EnumSet.of(TableCapability.BATCH_READ)

  override def newScanBuilder(options: CaseInsensitiveStringMap): ScanBuilder = new ScanBuilder {
    override def build(): Scan = new HdxTakeHomeScan
  }
}
