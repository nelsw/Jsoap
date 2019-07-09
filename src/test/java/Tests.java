import lombok.extern.log4j.Log4j2;
import org.jsoap.Jsoap;
import org.jsoap.Request;
import org.junit.After;
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

    private Request request;
    private String response;

    @Before
    public void before() {
        response = null;
        request = new Request()
                .wsdl("https://graphical.weather.gov:443/xml/SOAP_server/ndfdXMLserver.php")
                .body("https://graphical.weather.gov/xml/docs/SOAP_Requests/LatLonListZipCode.xml");
    }

    @After
    public void after() {
        log.debug(response);
        assertNotNull(response);
    }

    @Test
    public void validRequestParams_sendValidRequest_returnValidJson() {
        response = Jsoap.getInstance().send(request);
    }

    @Test
    public void validRequest_sendRequest_returnJson() {
        response = Jsoap.getInstance().send(request.params("listZipCodeList", "33401"));
    }

    @Test
    public void validBodyParamsSchema_sendRequest_returnJson() {
        String requestedTime =
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
                        LocalDateTime.now()
                                .plus(Duration.ofDays(5))
                                .truncatedTo(ChronoUnit.SECONDS));
        response = Jsoap.getInstance().send(request
                .body("https://graphical.weather.gov/xml/docs/SOAP_Requests/GmlLatLonList.xml")
                .params("requestedTime", requestedTime)
                .schema("gml:boundedBy", "gml:coordinates")
                .schema("gml:featureMember", "gml:coordinates", "app:validTime", "app:maximumTemperature"));
    }

    @Test
    public void invalidWsdl_sendRequest_returnResponse() {
        response = Jsoap.getInstance().send(request.encoding("foo"));
    }

    @Test
    public void invalidBody_sendRequest_returnResponse() {
        response = Jsoap.getInstance().send(request.body("https://foo.com"));
    }

    @Test
    public void invalidProxyPort_sendRequest_returnResponse() {
        response = Jsoap.getInstance().send(request
                .proxyType(Proxy.Type.SOCKS.name())
                .proxyHost("foo")
                .proxyPort(-1));
    }

}