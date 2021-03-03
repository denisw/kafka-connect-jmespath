# Kafka Connect JMESPath Demo

A [Docker Compose][docker-compose] configuration that lets you test the
Kafka Connect JMESPath plugin. It creates the following Docker containers:

* Kafka
* Kafka Connect
* MongoDB (as example sink connector target)
* [Mongo Express][mongo-express] (MongoDB web UI)

The Kafka Connect container comes with the following plugins:

* JMESPath
* [Kafka Connect Datagen][connect-datagen] to create example data
* [MongoDB Kafka Connector][mongo-connector] as a use case for record 
  filtering with JMESPath

## Prerequisites

* Python
* [HTTPie][httpie]
* Docker & Docker Compose

## Usage

The demo setup is controlled with the script in the `bin` folder. To
get started, run the `up` script, which runs `docker-compose up -d` to
create and start all containers. The script also configures all 
connectors defined in the `connectors` folder.

```shell
./bin/up
```

You can check the connectors' status with the `status` script:

```shell
./bin/status
```

The demo connectors include Datagen connectors that randomly populate
topics, as well as MongoDB sink connectors that consume these topics,
filter them using the JMESPath plugin, and write them to MongoDB. 

You can inspect the results by visiting the Mongo Express UI:

[http://localhost:8081/](http://localhost:8081/)

To stop and delete the containers again, run the `down` script.

```shell
./bin/down
```

[connect-datagen]: https://github.com/confluentinc/kafka-connect-datagen
[docker-compose]: https://docs.docker.com/compose/
[httpie]: https://httpie.io
[mongo-connector]: https://www.mongodb.com/kafka-connector
[mongo-express]: https://github.com/mongo-express/mongo-express