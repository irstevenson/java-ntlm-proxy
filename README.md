# Introduction

This project was 'forked' from a CVS repository over on SourceForge at:
<https://sourceforge.net/projects/java-ntlm-proxy/> where it had a license of 'Public Domain'.

I use this a fair bit at work and it has a couple of bugs and other areas for improvements. So first
step was to get to where I can easily access the code and work on.

In the migration, I did only bring across the main directory which has the source - there was some
other directories. Since then, I have also removed a couple of other directories.

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

TODO, but...

1. Get a copy of the zip package - e.g. `./gradle distZip`
1. Extract the package, and edit `ntlm-proxy.properties`
1. Run the tool: `java -jar NTLMProxy.jar`

# ToDo

1. Make sure that the Swing GUI still works - due to the code there having to simply be a
   decompilation;
1. Add Usage documentation - which should also be included in the distributable (so maybe a
   `src/main/dist/README.md` file (and just link to from the Usage section above).
