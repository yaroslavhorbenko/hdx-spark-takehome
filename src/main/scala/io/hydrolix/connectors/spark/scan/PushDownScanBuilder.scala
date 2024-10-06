package io.hydrolix.connectors.spark.scan

import org.apache.spark.internal.Logging
import org.apache.spark.sql.connector.read.{Scan, SupportsPushDownFilters}
import org.apache.spark.sql.sources.{Filter, IsNotNull, StringContains}

class PushDownScanBuilder(dataDirs: List[String]) extends SupportsPushDownFilters with Logging {
  private var filters = Array.empty[Filter]

  override def build(): Scan = {
    new HdxTakeHomeScan(dataDirs, filters.toList)
  }

  override def pushFilters(predicates: Array[Filter]): Array[Filter] = {
    val (supported, unsupported) = predicates.partition {
      case IsNotNull(_)         => true
      case StringContains(_, _) => true
      case _                    => false
    }
    this.filters = supported
    log.info(s"Supported push down filters detected: ${supported.toList}")
    unsupported
  }

  override def pushedFilters(): Array[Filter] = {
    log.info(s"Pushed filters: ${filters.toList}")
    filters
  }
}
