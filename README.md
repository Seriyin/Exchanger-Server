# Exchanger-Server #

Java financial exchange server on pub-sub basis.

- [Exchanger-Server](#exchanger-server)
    - [Purpose](#purpose)
    - [Overview](#overview)
        - [Order processing](#order-processing)
        - [Live-Monitoring](#live-monitoring)
    - [Communication](#communication)
        - [Exchanges <-> Front-end](#exchanges---front-end)
        - [Exchanges <-> Client](#exchanges---client)
        - [Exchanges <-> Directory](#exchanges---directory)
    - [Dependencies](#dependencies)

------------------------------

## Purpose ##

Is in charge of taking and processing trade orders and informing subscribers of trading operations in real-time.

------------------------------

## Overview ##

### Order processing ###

1. There will be multiple exchanges running.

1. Each company can be traded in a single exchange.

1. A trade order may involve multiple independent trades until the order is fulfilled.

1. Each trade is made according to the average of buy and sell price for matching buy/sell order pairs to the maximum fulfillable quantity.

1. A buy order must specify the company, quantity and maximum accepted price per stock.

1. A sell order must specify the company, quantity and minimum accepted price per stock.

1. The client should be immediately informed of a fulfilled order with the total order raw monetary value traded.

------------------------------

### Live-Monitoring ###

To keep monitoring information available for prospective clients, the [REST directory service](https://github.com/Seriyin/Exchanger-Directory) must be alerted on specific events via POST calls.

Exchanges must inform the directory on:

- Minimum and maximum live price negotiated on current day for its companies.
- Opening price on exchange for each company traded for the day.
- Closing price on exchange for each company traded for the day.

------------------------------

## Communication ##

_Serialization of all trade and consultation information will be handled through the use of [Protocol Buffers](https://github.com/google/protobuf)._

### Exchanges <-> Front-end ###

Communication must be kept between exchanges and front-end server through use of tcp [ZeroMQ sockets in native Java](https://github.com/zeromq/jeromq).

Exchanges must:

- Wait on trade orders to process.
- Inform front-end of completed orders.

------------------------------

### Exchanges <-> Client ###

Exchanges will publish directly to clients upon discovery via front-end server using publishing ZeroMQ sockets which clients directly subscribe too.

Exchanges must:

- Publish all trades in real-time for subscribed companies.
- Cancel an active subscription for a client.

------------------------------

### Exchanges <-> Directory ###

Exchanges will only POST information to the directory service.

Exchanges must:

- Publish their address when they come up online into the directory.
- Publish real-time min/max priced trade orders.

_**Note:** REST API requests and replies will use JSON format serialization for better Web compatibility._

------------------------------

## Dependencies ##

- [Java native implementation of ZeroMQ](https://github.com/zeromq/jeromq).
- [Java native implementation of Protocol Buffers](https://github.com/google/protobuf).
- [Jackson, JSON for Java](https://github.com/FasterXML/jackson).
- [Google http-client](https://developers.google.com/api-client-library/java/google-http-java-client/) for generating HTTP requests.

Dependencies are resolved using [Maven](https://maven.apache.org/).
