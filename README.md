# Introduction

This project was 'forked' from a CVS repository over on SourceForge at:
<https://sourceforge.net/projects/java-ntlm-proxy/> where it had a license of 'Public Domain'.

I use this a fair bit at work and it has a couple of bugs and other areas for improvements. So first
step was to get to where I can easily access the code and work on.

In the migration, I did only bring across the main directory which has the source - there was some
other directories. Since then, I have also removed a couple of other directories.

# Building

To provide a single executable JAR, the gradle [Shadow]() plugin is employed. So to build a 'fat'
JAR simply:

    ./gradlew shadowJar

Then you'll find your JAR at `build/libs/NTLMProxy.jar`.

(Note, previously the tool was packaged with [One-JAR](http://one-jar.sourceforge.net/), however I
felt Shadow had been kept more up to date and it worked. So...)

# Usage

TODO
