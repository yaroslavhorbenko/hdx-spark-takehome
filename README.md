# Getting Started

## 1. Fork this Repository

Click the ‘Fork’ button at the top right of this page to create your own copy of this repository.

## 2. Clone Your Fork

```
git clone https://github.com/hydrolix/hdx-spark-takehome.git
cd hdx-spark-takehome
```

## 3. Pull the Pre-built Docker Image

`docker pull fedrod/hdx-spark-takehome:latest`

## 4. Verify boilerplate is working

```
docker run -it --rm fedrod/hdx-spark-takehome:latest spark-shell
spark.sql("SELECT COUNT(*) FROM hdx.takehome.logs").show(false)
```

## 5. Implement Your Solution

Add your Scala code files to the src/ directory in your local repository.
Implement your custom DataSource V2 reader as per the assignment requirements.

## 6. Run and Test Your Implementation

`docker run -it --rm fedrod/hdx-spark-takehome:latest spark-shell`

This will run an entrypoint into the Spark shell. In the shell, you can test your implementation:
`spark.sql("SELECT * FROM hdx.takehome.logs").show(false)`

To enter docker container in a bash shell, simply run
`docker run -it --rm fedrod/hdx-spark-takehome:latest`

## 7. Submit Your Solution

Push your changes to your fork
Create a pull request to the original repository
