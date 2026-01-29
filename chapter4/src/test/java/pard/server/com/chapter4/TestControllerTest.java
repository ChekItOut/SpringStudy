package pard.server.com.chapter4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest //테스트용 애플리케이션 컨택스트 생성
//@SpringBootApplication이 있는 클래스를 찾아 그 클래스에 포함된 빈을 찾은다음 테스트용 애플리케이션 컨텍스트를 만든다.

@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성
// MockMvc는 애플리케이션을 서버에 배포하지 않고 테스트용 MVC 환경을 만들어 요청 및 전송, 응답 기능을 제공하는 유틸리티 클래스.
//즉, 컨트롤러를 테스트할 때 사용되는 클래스이다.

class TestControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext content;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach //테스트 실행 전 실행하는 메서드
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(content).build();
    }

    @AfterEach // 테스트 실행 후 실행하는 메서드
    public void cleanup() {
        memberRepository.deleteAll();
        //다른 테스트를 위해 기존 테스트에서 만든 테이블을 모두 삭제
    }

    @DisplayName("getAllMembers: 아티클 조회에 성공한다.")
    @Test
    public void getAllMembers() throws Exception {//mockMvc.perform(...) 같은 호출에서 예외가 날 수 있으니 간단히 던지도록 처리.
        //given: 테스트 준비를 위한 데이터 세팅
        final String url = "/test"; //호출할 경로를 저장
        Member savedMember = memberRepository.save(new Member(1L, "홍길동")); //DB에 임의로 1명을 저장

        //when: MockMvc가 가짜 http GET 요청을 보냄
        //.accept(MediaType.APPLICATION_JSON): JSON으로 응답을 받고 싶다라고 서버에 알림
        //실행 결과를 ResultActions으로 받는다.
        final ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));

        //then
        result   //andExpect: Assertion하는 메서드
                .andExpect(status().isOk()) //http 200인지 확인

                //http요청으로 받은 json과 savedMember가 같은지 확인하는 과정
                //$ -> json / [0] -> 배열 : JSON의 0번째 배열값의 id와 name을 조회
                .andExpect(jsonPath("$[0].id").value(savedMember.getId()))
                .andExpect(jsonPath("$[0].name").value(savedMember.getName()));
    }
}