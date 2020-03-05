package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static atdd.TestConstant.*;


public class GraphAcceptanceTest extends AbstractAcceptanceTest {
    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
    }

    @Test
    public void findMinTimePath() {
        Long stationId = stationHttpTest.createStation(STATION_NAME);
        Long stationId2 = stationHttpTest.createStation(STATION_NAME_2);
        Long stationId3 = stationHttpTest.createStation(STATION_NAME_3);
        Long lineId = lineHttpTest.createLine(LINE_NAME);
        lineHttpTest.createEdgeRequest(lineId, stationId, stationId2);
        lineHttpTest.createEdgeRequest(lineId, stationId2, stationId3);

        webTestClient.get().uri("/paths?startId=" + stationId + "&endId=" + stationId3)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.startStationId").isEqualTo(stationId)
                .jsonPath("$.endStationId").isEqualTo(stationId3)
                .jsonPath("$.stations.length()").isEqualTo(3)
                .jsonPath("$.lines.length()").isEqualTo(1)
                .jsonPath("$.distance").exists()
                .jsonPath("$.departAt").exists()
                .jsonPath("$.arriveBy").exists();
    }
}
