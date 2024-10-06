package io.hydrolix.connectors.spark.partition

import org.apache.spark.sql.connector.read.InputPartition

case class DataDirInputPartition(path: String) extends InputPartition
