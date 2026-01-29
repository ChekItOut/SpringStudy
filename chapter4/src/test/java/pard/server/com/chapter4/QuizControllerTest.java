package pard.server.com.chapter4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class QuizControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext content;

    @Autowired//객체와 Json간의 변환을 처리해준다.
    private ObjectMapper objectMapper;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(content).build();
    }

    @DisplayName("quiz(): GET /quiz?code=1 이면 응답 코드는 201, 응답 본문은 create!를 리턴")
    @Test
    public void getQuiz1() throws Exception {
        //given
        final String url = "/quiz";

        //when
        final ResultActions result = mockMvc.perform(get(url).param("code", "1"));

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(content().string("Created!"));
    }

    @DisplayName("quiz(): GET /quiz?code=2 이면 응답 코드는 400, 응답 본문은 Bad Request!를 리턴")
    @Test
    public void getQuiz2() throws Exception {
        //given
        final String url = "/quiz";

        //when
        final ResultActions result = mockMvc.perform(get(url).param("code", "2"));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad Request!"));
    }

    @DisplayName("quiz(): POST /quiz?code=1 이면 응답 코드는 403, 응답 본문은 Forbidden!를 리턴")
    @Test
    public void postQuiz1() throws Exception {
        //given
        final String url = "/quiz";

        //when
        final ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Code(1)))
        ); //해당 url로 post요청을 보낼 때 JSON으로 보낼거라고 명시하고, objectMapper의 writeValuesAsString메서드로
        //객체를 만들어 JSON으로 변환한다.

        //then
        result
                .andExpect(status().isForbidden())
                .andExpect(content().string("Forbidden!"));
    }

    @DisplayName("quiz(): POST /quiz?code=13 이면 응답 코드는 200, 응답 본문은 OK!를 리턴")
    @Test
    public void postQuiz13() throws Exception {
        //given
        final String url = "/quiz";

        //when
        final ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Code(13)))
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpect(content().string("OK!"));
    }

}