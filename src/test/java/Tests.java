import lombok.extern.log4j.Log4j2;
import org.jsoap.Jsoap;
import org.jsoap.model.JsoapRequest;
import org.jsoap.model.JsoapResponse;
import org.junit.Before;
import org.junit.Test;

import java.net.Proxy;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Log4j2
public class Tests {

    JsoapRequest jsoapRequest;

    @Before
    public void before() {
        System.out.println();
        jsoapRequest = new JsoapRequest()
                .service("https://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php")
                .request("https://graphical.weather.gov/xml/docs/SOAP_Requests/LatLonListZipCode.xml");
    }

    @Test
    public void urlBody() {
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(Jsoap.responseObject()).get();
        log.debug(jsoapResponse);
        assertNotNull(jsoapResponse.getBody());
        assertEquals(200, (int) jsoapResponse.getCode());
    }

    @Test
    public void urlBodyData() {
        jsoapRequest.data("listZipCodeList", "33401");
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(Jsoap.responseObject()).get();
        log.debug(jsoapResponse);
        assertNotNull(jsoapResponse.getBody());
        assertEquals(200, (int) jsoapResponse.getCode());
    }

    @Test
    public void urlBodyDataTableList() {
        jsoapRequest.data("listZipCodeList", "33401");
        jsoapRequest.table("nil", "latLonList", "West Palm Beach");
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(Jsoap.responseObject()).get();
        log.debug(jsoapResponse);
        assertNotNull(jsoapResponse.getBody());
        assertEquals(200, (int) jsoapResponse.getCode());
    }

    @Test
    public void urlBodyDataTableMap() {
        jsoapRequest.request("https://graphical.weather.gov/xml/docs/SOAP_Requests/GmlLatLonList.xml");
        jsoapRequest.data("requestedTime", "2019-06-30T23:59:59");
        jsoapRequest.table("gml:boundedBy", "gml:coordinates", "Coordinates");
        jsoapRequest.table("gml:featureMember", "gml:coordinates", "Coordinates");
        jsoapRequest.table("gml:featureMember", "app:validTime", "Time");
        jsoapRequest.table("gml:featureMember", "app:maximumTemperature", "Max Temp");
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(Jsoap.responseObject()).get();
        log.debug(jsoapResponse);
        assertNotNull(jsoapResponse.getBody());
        assertEquals(200, (int) jsoapResponse.getCode());
    }

    @Test
    public void invalid1() {
        jsoapRequest.proxy(null);
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(Jsoap.responseObject()).get();
        log.debug(jsoapResponse);
        assertNotNull(jsoapResponse.getBody());
        assertEquals(200, (int) jsoapResponse.getCode());
    }

    @Test
    public void invalid2() {
        jsoapRequest.proxyHost(null);
        jsoapRequest.proxyPort(1);
        jsoapRequest.proxyType(Proxy.Type.SOCKS.name());
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(Jsoap.responseObject()).get();
        log.debug(jsoapResponse);
        assertNotNull(jsoapResponse.getBody());
        assertEquals(200, (int) jsoapResponse.getCode());
    }

    @Test
    public void invalid3() {
        jsoapRequest.request("");
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(Jsoap.responseObject()).get();
        log.debug(jsoapResponse);
        assertNotNull(jsoapResponse.getBody());
        assertEquals(400, (int) jsoapResponse.getCode());
    }

}