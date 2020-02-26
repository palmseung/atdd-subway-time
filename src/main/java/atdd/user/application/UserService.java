package atdd.user.application;

import atdd.user.application.dto.CreateUserRequestView;
import atdd.user.application.dto.UserResponseView;
import atdd.user.domain.User;
import atdd.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = false)
    public UserResponseView createUser(CreateUserRequestView request) {
        User createdUser = userRepository.save(request.toEntity());
        return UserResponseView.of(createdUser);
    }

    public Long deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
        return user.getId();
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    public void deleteAll(){
        userRepository.deleteAll();
    }
}
