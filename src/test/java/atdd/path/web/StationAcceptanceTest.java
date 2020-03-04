package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.path.application.dto.StationResponseView;
import atdd.path.application.dto.TimeTableResponseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static atdd.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends AbstractAcceptanceTest {
    public static final String STATION_URL = "/stations";
    public static final String TIMETABLES_URL = "/timetables";
    private LineHttpTest lineHttpTest;
    private StationHttpTest stationHttpTest;

    @BeforeEach
    void setUp() {
        this.lineHttpTest = new LineHttpTest(webTestClient);
        this.stationHttpTest = new StationHttpTest(webTestClient);
    }

    @DisplayName("지하철역 등록")
    @Test
    public void createStation() {
        // when
        Long stationId = stationHttpTest.createStation(STATION_NAME);

        // then
        EntityExchangeResult<StationResponseView> response = stationHttpTest.retrieveStation(stationId);
        assertThat(response.getResponseBody().getName()).isEqualTo(STATION_NAME);
    }

    @DisplayName("지하철역 정보 조회")
    @Test
    public void retrieveStation() {
        // given
        Long stationId = stationHttpTest.createStation(STATION_NAME);

        // when
        EntityExchangeResult<StationResponseView> response = stationHttpTest.retrieveStation(stationId);

        // then
        assertThat(response.getResponseBody().getId()).isNotNull();
        assertThat(response.getResponseBody().getName()).isEqualTo(STATION_NAME);
    }

    @DisplayName("구간이 연결된 지하철역 정보 조회")
    @Test
    public void retrieveStationWithLine() {
        // given
        Long stationId = stationHttpTest.createStation(STATION_NAME);
        Long stationId2 = stationHttpTest.createStation(STATION_NAME_2);
        Long stationId3 = stationHttpTest.createStation(STATION_NAME_3);
        Long lineId = lineHttpTest.createLine(LINE_NAME);
        Long lineId2 = lineHttpTest.createLine(LINE_NAME_2);
        lineHttpTest.createEdgeRequest(lineId, stationId, stationId2);
        lineHttpTest.createEdgeRequest(lineId2, stationId, stationId3);

        // when
        EntityExchangeResult<StationResponseView> response = stationHttpTest.retrieveStation(stationId);

        // then
        assertThat(response.getResponseBody().getId()).isEqualTo(stationId);
        assertThat(response.getResponseBody().getName()).isEqualTo(STATION_NAME);
        assertThat(response.getResponseBody().getLines().size()).isEqualTo(2);
    }


    @DisplayName("지하철역 목록 조회")
    @Test
    public void showStations() {
        // given
        stationHttpTest.createStationRequest(STATION_NAME);
        stationHttpTest.createStationRequest(STATION_NAME_2);
        stationHttpTest.createStationRequest(STATION_NAME_3);

        // when
        EntityExchangeResult<List<StationResponseView>> response = stationHttpTest.showStationsRequest();

        // then
        assertThat(response.getResponseBody().size()).isEqualTo(3);
    }

    @DisplayName("지하철역 삭제")
    @Test
    public void deleteStation() {
        // given
        Long stationId = stationHttpTest.createStation(STATION_NAME);
        EntityExchangeResult<StationResponseView> response = stationHttpTest.retrieveStation(stationId);

        // when
        webTestClient.delete().uri(STATION_URL + "/" + stationId)
                .exchange()
                .expectStatus().isNoContent();

        // then
        webTestClient.get().uri(STATION_URL + "/" + stationId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @DisplayName("지하철역 시간표 조회")
    @Test
    public void retrieveTimeTables(){
        //given
        Long stationId = stationHttpTest.createStation(STATION_NAME);
        Long stationId2 = stationHttpTest.createStation(STATION_NAME_2);
        Long stationId3 = stationHttpTest.createStation(STATION_NAME_3);
        Long stationId4 = stationHttpTest.createStation(STATION_NAME_6);
        Long lineId2 = lineHttpTest.createLine(LINE_NAME_2);
        Long lineId = lineHttpTest.createLine(LINE_NAME);
        lineHttpTest.createEdgeRequest(lineId2, stationId4, stationId);
        int theNumberOfLinesForStation = 2;
        lineHttpTest.createEdgeRequest(lineId, stationId, stationId2);
        lineHttpTest.createEdgeRequest(lineId, stationId2, stationId3);

        //when
        String inputJson = "{\"name\":\"" + STATION_NAME + "\"}";
        List<TimeTableResponseView> timeTables
                = webTestClient.post().uri(STATION_URL + "/" + stationId + "/" + TIMETABLES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .returnResult(TimeTableResponseView.class)
                .getResponseBody()
                .toStream()
                .collect(Collectors.toList());

        //then
        assertThat(timeTables.size()).isEqualTo(theNumberOfLinesForStation);
    }
}
