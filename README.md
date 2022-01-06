# JMeter AMQP Plugin

![License](https://img.shields.io/github/license/a1dutch/jmeter-amqp?style=for-the-badge)
![Latest release](https://img.shields.io/github/v/release/a1dutch/jmeter-amqp?style=for-the-badge)

![Build (master)](https://img.shields.io/github/workflow/status/a1dutch/jmeter-amqp/cd/master?style=for-the-badge)
![Sonarcloud](https://img.shields.io/sonar/violations/a1dutch_jmeter-amqp/master?format=long&server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)
![Issues](https://img.shields.io/github/issues/a1dutch/jmeter-amqp?style=for-the-badge)


A [JMeter](http://jmeter.apache.org/) plugin to publish & consume messages from any [AMQP](http://www.amqp.org/) message brokers.

## Installing

To install the plugin and its dependencies by copying them into JMeters lib/ directory

* jmeter-amqp-{version}.jar
* amqp-client-{version}.jar

## Building

The project is built using Maven. To execute the build script, just execute:

`mvn clean package`
