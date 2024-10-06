package io.hydrolix.connectors.spark

import io.hydrolix.connectors.spark.scan.PushDownScanBuilder
import org.apache.spark.internal.Logging
import org.apache.spark.sql.connector.catalog.{SupportsRead, Table, TableCapability}
import org.apache.spark.sql.connector.read.ScanBuilder
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.{util => ju}

case class HdxTakeHomeTable(dataDirs: List[String]) extends Table with SupportsRead with Logging {

  override def name(): String = "hdx.takehome.logs"

  override def schema(): StructType = StructType(
    Seq(
      StructField("id", IntegerType, nullable = true),
      StructField("data", StringType, nullable = true)
    )
  )

  override def capabilities(): ju.Set[TableCapability] = ju.EnumSet.of(TableCapability.BATCH_READ)

  override def newScanBuilder(options: CaseInsensitiveStringMap): ScanBuilder = new PushDownScanBuilder(dataDirs)
}
