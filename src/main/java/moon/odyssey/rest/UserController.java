package moon.odyssey.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moon.odyssey.entity.User;
import moon.odyssey.model.UserInfo;
import moon.odyssey.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping(path = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> addUser(@RequestBody @Valid UserInfo userInfo) {

        return
            Mono.defer(() -> repository.findById(userInfo.getUserId()).map(Mono::just).orElseGet(Mono::empty))
                .subscribeOn(Schedulers.elastic())
                .flatMap(__ -> Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "The userId already exists : " + userInfo.getUserId())))
                .switchIfEmpty(Mono.defer(() -> Mono.just(repository.save(new User(userInfo.getUserId(), userInfo.getPassword(), userInfo.getIsVerified())))).subscribeOn(Schedulers.elastic()))
                .cast(User.class)
                .then()
            ;
    }

    @GetMapping
    public Flux<UserInfo> getUsers() {

        return
            Flux.defer(() -> Flux.fromIterable(repository.findAll())).subscribeOn(Schedulers.elastic())
                .map(user -> UserInfo.builder()
                                     .userId(user.getUserId())
                                     .isVerified(user.getIsVerified())
                                     .build()
                )
            ;
    }

    @GetMapping("/{userId}")
    public Mono<UserInfo> getUser(@PathVariable String userId) {

        return
            Mono.defer(() -> repository.findById(userId).map(Mono::just).orElseGet(Mono::empty)).subscribeOn(Schedulers.elastic())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found user")))
                .map(user -> UserInfo.builder()
                                     .userId(user.getUserId())
                                     .isVerified(user.getIsVerified())
                                     .build()
                )
            ;
    }
}
