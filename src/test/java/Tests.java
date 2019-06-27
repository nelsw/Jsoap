import org.jsoap.JsoapUtils;
import org.jsoap.model.JsoapRequest;
import org.jsoap.model.JsoapResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class Tests {

    JsoapRequest jsoapRequest;

    @Before
    public void before() {

        jsoapRequest = new JsoapRequest()
                .service("https://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php")
                .request("https://graphical.weather.gov/xml/docs/SOAP_Requests/LatLonListZipCode.xml");
    }

    @Test
    public void urlBody() {
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(JsoapUtils.response()).get();
        assertNotNull(jsoapResponse.body());
        assertEquals(200, (int) jsoapResponse.code());
        System.out.println(jsoapResponse.body());
    }

    @Test
    public void urlBodyData() {
        jsoapRequest.data("listZipCodeList", "33401");
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(JsoapUtils.response()).get();
        assertNotNull(jsoapResponse.body());
        assertEquals(200, (int) jsoapResponse.code());
        System.out.println(jsoapResponse.body());
    }

    @Test
    public void urlBodyDataTableList() {
        jsoapRequest.data("listZipCodeList", "33401");
        jsoapRequest.table("nil", "latLonList", "West Palm Beach");
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(JsoapUtils.response()).get();
        assertNotNull(jsoapResponse.body());
        assertEquals(200, (int) jsoapResponse.code());
        System.out.println(jsoapResponse.body());
    }

    @Test
    public void urlBodyDataTableMap() {
        jsoapRequest.request("https://graphical.weather.gov/xml/docs/SOAP_Requests/GmlLatLonList.xml");
        jsoapRequest.data("requestedTime", "2019-06-30T23:59:59");
        jsoapRequest.table("gml:boundedBy", "gml:coordinates", "Coordinates");
        jsoapRequest.table("gml:featureMember", "gml:coordinates", "Coordinates");
        jsoapRequest.table("gml:featureMember", "app:validTime", "Time");
        jsoapRequest.table("gml:featureMember", "app:maximumTemperature", "Max Temp");
        JsoapResponse jsoapResponse = Optional.of(jsoapRequest).map(JsoapUtils.response()).get();
        assertNotNull(jsoapResponse.body());
        assertEquals(200, (int) jsoapResponse.code());
        System.out.println(jsoapResponse.body());
    }


}