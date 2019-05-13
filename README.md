# Flogger Fluentd Backend

## Overview

Flogger Fluentd Backend is a backend for [Flogger](https://github.com/google/flogger) utilizing the [Fluentd Logger for Java](https://github.com/fluent/fluent-logger-java) that sends logs to [Fluentd](https://github.com/fluent/fluentd).

## Requirements

* Java >= 1.8
* flogger >= 0.2
* flogger-system-backend >= 0.2

## Install

In the following examples replace `${flogger.version}` or `floggerVersion` and `${flogger-fluentd-backend.version}` or `floggerFluentdBackendVersion` with the current version of Flogger and Flogger Fluentd Backend respectively.

**Manual:**

Add the following jar files to your `classpath`:

```bash
wget http://central.maven.org/maven2/com/google/flogger/flogger/${flogger.version}/flogger-${flogger.version}.jar
wget http://central.maven.org/maven2/com/google/flogger/flogger-system-backend/${flogger.version}/flogger-system-backend-${flogger.version}.jar
wget http://central.maven.org/maven2/io/github/agsimeonov/flogger-fluentd-backend/${flogger-fluentd-backend.version}/flogger-fluentd-backend-${flogger-fluentd-backend.version}.jar
```

**Maven:**

Include the following dependencies in your `pom.xml`:

```xml
<dependencies>
  ...
  <dependency>
    <groupId>com.google.flogger</groupId>
    <artifactId>flogger</artifactId>
    <version>${flogger.version}</version>
  </dependency>
  <dependency>
    <groupId>com.google.flogger</groupId>
    <artifactId>flogger-system-backend</artifactId>
    <version>${flogger.version}</version>
  </dependency>
  <dependency>
    <groupId>io.github.agsimeonov</groupId>
    <artifactId>flogger-fluentd-backend</artifactId>
    <version>${flogger-fluentd-backend.version}</version>
  </dependency>
  ...
</dependencies>
```

**Gradle:**

Include the following dependencies in your `build.gradle`:

```gradle
dependencies {
  compile 'com.google.flogger:flogger:'+floggerVersion
  compile 'com.google.flogger:flogger-system-backend:'+floggerVersion
  compile 'io.github.agsimeonov:flogger-fluentd-backend:'+floggerFluentdBackendVersion
}
```

## Fluentd Flogger Backend Features

### Configuring Fluentd Flogger Backend

In order for Fluentd Flogger Backend to be used we need to do the following:

#### Configure Backend Factory via Java System Property

```java
flogger.backend_factory=com.agsimeonov.flogger.backend.fluentd.FluentdBackendFactory#getInstance
```

### Configure Logger Name [`tag_prefix`] via [`FluentdCallerFinder`]

By default the Fluentd Logger Name otherwise known as `tag_prefix` will be set to the calling class name. You can change this by extending and configuring your own `FluentdCallerFinder` or using an existing one that ships with Fluentd Flogger Backend.

#### Configure your own [`FluentdCallerFinder`]

You might want to do something custom.  If so you will have to write some code.

##### 1. Extend [`com.agsimeonov.flogger.backend.fluentd.FluentdCallerFinder`]

Implement your desired logic and provide a singleton getter:

```java
public static com.google.common.flogger.backend.Platform.LogCallerFinder getInstance()
```

##### 2. Configure Caller Finder via Java System Property

```java
flogger.caller_finder=your.pack.path.YourCallerFinder#getInstance
```

#### Utilize [`SystemPropertiesCallerFinder`]

You may wish to set the Logger Name using a Java System Property.

##### 1. Configure [`SystemPropertiesCallerFinder`] via Java System Property

```java
flogger.caller_finder=com.agsimeonov.flogger.backend.fluentd.SystemPropertiesCallerFinder#getInstance
```

##### 1. Configure Logger Name via Java System Property

```java
flogger.tag_prefix=<tag_prefix>
```

#### Utilize [`ImplementationTitleCallerFinder`]

If the Java Implementation Title is set you can utilize the [`ImplementationTitleCallerFinder`].

##### Example add implementation title via Maven

```xml
<plugins>
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
      <archive>
        <manifest>
          <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
        </manifest>
      </archive>
    </configuration>
  </plugin>
</plugins>
```

##### Configure [`ImplementationTitleCallerFinder`] via Java System Property

```java
flogger.caller_finder=com.agsimeonov.flogger.backend.fluentd.ImplementationTitleCallerFinder#getInstance
```

### Configure Remote Fluentd Logger

By default Flogger Fluentd Backend assumed the fluent daemon is launched locally.Sometimes you will need to connect to Fluentd on a remote host. In order to achieve this you can either configure your own mechanism by extending and configuring `FluentdRemoteSettings` or you can use System Properties and utilize `SystemPropertiesRemoteSettings`.

#### Configure Remote Settings via Java System Properties

Set the Java System Properties necessary for a remote Fluentd connection as follows:

```java
flogger.remote_settings=com.agsimeonov.flogger.backend.fluentd.SystemPropertiesFluentdRemoteLoggerSettings#getInstance
flogger.fluentd_host=<fluentd_host>
flogger.fluentd_port=<fluentd_port>
```

#### Extend [`com.agsimeonov.flogger.backend.fluentd.FluentdRemoteSettings`]

Implement your desired logic and provide a singleton getter:

```java
public static com.agsimeonov.flogger.backend.fluentd.FluentdRemoteSettings getInstance()
```

##### Configure [`FluentdRemoteSettings`] via Java System Property

```java
flogger.remote_settings=your.pack.path.YourRemoteSettings#getInstance
```

### Logging Levels

By default Logging Levels are not a concept reconginzed by Fluentd. They can however easily be added as log elements and the `tag_suffix` of the Logger Name which is what we do in the Fluentd Flogger Backend. Flogger provides features to handle disabled logs and force logging. We provide support for those features via `FluentdLevelDisabler`. This is a class that can be extended and configured to handle disabling of Levels. We provide an implementation of this class that handles this via System Properties.

#### Configure Level Disabling/Enabling via Java System Properties

Java System Properties available for Level Disabling:

```java
flogger.level_disabler=com.agsimeonov.flogger.backend.fluentd.SystemPropertiesLevelDisabler#getInstance
flogger.exclusive=<true/false>
flogger.<name>=<true/false>
flogger.level=<integer>
```

Here is how they will reslove:

1. If `flogger.exclusive` is not set logging is enabled for all Levels.
2. If `flogger.exclusive` is set to true and `logger.<name>` is set to true logging is disabled for Level `<name>`
3. If `flogger.exclusive` is set to true and `flogger.level` is set logging is disabled if the Level integer value is greater than `flogger.level`.
4. Otherwise if `flogger.exclusive` is set to true logging is enabled for all Levels.
5. If `flogger.exclusive` is set to false and `logger.<name>` is set to true logging is enabled for Level `<name>`
6. If `flogger.exclusive` is set to false and `flogger.level` is set logging is enabled if the Level integer value is greater than or equal to `flogger.level`.
7. Otherwise if `flogger.exclusive` is is to false logging is disabled for all Levels.

#### Extend [`com.agsimeonov.flogger.backend.fluentd.FluentdLevelDisabler`]

Implement your desired logic and provide a singleton getter:

```java
public static com.agsimeonov.flogger.backend.fluentd.FluentdLevelDisabler getInstance()
```

##### Configure [`FluentdLevelDisabler`] via Java System Property

```java
flogger.level_disabler=your.pack.path.YourLevelDisabler#getInstance
```

## How to use Flogger

### 1. Add an import for [`FluentLogger`]

```java
import com.google.common.flogger.FluentLogger;
```

### 2. Create a `private static final` instance

```java
private static final FluentLogger logger = FluentLogger.forEnclosingClass();
```

### 3. Start logging

```java
logger.atInfo().withCause(exception).log("Log message with: %s", argument);
```

## Flogger Advanced Features

### Metadata and [`GoogleLogger`]

Metadata represents any additional key/value information that should be attached to a log statement. Metadata keys can be used to provide log statements with strongly typed values which can be read and interpreted by logging backends or other logs related tools. This mechanism is intended for values with specific semantics and should not be seen as a replacement for logging arguments as part of a formatted log message.

**NOTE: 1, 2, 3 will not be necessary in Flogger versions > 0.4 as the necessary APIs for 4, 5 will be released into `com.google.common.flogger.FluentLogger`.**

#### 1. Add the [`google-extensions`] Flogger dependency

**Manual:**

Add the following jar file to your `classpath`:

```bash
wget http://central.maven.org/maven2/com/google/flogger/google-extensions/${flogger.version}/google-extensions-${flogger.version}.jar
```

**Maven:**

Include the following dependency in your `pom.xml`:

```xml
<dependencies>
  ...
  <dependency>
    <groupId>com.google.flogger</groupId>
    <artifactId>google-extensions</artifactId>
    <version>${flogger.version}</version>
  </dependency>
  ...
</dependencies>
```

**Gradle:**

Include the following dependency in your `build.gradle`:

```gradle
dependencies {
  compile 'com.google.flogger:google-extensions:'+floggerVersion
}
```

#### 2. Add an import for [`GoogleLogger`]

```java
import com.google.common.flogger.GoogleLogger;
```

#### 3. Create a `private static final` instance

```java
private static final GoogleLogger logger = FluentLogger.forEnclosingClass();
```

#### 4. Log single Metadata

Creates a key for a single piece of metadata. If metadata is set more than once using thiskey for the same log statement, the last set value will be the one used, and other values will be ignored (although callers should never rely on this behavior).

```java
logger.atInfo()
      .with(MetadataKey .single("text", String.class), "Hello, World!")
      .log();
```

#### 5. Log repeated Metadata

Creates a key for a repeated piece of metadata. If metadata is added more than once using this key for a log statement, all values will be retained as key/value pairs in the order they were added.

```java
logger.atInfo()
      .with(MetadataKey.repeated("numeric", Integer.class), 1)
      .with(MetadataKey.repeated("numeric", Integer.class), 2)
      .with(MetadataKey.repeated("numeric", Integer.class), 2)
      .log();
```

### Tags

Tags are a special type of immutable Metadata. A tag is either a `simple` tag or a tag with a value. When thinking of tags as a `Map<String, Set<Object>>`, the value of a `simple` tag is the empty set.

#### 1. Add and import for [`Tags`]

```java
import com.google.common.flogger.backend.Tags;
```

#### 2. Create some `simple` tags

```java
Tags.builder()
    .addTag("a")
    .addTag("b")
    .addTag("c")
    .build();
```

#### 3. Create tags with values

```java
Tags.builder()
    .addTag("long", 1L)
    .addTag("boolean", true)
    .addTag("double", 1.0)
    .addTag("string", "text")
    .build();
```

### Modify logging behavior via [`LoggingContext`]

`LoggingContext` is an API for injecting scoped metadata for log statements (either globally or on a per-request basis). The API can also be used to force logging regardless of the normal log level configuration of the logger and ignoring any rate limiting or other filtering.

#### 1. Extend [`com.google.common.flogger.backend.system.LoggingContext`]

Implement your desired logic and provide a singleton getter:

```java
public static LoggingContext getInstance()
```

#### 2. Configure Logging Context via Java System Property

```java
flogger.logging_context=your.pack.path.YourLoggingContext#getInstance
```

### Specify a custom [`Clock`]

A clock populates walltime timestamps for log statements.

#### 1. Extend [`com.google.common.flogger.backend.system.Clock`]

Implement your desired logic and provide a singleton getter:

```java
public static LoggingContext getInstance()
```

#### 2. Configure Clock via Java System Property

```java
flogger.clock=your.pack.path.YourClock#getInstance
```

## Settings Java System Properties

As you might have noticed Flogger and the Flogger Fluentd Backend are highly configurable through Java System Properties.  Here are aome simple ways to set them:

### 1. Programatically

```java
System.setProperty("key", "value");
```

### 2. Environment Variable

```bash
export JAVA_TOOL_OPTIONS='-Dkey1=value1 -Dkey2=value2'
```

### 3. Command Line

```bash
java -Dkey="value" application_launcher_class
```

## Documentation

Documentation can be found at:

* <https://agsimeonov.github.io/flogger-fluentd-backend>
* <https://google.github.io/flogger>

## License

Apache License, Version 2.0
