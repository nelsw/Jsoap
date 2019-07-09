# Jsoap

[![Build Status][ci-img]][ci]
[![Coverage Status][coveralls-img]][coveralls]

The last [SOAP][sp] library you'll ever use.

## Installation
To [make (software)][ms], include this dependency in the build of your project.

### maven
Include the following two (2) elements in pom.xml:
```xml
<!-- remote artifact host for github.com repos -->
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

## Usage


## Examples

### CLI

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

### Java

----

[sp]: https://en.wikipedia.org/wiki/SOAP
[ms]: https://en.wikipedia.org/wiki/Make_(software)
[ci-img]: https://travis-ci.com/connorvanelswyk/soapless.svg?branch=master
[ci]: https://travis-ci.com/connorvanelswyk/soapless
[coveralls-img]: https://coveralls.io/repos/github/connorvanelswyk/soapless/badge.svg?branch=master
[coveralls]: https://coveralls.io/github/connorvanelswyk/soapless