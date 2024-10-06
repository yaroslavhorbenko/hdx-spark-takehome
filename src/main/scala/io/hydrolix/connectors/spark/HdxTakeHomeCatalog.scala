package io.hydrolix.connectors.spark

import io.hydrolix.connectors.spark.HdxTakeHomeCatalog.ROOT_DATA_DIR_KEY
import io.hydrolix.connectors.spark.spark.nope
import io.hydrolix.connectors.spark.utils.FileUtils
import io.hydrolix.connectors.spark.utils.FileUtils.DATA_FILE_EXTENSION
import org.apache.spark.internal.Logging
import org.apache.spark.sql.connector.catalog._
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.util.Collections
import java.{util => ju}

object HdxTakeHomeCatalog {
  val ROOT_DATA_DIR_KEY = "data.dir"
}

class HdxTakeHomeCatalog extends TableCatalog with SupportsNamespaces with Logging {

  private var tables: Map[Identifier, Table] = Map()

  override def initialize(name: String, options: CaseInsensitiveStringMap): Unit = {
    if (!options.containsKey(ROOT_DATA_DIR_KEY))
      throw new IllegalArgumentException(
        s"Root data directory is not configured. " +
          s"Please make sure key spark.sql.catalog.hdx.$ROOT_DATA_DIR_KEY is specified in config"
      )
    val rootDataDir    = options.get(ROOT_DATA_DIR_KEY)
    val all            = FileUtils.listFiles(rootDataDir, DATA_FILE_EXTENSION, recursively = true)
    val dataDirs       = FileUtils.findNonEmptyDataFolders(all)
    val logsIdentifier = Identifier.of(Array("takehome"), "logs")
    tables += (logsIdentifier -> HdxTakeHomeTable(dataDirs))
  }

  override def name(): String = "hdx"

  override def listTables(namespace: Array[String]): Array[Identifier] =
    tables.keys.toArray

  override def loadTable(ident: Identifier): Table =
    tables.getOrElse(ident, throw new IllegalArgumentException(s"Table ${ident.name()} not found."))

  override def listNamespaces(): Array[Array[String]] =
    Array(Array(name()))

  override def listNamespaces(namespace: Array[String]): Array[Array[String]] = Array()

  override def loadNamespaceMetadata(namespace: Array[String]): ju.Map[String, String] = Collections.emptyMap()

  // noinspection ScalaDeprecation
  override def createTable(
    ident: Identifier,
    schema: StructType,
    partitions: Array[Transform],
    properties: ju.Map[String, String]
  ): Table = nope()
  override def alterTable(ident: Identifier, changes: TableChange*): Table                       = nope()
  override def dropTable(ident: Identifier): Boolean                                             = nope()
  override def renameTable(oldIdent: Identifier, newIdent: Identifier): Unit                     = nope()
  override def createNamespace(namespace: Array[String], metadata: ju.Map[String, String]): Unit = nope()
  override def alterNamespace(namespace: Array[String], changes: NamespaceChange*): Unit         = nope()
  override def dropNamespace(namespace: Array[String], cascade: Boolean): Boolean                = nope()
}

package object spark {
  def nope() = throw new UnsupportedOperationException("HydrolixTakeHome connector is read-only")
}
