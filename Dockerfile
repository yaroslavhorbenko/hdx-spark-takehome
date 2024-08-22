# Build instructions:
# docker buildx create --name mybuilder --driver docker-container --use
# docker buildx build --load -t hdx-spark-takehome:local .
# Run instructions:
# docker run -it --rm hdx-spark-takehome:local


# Use the official SBT image as the base
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# Set environment variables
ENV SPARK_VERSION=3.5.2
ENV HADOOP_VERSION=3
ENV SPARK_HOME=/opt/spark
ENV PATH=$SPARK_HOME/bin:$PATH

# Switch to root user to install additional dependencies and set up directories
USER root

# Install additional dependencies
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Download and install Spark
RUN curl -LO https://downloads.apache.org/spark/spark-$SPARK_VERSION/spark-$SPARK_VERSION-bin-hadoop$HADOOP_VERSION.tgz && \
    tar -xzf spark-$SPARK_VERSION-bin-hadoop$HADOOP_VERSION.tgz -C /opt && \
    mv /opt/spark-$SPARK_VERSION-bin-hadoop$HADOOP_VERSION /opt/spark && \
    rm spark-$SPARK_VERSION-bin-hadoop$HADOOP_VERSION.tgz

# Set up the working directory and ensure proper permissions
RUN mkdir -p /home/sbtuser/app && \
    chown -R sbtuser:sbtuser /home/sbtuser/app

# Set the working directory
WORKDIR /home/sbtuser/app

# Copy project files and set correct ownership
COPY --chown=sbtuser:sbtuser build.sbt .
COPY --chown=sbtuser:sbtuser project project
COPY --chown=sbtuser:sbtuser src src

# Switch back to sbtuser
USER sbtuser

# Create necessary directories with correct permissions
RUN mkdir -p /home/sbtuser/app/target

# Cache SBT dependencies
RUN sbt update

# Package the project with SBT
RUN sbt clean package

# Switch to root to copy the JAR file
USER root

# Copy the JAR file to Spark's jars directory
RUN cp /home/sbtuser/app/target/scala-*/hdxtakehomeproject_*.jar $SPARK_HOME/jars/

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Switch back to sbtuser for running the application
USER sbtuser

ENTRYPOINT ["/entrypoint.sh"]
CMD ["bash"]