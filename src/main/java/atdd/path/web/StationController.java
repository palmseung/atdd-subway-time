package atdd.path.web;

import atdd.path.application.GraphService;
import atdd.path.application.domain.TimeTables;
import atdd.path.application.dto.CreateStationRequestView;
import atdd.path.application.dto.StationResponseView;
import atdd.path.application.dto.TimeTableResponseView;
import atdd.path.dao.EdgeDao;
import atdd.path.dao.LineDao;
import atdd.path.dao.StationDao;
import atdd.path.domain.Edge;
import atdd.path.domain.Edges;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static atdd.Constant.STATION_BASE_URI;

@RestController
@RequestMapping(STATION_BASE_URI)
public class StationController {
    private StationDao stationDao;
    private LineDao lineDao;
    private EdgeDao edgeDao;
    private GraphService graphService;

    public StationController(StationDao stationDao, LineDao lineDao,
                             EdgeDao edgeDao, GraphService graphService) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.edgeDao = edgeDao;
        this.graphService = graphService;
    }

    @PostMapping
    public ResponseEntity createStation(@RequestBody CreateStationRequestView view) {
        Station persistStation = stationDao.save(view.toStation());
        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(StationResponseView.of(persistStation));
    }

    @GetMapping("/{id}")
    public ResponseEntity retrieveStation(@PathVariable Long id) {
        try {
            Station persistStation = stationDao.findById(id);
            return ResponseEntity.ok().body(StationResponseView.of(persistStation));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity showStation() {
        List<Station> persistStations = stationDao.findAll();
        return ResponseEntity.ok().body(StationResponseView.listOf(persistStations));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping ("/{id}/timetables")
    public ResponseEntity retrieveTimetables(@PathVariable Long id){
        Station station = stationDao.findById(id);
        return ResponseEntity.ok().build();
    }
}





