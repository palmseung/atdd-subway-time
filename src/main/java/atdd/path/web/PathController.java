package atdd.path.web;

import atdd.path.application.GraphService;
import atdd.path.application.dto.MinTimePathResponseView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static atdd.Constant.PATH_BASE_URI;

@RestController
@RequestMapping(PATH_BASE_URI)
public class PathController {
    private GraphService graphService;

    public PathController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping
    public ResponseEntity findPath(@RequestParam Long startId, @RequestParam Long endId) {
        MinTimePathResponseView responseView = graphService.findMinTimePath(startId, endId);
        responseView.changeLines();
        return ResponseEntity
                .ok(responseView);
    }
}
