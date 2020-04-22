package moon.odyssey.rest;

import org.assertj.core.api.Assertions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import moon.odyssey.code.TFCode;
import moon.odyssey.model.UserInfo;

@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class UserControllerTest {

    @Autowired
    private WebTestClient testClient;

    @Test
    public void _0_init() {
        Assertions.assertThat(testClient).isNotNull();
    }

    @Test
    public void _1_addUser_should_return_201() {

        UserInfo param = UserInfo.builder()
                                 .userId("testUser2")
                                 .password("testPassword2")
                                 .isVerified(TFCode.TRUE)
                                 .build();

        testClient
            .post()
            .uri("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(param)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(String.class)
            .consumeWith(result -> log.info("##### {}", result));
    }

    @Test
    public void _2_addUser_InvalidParam_should_return_400() {

        Map<String, String> param = new HashMap<>();
        param.put("userId", "testUser2");
        param.put("password", "testPassword2");
        param.put("isVerified", "NULL");

        testClient
            .post()
            .uri("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(param)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class)
            .consumeWith(result -> log.info("##### {}", result));
    }

    @Test
    public void _3_getUsers_should_return_UserInfoList() {

        testClient
            .get()
            .uri("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(UserInfo.class)
            .consumeWith(result -> {
                log.info("##### {}", result);
                result.getResponseBody().stream()
                      .forEach(userInfo -> Assertions.assertThat(userInfo).isInstanceOf(UserInfo.class));
            });
    }

    @Test
    public void _4_getUser_should_return_UserInfo() {

        testClient
            .get()
            .uri("/api/user/{userId}"
                , "testUser"
            )
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserInfo.class)
            .consumeWith(result -> {
                log.info("##### {}", result);
                Assertions.assertThat(result.getResponseBody()).isInstanceOf(UserInfo.class);
            });

    }



}