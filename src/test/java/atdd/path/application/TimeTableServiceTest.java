package atdd.path.application;

import atdd.path.dao.EdgeDao;
import atdd.path.dao.LineDao;
import atdd.path.dao.StationDao;
import atdd.path.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static atdd.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = TimeTableService.class)
public class TimeTableServiceTest {
    private TimeTableService timeTableService;

    @MockBean
    LineDao lineDao;

    @MockBean
    StationDao stationDao;

    @MockBean
    EdgeDao edgeDao;

    @BeforeEach
    void setUp() {
        this.timeTableService = new TimeTableService(lineDao, stationDao, edgeDao);
    }

//    @Test
//    void 열차목록과_역정보를_주면_지하철역의_상행선_첫차시간을_알려준다() {
//        //given
//        int indexOfStation = 1;
//        int intervalOfLine = TEST_LINE.getInterval();
//        List<Station> stations
//                = Arrays.asList(TEST_STATION_4, TEST_STATION, TEST_STATION_2, TEST_STATION_3));
//
//        //when
//        LocalTime firstTimeForUp = timeTableService.findFirstTimeForUp(stations, TEST_STATION);
//
//        //then
//        assertThat(firstTimeForUp)
//                .isEqualTo(TEST_LINE.getStartTime().plusMinutes(indexOfStation*intervalOfLine));
//
//    }

    @Test
    void 노선의_지하철역_목록을_주면_지금_지하철역이_몇_번쨰인지_반환한다(){
        //given
        List<Station> stations
                = Arrays.asList(TEST_STATION_4, TEST_STATION, TEST_STATION_2, TEST_STATION_3);

        //when
        int index = timeTableService.calculateIndex(stations, TEST_STATION);

        //then
        assertThat(index).isEqualTo(1);
    }
}
