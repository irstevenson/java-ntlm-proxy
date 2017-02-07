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

# ToDo

1. Complete usage documentation in `src/main/dist/Usage.md`.
1. Sort out license file - on SF it was simply Public Domain, but would be good to add a LICENSE
   file for that. And do I simply place the copyright notice pointing back to original author  - not
   that I have their name...
1. Look into bug where it appears to get a thread locked in an infinite loop - i.e. CPU usage sky
   rockets after a bit of use.
1. Add a check of supplied user password by triggering a simple request - maybe to a site configured
   in the `ntlm-proxy.properties` file.
