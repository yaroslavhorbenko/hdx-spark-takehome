# Custom DataSource V2 Reader with Directory-Based Partitioning and Optimized String Searching

## Overview
Spark DataSource V2 reader for a custom file format (`.log`) has been implemented in this repo.
It has built-in support for directory-based partitioning and filter push-down for string searches.

## General Approach
1. **Directory-Based Partitioning and Parsing:**
    - **Directory Scan**. Reader treats each subdirectory containing a `.log` file as a separate partition. 
    in the case subdirectory is empty or doesn't have `.log` file it's ignored.
    - **Parsing**. Each `partition.log` file within the directories is read line by line, and each line is parsed into a Row object with two fields: an integer (epoch timestamp) and a string. 
    The values on each line should be separated by a pipe (`|`).
    Parsing logic is implemented using regexp, lines with incorrect format are simply ignored with warning message produced. 
    Such approach allows to parse as much valid rows as possible.
    Line iterator is used in partition reader, it allows process even big files without any potential memory issues.  

2. **String Search Optimization:**
    - **Filters push-down**  feature allows us to perform highly efficient filtering on data-source level before data is even loaded into Spark.
    Particularly for `LIKE` operation on `data` column we have used push down to directly filter out values using Scala’s regular expressions.

## Code overview
Code base consist of several packages. Let's review the most important: 
1. Test application lives here `io.hydrolix.connectors.spark.TakeHomeSimpleApp`. It creates local spark instance and performs `LIKE` query under table from our catalog
2. There are two classes in root connector's package `io.hydrolix.connectors.spark`, those are `HdxTakeHomeCatalog` and `HdxTakeHomeTable`. 
Catalog changes include config parameter support for data folder, it better than simply hardcoded it. Also partition directories scan is performed there. 
Table implementation hasn't been changed so much, just partition dirs were added into constructor and propagated to `PushDownScanBuilder` class instance
3. Utils package `io.hydrolix.connectors.spark.utils` consist of FileUtils object which utilizes methods to scan directory tree and list files in directory
4. Parse package `io.hydrolix.connectors.spark.parse` consist of parser for line from .log file. Parser returns `Option` value, so in the case line parsing has failed `None` is returned
5. There are several files in the `io.hydrolix.connectors.spark.partition` package. `DataDirInputPartition` is just custom partition class with directory path added. 
The `DataPartitionReaderFactory` creates separate partition instance for each directory. A lot of work happens in `DataDirPartitionReader` - iteration over file lines, parsing and filtering
6. `PushDownScanBuilder` is the most interesting in `io.hydrolix.connectors.spark.scan` package. It specifies supported push down filters which are used later in `DataDirInputPartition` class

## Getting Started

### 1. Clone repo

```
git clone https://github.com/yaroslavhorbenko/hdx-spark-takehome.git
```

```
cd hdx-spark-takehome
```
Test data is placed into `data` directory in the root folder

### 2. Run the application
There are two ways of testing connector implementation:
1. Run application using SBT.
    Please make sure JDK 11+ and SBT are installed on you local machine
    Simply run following command in root of cloned repo
    ```
    sbt run
    ```

2. Another way is to build and run a docker image, then run a query in the spark-shell

    Please run set of commands to build a docker image locally:
    ```
    docker buildx create --name mybuilder --driver docker-container --use
    docker buildx build --load -t hdx-spark-takehome:local .
    ```
    In order to run & enter docker container in a Spark shell, simply type:
    ```
    docker run -it --rm hdx-spark-takehome:local spark-shell
    ```
    Put the following command into Spark shell and see filtered data output
    ```
    spark.sql("SELECT * FROM hdx.takehome.logs where data LIKE '%partition2%'").show(false)
    ```