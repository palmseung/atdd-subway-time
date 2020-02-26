package atdd.user.web;

import atdd.user.application.UserService;
import atdd.user.application.dto.LoginRequestView;
import atdd.user.application.dto.LoginResource;
import atdd.user.application.dto.LoginResponseView;
import atdd.user.domain.User;
import atdd.user.jwt.JwtTokenProvider;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static atdd.Constant.AUTH_SCHEME_BEARER;
import static atdd.Constant.LOGIN_BASE_URI;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(LOGIN_BASE_URI)
public class LoginController {
    private UserService userService;
    private JwtTokenProvider jwtTokenProvider;

    public LoginController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    public ResponseEntity login(@RequestBody LoginRequestView request) {
        User user = userService.findByEmail(request.getEmail());
        boolean isMatch = user.getPassword().equals(request.getPassword());
        if (!isMatch) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
        String token = jwtTokenProvider.createToken(request.getEmail());
        LoginResponseView response = new LoginResponseView(token, AUTH_SCHEME_BEARER);
        LoginResource resource = new LoginResource(response);
        resource.add(linkTo(LoginController.class).slash("oauth/token").withSelfRel());
        resource.add(linkTo(UserController.class).slash(user.getId()).withRel("users-delete"));
        resource.add(linkTo(UserController.class).slash("me").withRel("users-me"));
        resource.add(new Link("/docs/api-guide.html#resources-login").withRel("profile"));
        return ResponseEntity
                .created(URI.create("/oauth/token"))
                .body(resource);
    }
}
