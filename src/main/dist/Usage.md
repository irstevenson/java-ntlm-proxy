# Usage

## Requirements

* Java 8 (JRE or JDK)

## Setup

1. Download the most recent distributable zip - e.g. NTLMProxy-<version>.zip
1. 

## Example Launch Scripts

Below are some rough examples of scripts to launch the proxy - i.e. just by clicking on a shortcut.
Really, they just show how you go about using the tool after you've done the setup.

The following **script for Linux** requires:

* You update the `JAVA_HOME` variable to point to the install location of your JRE or (as I have below) JDK.
* You update the `cd` line to specify your install directory.

```
#!/bin/bash
export JAVA_HOME=/data/tools/jdk8
export PATH=$JAVA_HOME/bin:$PATH

cd /data/tools/ntlm-proxy
java -jar NTLMProxy.jar
```

The following **Windows Batch script** requires:

* You update the `JAVA_HOME` variable to point to the install location of your JRE or (as I have below) JDK.
* You update the `c:` and `cd` lines to properly navigate to your install location.

```
@echo off

title Java NTLM Proxy

set JAVA_HOME=C:\Program Files (x86)\Java\jre8
set PATH=%JAVA_HOME%\bin;%PATH%

c:
cd \tools\ntlm-proxy
java -jar NTLMProxy.jar
```