# Apache Log4j Boot

Log4j Boot is a collection of bootstrapping modules to get up and running with
various Log4j components. These modules pull in all the necessary dependencies
to use the various optional features in Log4j.

Log4j Boot is just getting started. See [Log4j Boot epic on JIRA][boot-jira].

## Planned Modules

All modules in this list are assumed to be prefixed with `log4j-boot-`. For the
dependency lists, the format is `groupId:artifactId` or just `artifactId` for
dependencies in `org.apache.logging.log4j`. All modules listed after logback are
implicitly dependent on log4j-boot-core. All modules depend on log4j-api.

* core
  - log4j-api
  - log4j-core
* async
  - log4j-boot-core
  - com.lmax:disruptor
* log4j-spi
  - log4j-boot-core
  - log4j-slf4j-impl
  - org.slf4j:slf4j-api
  - log4j-jcl
  - log4j-jul
  - log4j-1.2-api
* slf4j-spi
  - log4j-boot-core
  - log4j-slf4j-impl
  - org.slf4j:slf4j-api
  - org.slf4j:jcl-over-slf4j
  - org.slf4j:jul-to-slf4j
* logback
  - log4j-to-slf4j
  - org.slf4j:slf4j-api
  - ch.qos.logback:logback-core
  - (other logback jars?)
* advertiser-jmdns
  - javax.jmdns:jmdns
* appender-async-conversant
  - com.conversantmedia:disruptor
* appender-async-jctools
  - org.jctools:jctools-core
* appender-cassandra
  - com.datastax.cassandra:cassandra-driver-core
* appender-commons-compress
  - org.apache.commons:commons-compress
* appender-couchdb
  - org.lightcouch:lightcouch
* appender-jms
  - jms-api?
  - (is this needed? works via a provided scope API normally); alternative: appender-activemq, etc.
* appender-jpa
  - org.eclipse.persistence:javax.persistence
  - (also hibernate/etc. versions?)
* appender-kafka
  - org.apache.kafka:kafka-clients
* appender-mongodb
  - org.mongodb:mongo-java-driver
* appender-smtp
  - com.sun.mail:javax.mail
* appender-zeromq
  - org.zeromq:jeromq
* config-json
  - com.fasterxml.jackson.core:jackson-databind
* config-yaml
  - com.fasterxml.jackson.dataformat:jackson-dataformat-yaml
* layout-csv
  - org.apache.commons:commons-csv
* layout-jansi
  - org.fusesource.jansi:jansi
* layout-json
  - com.fasterxml.jackson.core:jackson-databind
* layout-xml
  - com.fasterxml.jackson.dataformat:jackson-dataformat-xml
* layout-yaml
  - com.fasterxml.jackson.dataformat:jackson-dataformat-yaml
* script-groovy
  - org.codehaus.groovy:groovy

[boot-jira]: https://issues.apache.org/jira/browse/LOG4J2-1775
