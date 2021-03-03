FROM confluentinc/cp-kafka-connect:6.1.0

# Install https://github.com/confluentinc/kafka-connect-datagen
# to generate test data for the JMESPath connector.
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:0.4.0

# Install https://github.com/confluentinc/kafka-connect-jdbc
# as a sink to inspect the JMESPath-filtered data.
# RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.0.0

# Install Kafka Connect MongoDB Connector.
# Used for streaming datagen topics to a MongoDB collections
# for manually inspecting JMESPath filtering results.
RUN confluent-hub install --no-prompt mongodb/kafka-connect-mongodb:1.4.0

# Download the PostgreSQL JDBC driver for the JDBC connector.
# WORKDIR /etc/kafka-connect/jars
# RUN curl -O https://jdbc.postgresql.org/download/postgresql-42.2.19.jar

# Install the latest version of JMESPath connector from Confluent Hub.
RUN confluent-hub install --no-prompt denisw/kafka-connect-jmespath:latest