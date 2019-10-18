# Jsoap

[![Build Status][ci-img]][ci]
[![Coverage Status][coveralls-img]][coveralls]

The last [SOAP][sp] library you'll ever use.

## Installation
[make (software)][ms] or include this dependency as a remote artifact into the build of your project: 
- [gradle](#gradle)
- [maven](#maven)
- [sbt](#sbt)
- [leiningen](#leiningen)

### gradle
Include in your root build.gradle:
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
```groovy
dependencies {
    implementation 'com.github.connorvanelswyk:Jsoap:LATEST'
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
    <version>1.0.2</version>
</dependency>
```

### sbt
Include in your build.sbt:
```play
resolvers += "jitpack" at "https://jitpack.io"
```
```play
libraryDependencies += "com.github.connorvanelswyk" % "Jsoap" % "LATEST"
```

### leiningen
Include in your project.clj:
```
:repositories [["jitpack" "https://jitpack.io"]]
```
```
:dependencies [[com.github.connorvanelswyk/Jsoap "LATEST"]]
```

## Usage

```java
public class RequestTests {
    
    @Test
    public void simpleTest() {
        
        Request request = new Request()
                        .encoding("ISO-8859-1")
                        .wsdl("https://graphical.weather.gov:443/xml/SOAP_server/ndfdXMLserver.php")
                        .body("https://graphical.weather.gov/xml/docs/SOAP_Requests/LatLonListZipCode.xml");
                
        String expected = "{\"\":\"39.0138,-77.0242 39.2851,-77.8575\"}";
        
        String actual = Jsoap.getInstance().send(request);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void complexTest() {
        
        String requestedTime =
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
                        LocalDateTime.now()
                                .plus(Duration.ofDays(5))
                                .truncatedTo(ChronoUnit.SECONDS));
        
        Request request = new Request()
                .encoding("ISO-8859-1")
                .wsdl("https://graphical.weather.gov:443/xml/SOAP_server/ndfdXMLserver.php")
                .body("https://graphical.weather.gov/xml/docs/SOAP_Requests/GmlLatLonList.xml")
                .params("requestedTime", requestedTime)
                .schema("gml:boundedBy", "gml:coordinates")
                .schema("gml:featureMember", "gml:coordinates", "app:validTime", "app:maximumTemperature");
        
        String expected = "{\"gml:featureMember\":[{\"app:validTime\":\"2019-07-14T18:46:44\",\"gml:coordinates\":\"-77.02,38.99\",\"app:maximumTemperature\":\"91.0\"},{\"app:validTime\":\"2019-07-14T18:46:44\",\"gml:coordinates\":\"-122.30,47.6\",\"app:maximumTemperature\":\"73.0\"},{\"app:validTime\":\"2019-07-14T18:46:44\",\"gml:coordinates\":\"-104.80,39.70\",\"app:maximumTemperature\":\"92.0\"}],\"gml:boundedBy\":{\"gml:coordinates\":\"-122.30,38.99 -77.02,47.6\"}}";

        String actual = Jsoap.getInstance().send(request);
        
        assertEquals(expected, actual);
    }
    
}

```

## Examples

See [Jsoap-api][js].

----

[js]: https://github.com/nelsw/Jsoap-api
[sp]: https://en.wikipedia.org/wiki/SOAP
[ms]: https://en.wikipedia.org/wiki/Make_(software)
[ci-img]: https://travis-ci.com/nelsw/Jsoap.svg?branch=master
[ci]: https://travis-ci.com/nelsw/Jsoap
[coveralls-img]: https://coveralls.io/repos/github/nelsw/Jsoap/badge.svg?branch=master
[coveralls]: https://coveralls.io/github/nelsw/Jsoap
