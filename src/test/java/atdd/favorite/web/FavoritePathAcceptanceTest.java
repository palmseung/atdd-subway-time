package atdd.favorite.web;

import atdd.path.AbstractAcceptanceTest;
import atdd.path.web.LineHttpTest;
import atdd.path.web.StationHttpTest;
import atdd.user.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static atdd.Constant.AUTH_SCHEME_BEARER;
import static atdd.Constant.FAVORITE_PATH_BASE_URI;
import static atdd.path.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FavoritePathAcceptanceTest extends AbstractAcceptanceTest {
    public static final String EMAIL = "boorwonie@email.com";
    private FavoritePathHttpTest favoritePathHttpTest;
    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;
    private Long stationId;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;
    private Long lineId;
    private String token;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        setUpForPathTest(EMAIL);
    }

    @Test
    public void 지하철경로_즐겨찾기_등록하기() {
        //when
        String token = jwtTokenProvider.createToken(EMAIL);
        Long pathId = favoritePathHttpTest.createFavoritePath(EMAIL, stationId, stationId4, token);

        //then
        assertThat(1L).isEqualTo(pathId);
    }

    @Test
    public void 지하철경로_즐겨찾기_삭제하기() throws Exception {
        //when
        Long pathId = favoritePathHttpTest.createFavoritePath(EMAIL, stationId, stationId4, token);

        //then
        mockMvc.perform(
                delete(FAVORITE_PATH_BASE_URI + "/" + pathId)
                        .header("Authorization", AUTH_SCHEME_BEARER + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 지하철경로_즐겨찾기_목록보기() throws Exception {
        int theNumberOfStations = 2;
        favoritePathHttpTest.createFavoritePath(EMAIL, stationId, stationId4, token);
        favoritePathHttpTest.createFavoritePath(EMAIL, stationId3, stationId4, token);

        //when, then
        mockMvc.perform(
                get(FAVORITE_PATH_BASE_URI)
                        .header("Authorization", AUTH_SCHEME_BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.favoritePaths.*", hasSize(theNumberOfStations)))
                .andDo(print());
    }

    public void setUpForPathTest(String email) {
        token = jwtTokenProvider.createToken(email);
        this.favoritePathHttpTest = new FavoritePathHttpTest(webTestClient, jwtTokenProvider);
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
        this.stationId = stationHttpTest.createStation(STATION_NAME);
        this.stationId2 = stationHttpTest.createStation(STATION_NAME_2);
        this.stationId3 = stationHttpTest.createStation(STATION_NAME_3);
        this.stationId4 = stationHttpTest.createStation(STATION_NAME_4);
        this.lineId = lineHttpTest.createLine(LINE_NAME);
        lineHttpTest.createEdgeRequest(lineId, stationId, stationId2);
        lineHttpTest.createEdgeRequest(lineId, stationId2, stationId3);
        lineHttpTest.createEdgeRequest(lineId, stationId3, stationId4);
    }
}