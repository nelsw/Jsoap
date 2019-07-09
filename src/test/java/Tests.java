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
    public void urlBodyData() {
        String json = Jsoap.getInstance().send(request
                .params("listZipCodeList", "33401")
                .schema("latLonList"));
        log.debug(json);
        assertNotNull(json);
    }

    @Test
    public void urlBodyDataschemaMapJson() {
        String json = Jsoap.getInstance().send(request
                .body("https://graphical.weather.gov/xml/docs/SOAP_Requests/GmlLatLonList.xml")
                .params("requestedTime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime))
                .schema("gml:boundedBy", "gml:coordinates")
                .schema("gml:featureMember", "gml:coordinates", "app:validTime", "app:maximumTemperature"));
        log.debug(json);
        assertNotNull(json);
    }

    @Test
    public void invalid1() {
        String json = Jsoap.getInstance().send(request);
        log.debug(json);
        assertNotNull(json);
    }

    @Test
    public void invalid2() {
        request.proxyPort(1);
        request.proxyType(Proxy.Type.SOCKS.name());
        String json = Jsoap.getInstance().send(request);
        log.debug(json);
        assertNotNull(json);
    }

    @Test
    public void invalid3() {
        request.body("");
        String json = Jsoap.getInstance().send(request);
        log.debug(json);
        assertNotNull(json);
    }

}