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

**NOTE: The featuers outlined here will soon be released into `com.google.common.flogger.FluentLogger`. When this happens `GoogleLogger` and the `google-extensions` dependency will not be required in order to use Metadata.**

#### 1. Add the [`google-extensions`] Flogger dependency

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

#### 2. Java System Property

```java
flogger.logging_context=your.pack.path.YourLoggingContext#getInstance
```

## Settings Java System Properties

As you might have noticed a Flogger and the Flogger Fluentd Backend are highly configurable through Java System Properties.  Here are aome simple ways to set those:

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