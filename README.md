# Introduction

This project was 'forked' from a CVS repository over on SourceForge at:
<https://sourceforge.net/projects/java-ntlm-proxy/> where it had a license of 'Public Domain'.

I use this a fair bit at work and it has a couple of bugs and other areas for improvements. So first
step was to get to where I can easily access the code and work on.

In the migration, I only brought across the main directory which had the source - there was some
others, but mainly intermediate files. Since then I've also removed a couple of other directories.

Also found after migrating and attempting a build that the main class for the Swing UI was missing.
To get things building I've simply included the decompiled class from the SourceForge download. All
appears to still work.

# Building

To provide a single executable JAR, the gradle [Shadow](https://github.com/johnrengelman/shadow)
plugin is employed. So to build a 'fat' JAR simply:

    ./gradlew shadowJar

Then you'll find your JAR at `build/libs/NTLMProxy.jar`.

How to provide a nicely packaged up tool, then go with:

    ./gradlew distZip

You'll then find a new zip located at `build/distributions/NTLMProxy.zip`. This zip can be extracted
into a folder and then run from there.

(Note, previously the tool was packaged with [One-JAR](http://one-jar.sourceforge.net/), however I
felt Shadow had been kept more up to date and it worked. So...)

# Usage

The quick version:

1. Get a copy of the zip package - e.g. `./gradle distZip`
1. Extract the package, and edit `ntlm-proxy.properties`
1. Run the tool: `java -jar NTLMProxy.jar`

Need more, see the [full version](src/main/dist/Usage.md).

## Logging

If you would like to enable more detailed logging there are two steps:

1. Create yourself a custom `log4j.xml` file; and
2. Launch the JAR with an additional parameter (`-Dlog4j.configuration`).

Example `log4j.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/" debug="false">
    <appender name="Console" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d %-5p [%t] %C{2} {%F:%L} - %m%n" />
        </layout>
    </appender>
    <appender name="File" class="org.apache.log4j.RollingFileAppender">
        <param name="maxFileSize" value="10MB" />
        <param name="maxBackupIndex" value="9" />
        <param name="file" value="ntlm-proxy.log" />
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d %-5p [%t] %C{2} {%F:%L} - %m%n" />
        </layout>
    </appender>
    <category name="ntlmproxy" additivity="false">
        <priority value="debug" />
        <appender-ref ref="File" />
    </category>
    <category name="org.apache.commons.httpclient" additivity="false">
        <priority value="info" />
        <appender-ref ref="File" />
    </category>
    <category name="httpclient.wire" additivity="false">
        <priority value="info" />
        <appender-ref ref="File" />
    </category>

    <root>
        <priority value="info" />
        <appender-ref ref="File" />
    </root>
</log4j:configuration>
```

Then launch the JAR instructing it to use the above config:

    java -Dlog4j.configuration=file:///<path>/log4j.xml -jar NTLMProxy.jar
    
(Replacing `<path>` with an actual path.)

# ToDo

1. Create issues for all the below.
1. Complete usage documentation in `src/main/dist/Usage.md`.
1. Sort out license file - on SF it was simply Public Domain, but would be good to add a LICENSE
   file for that. And do I simply place the copyright notice pointing back to original author  - not
   that I have their name...
1. Add a check of supplied user password by triggering a simple request - maybe to a site configured
   in the `ntlm-proxy.properties` file.
