# JMeter AMQP Plugin

![GitHub](https://img.shields.io/github/license/a1dutch/jmeter-amqp?label=License&style=for-the-badge)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/a1dutch/jmeter-amqp?label=Latest%20Release&style=for-the-badge)

![GitHub Issues](https://img.shields.io/github/issues/a1dutch/jmeter-amqp?label=Issues&style=for-the-badge)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/a1dutch/jmeter-amqp/master?label=Build%20Status&style=for-the-badge)
![Sonar Violations (long format)](https://img.shields.io/sonar/violations/a1dutch_jmeter-amqp/master?format=long&label=Sonar%20Violations&server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)


A [JMeter](http://jmeter.apache.org/) plugin to publish & consume messages from any [AMQP](http://www.amqp.org/) message brokers.

## Installing

To install the plugin and its dependencies by copying them into JMeters lib/ directory

* jmeter-amqp-{version}.jar
* amqp-client-{version}.jar

## Building

The project is built using Maven. To execute the build script, just execute:

`mvn clean package`
