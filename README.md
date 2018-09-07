# Demo for Creating a custom JSON layout for Log4J2 (driven by SLF4j)

## Features
 - MDC kv pairs are added as first-level JSON fields without the need for additional logging configuration
 - Exceptions and stack traces are logged with the JSON event, allowing log parsers to associate the stack trace with the event.
 - Ability to define custom name-value pairs to add to logging
 - Ability to use different names for standard logging fields

## Configuration

see [log4j2.properties](src/main/resources/log4j2.properties).

```
appender.myCustom.type = Console
appender.myCustom.name = myCustom
appender.myCustom.layout.type = ToddCustomJsonLayout
appender.myCustom.layout.environment = local dev
appender.myCustom.layout.pretty = false
```

- Use whatever appender name.  I used "myCustom"
- The recommended type for 12-factor applications is Console
- layout type is the glue that associates the appender with the java class file
- the environment should probably be picked up through a placeholder, but it can be configured differently for different environments.
- always use "pretty=false" when logging for an aggregator (e.g. Splunk, DataDog, Logstash, etc.)

## Demo

- Run the application [LoggingDemo](src/main/java/todd/customLogging/demo/LoggingDemo.java).
- Verify two JSON log entries.  One contains a stack trace.  View in a JSON formatter or log aggregator, or set pretty=true.

## Sample

The demo program uses a sample custom JSON layout [ToddCustomJsonLayout](src/main/java/todd/customLogging/sample/ToddCustomJsonLayout.java).  You will probably want to make your own customized as you see fit.

### Annotation Wiring
There are two annotations that are required to configure this class as a logging layout:

```
@Plugin(name = "ToddCustomJsonLayout", category = "Core", elementType = "layout", printObject = true)
public class ToddCustomJsonLayout extends AbstractCustomJsonLayout {
```

...the class definition annotation allows you to define the appender in your log4j2 properties file `appender.myCustom.layout.type = ToddCustomJsonLayout`

```
    @PluginFactory
    public static ToddCustomJsonLayout createLayout(
            @PluginAttribute(value = "environment", defaultString = "unknown") String environment,
            @PluginAttribute(value = "pretty", defaultBoolean = false) boolean pretty
    ) {
        return new ToddCustomJsonLayout(environment, pretty);
    }
```

...the factory method annotation allows Log4J2 to construct the layout with the specified parameters from your log4j2 properties file

```
appender.myCustom.layout.environment = local dev
appender.myCustom.layout.pretty = false
```

### Defining logger behavior:

There are three abstract methods from `AbstractCustomJsonLayout` that you must override:

- `protected Map<String, Object> mapLogEvent(LogEvent event)`
- `protected Map<String, Object> logStackTrace(Throwable throwable)`
- `protected Map<String, Object> logException(Throwable throwable)`

#### mapLogEvent
Allows you to map fields from LogEvent to JSON.  You can also add additional fields that are not in MDC.
MDC fields are automagically added to the logging with no configuration required.

#### logStackTrace
Allows you to log a stack trace.  The `ConciseStackTraceLogger` is probably fine for your needs, but you are free to plug in a different approach.

#### logException
Allows you to log the exception.  The `DefaultExceptionLogger` recurses through exception causes and adds log entries for each cause.  Again, you are free to plug in a different approach.

## TODO
 - add log event timestamp
 - think about one method for logging exceptions, rather than two.
