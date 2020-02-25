package atdd.path.web;

import atdd.favorite.web.FavoritePathController;
import atdd.favorite.web.FavoriteStationController;
import atdd.path.application.GraphService;
import atdd.path.application.dto.PathResource;
import atdd.path.application.dto.PathResponseView;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static atdd.Constant.FAVORITE_PATH_BASE_URI;
import static atdd.Constant.PATH_BASE_URI;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(PATH_BASE_URI)
public class PathController {
    private GraphService graphService;

    public PathController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping
    public ResponseEntity findPath(@RequestParam Long startId, @RequestParam Long endId) {
        PathResponseView response
                = new PathResponseView(startId, endId, graphService.findPath(startId, endId));
        PathResource pathResource=new PathResource(response);
        pathResource.add(linkTo(PathController.class)
                .slash("?startId=" + startId + "&endId=" + endId)
                .withSelfRel());
        pathResource.add(linkTo(FavoritePathController.class)
                .withRel("favorite-paths"));
        pathResource.add(new Link("/docs/api-guide.html#resource-find-path")
                .withRel("profile"));
        return ResponseEntity.ok(pathResource);
    }
}
