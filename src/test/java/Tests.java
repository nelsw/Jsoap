import lombok.extern.log4j.Log4j2;
import org.jsoap.Jsoap;
import org.jsoap.Request;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.Proxy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertNotNull;

@Log4j2
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Tests {

    private LocalDateTime localDateTime = LocalDateTime.now().plus(Duration.ofDays(5)).truncatedTo(ChronoUnit.SECONDS);
    private Request request;

    @Before
    public void before() {
        System.out.println();
        request = new Request()
                .wsdl("https://graphical.weather.gov:443/xml/SOAP_server/ndfdXMLserver.php")
                .body("https://graphical.weather.gov/xml/docs/SOAP_Requests/LatLonListZipCode.xml");
    }

    @Test
    public void validRequestParams_sendValidRequest_returnValidJson() {
        String json = Jsoap.getInstance().send(request);
        log.debug(json);
        assertNotNull(json);
    }

    @Test
    public void validRequest_sendRequest_returnJson() {
        String json = Jsoap.getInstance().send(request
                .params("listZipCodeList", "33401"));
        log.debug(json);
        assertNotNull(json);
    }

    @Test
    public void validBodyParamsSchema_sendRequest_returnJson() {
        String json = Jsoap.getInstance().send(request
                .body("https://graphical.weather.gov/xml/docs/SOAP_Requests/GmlLatLonList.xml")
                .params("requestedTime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime))
                .schema("gml:boundedBy", "gml:coordinates")
                .schema("gml:featureMember", "gml:coordinates", "app:validTime", "app:maximumTemperature"));
        log.debug(json);
        assertNotNull(json);
    }

    @Test
    public void invalidWsdl_sendRequest_returnResponse() {
        String json = Jsoap.getInstance().send(request
                .encoding("invalid encoding"));
        log.debug(json);
        assertNotNull(json);
    }

    @Test
    public void invalidBody_sendRequest_returnResponse() {
        String json = Jsoap.getInstance().send(request
                .body("https://graphical.weather.gov/xml/docs/SOAP_Requests/GmlLatLonList"));
        log.debug(json);
        assertNotNull(json);
    }

    @Test
    public void invalidProxyPort_sendRequest_returnResponse() {
        String json = Jsoap.getInstance().send(request
                .proxyPort(-1)
                .proxyType(Proxy.Type.SOCKS.name()));
        log.debug(json);
        assertNotNull(json);
    }

    @Test
    public void invalid3() {
        String json = Jsoap.getInstance().send(request
                .body(""));
        log.debug(json);
        assertNotNull(json);
    }

}