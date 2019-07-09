# Jsoap

[![Build Status][ci-img]][ci]
[![Coverage Status][coveralls-img]][coveralls]

The last [SOAP][sp] library you'll ever use.

## Installation
To [make (software)][ms], include this dependency into the build of your project: 
- [gradle](#gradle)
- [maven](#maven)
- [sbt](#sbt)
- [leiningen](#leiningen)

### gradle
Include in your root build.gradle
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

### maven
Include in your pom.xml:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.connorvanelswyk</groupId>
    <artifactId>jsoap</artifactId>
    <version>LATEST</version>
</dependency>
```

### sbt
Include in your build.sbt:
```play
resolvers += "jitpack" at "https://jitpack.io"
```
```
libraryDependencies += "com.github.User" % "Repo" % "Tag"
```

### leiningen
Include in your project.clj
```
:repositories [["jitpack" "https://jitpack.io"]]
```
```
:dependencies [[com.github.User/Repo "Tag"]]
```

## Usage


## Examples

A simple request without parameters or result schema  
```cmd
curl \
-d '{
  "wsdl": "https://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php",
  "body": "https://graphical.weather.gov/xml/docs/SOAP_Requests/LatLonListZipCode.xml",
}' \
-H 'Content-Type: application/json' \
http://ifvanelswyk.com/jsoap
```

A complex request with parameters and a nested result schema
```cmd
curl \
-d '{
  "wsdl": "https://graphical.weather.gov:443/xml/SOAP_server/ndfdXMLserver.php",
  "body": "https://graphical.weather.gov/xml/docs/SOAP_Requests/GmlLatLonList.xml",  
  "params": {
    "requestedTime": "2019-07-22T23:59:59"
  },
  "schema": {
    "gml:boundedBy": {
      "gml:coordinates": ""
    },
    "gml:featureMember": {
      "gml:coordinates": "",
      "app:validTime": "",
      "app:maximumTemperature": ""
    }
  }
}' \
-H 'Content-Type: application/json' \
http://ifvanelswyk.com/jsoap
```

----

[sp]: https://en.wikipedia.org/wiki/SOAP
[ms]: https://en.wikipedia.org/wiki/Make_(software)
[ci-img]: https://travis-ci.com/connorvanelswyk/soapless.svg?branch=master
[ci]: https://travis-ci.com/connorvanelswyk/soapless
[coveralls-img]: https://coveralls.io/repos/github/connorvanelswyk/soapless/badge.svg?branch=master
[coveralls]: https://coveralls.io/github/connorvanelswyk/soapless