package it.io.openliberty.guides.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import jakarta.ws.rs.core.Response;
import io.openliberty.guides.graphql.models.NoteInfo;
import io.openliberty.guides.graphql.models.SystemLoad;
import io.openliberty.guides.graphql.models.SystemLoadData;
import io.openliberty.guides.graphql.models.SystemInfo;

// tag::testcontainers[]
@Testcontainers
// end::testcontainers[]
@TestMethodOrder(OrderAnnotation.class)
public class QueryResourceIT {

    private static Logger logger = LoggerFactory.getLogger(QueryResourceIT.class);
    private static String system8ImageName = "system:1.0-java8-SNAPSHOT";
    private static String queryImageName = "query:1.0-SNAPSHOT";
    private static String graphqlImageName = "graphql:1.0-SNAPSHOT";

    public static QueryResourceClient client;
    public static Network network = Network.newNetwork();

    // tag::systemContainer[]
    // tag::container1[]
    @Container
    // end::container1[]
    public static GenericContainer<?> systemContainer
        = new GenericContainer<>(system8ImageName)
              .withNetwork(network)
              .withExposedPorts(9080)
              .withNetworkAliases("system-java8")
              .withLogConsumer(new Slf4jLogConsumer(logger));
    // end::systemContainer[]

    // tag::graphqlContainer[]
    // tag::container2[]
    @Container
    // end::container2[]
    public static LibertyContainer graphqlContainer
        = new LibertyContainer(graphqlImageName)
              .withNetwork(network)
              .withExposedPorts(9082)
              .withNetworkAliases("graphql")
              .withLogConsumer(new Slf4jLogConsumer(logger));
    // end::graphqlContainer[]

    // tag::libertyContainer[]
    // tag::container[]
    @Container
    // end::container[]
    public static LibertyContainer libertyContainer
        = new LibertyContainer(queryImageName)
              .withNetwork(network)
              .withExposedPorts(9084)
              .withLogConsumer(new Slf4jLogConsumer(logger));
    // end::libertyContainer[]

    @BeforeAll
    public static void setupTestClass() throws Exception {
        System.out.println("TEST: Starting Liberty Container setup");
        client = libertyContainer.createRestClient(QueryResourceClient.class);
    }

    // tag::testGetSystem[]
    @Test
    @Order(1)
    public void testGetSystem() {
        System.out.println("TEST: Testing get system /system/system-java8");
        SystemInfo systemInfo = client.querySystem("system-java8");
        assertEquals(systemInfo.getHostname(), "system-java8");
        assertNotNull(systemInfo.getOsVersion(), "osVersion is null");
        assertNotNull(systemInfo.getJava(), "java is null");
        assertNotNull(systemInfo.getSystemMetrics(), "systemMetrics is null");
    }
    // end::testGetSystem[]

    // tag::testGetSystemLoad[]
    @Test
    @Order(2)
    public void testGetSystemLoad() {
        System.out.println("TEST: Testing get system load /systemLoad/system-java8");
        List<SystemLoad> systemLoad = client.querySystemLoad("system-java8");
        assertEquals(systemLoad.get(0).getHostname(), "system-java8");
        SystemLoadData systemLoadData = systemLoad.get(0).getLoadData();
        assertNotNull(systemLoadData.getLoadAverage(), "loadAverage is null");
        assertNotNull(systemLoadData.getHeapUsed(), "headUsed is null");
        assertNotNull(systemLoadData.getNonHeapUsed(), "nonHeapUsed is null");
    }
    // end::testGetSystemLoad[]

    // tag::testEditNote[]
    @Test
    @Order(3)
    public void testEditNote() {
        System.out.println("TEST: Testing editing note /mutation/system/note");
        NoteInfo note = new NoteInfo();
        note.setHostname("system-java8");
        note.setText("I am trying out GraphQL on Open Liberty!");
        Response response = client.editNote(note);
        assertEquals(200, response.getStatus(), "Incorrect response code");
        SystemInfo systemInfo = client.querySystem("system-java8");
        assertEquals(systemInfo.getNote(), "I am trying out GraphQL on Open Liberty!");
    }
    // end::testEditNote[]
}
