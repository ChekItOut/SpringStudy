package pard.server.com.chapter6.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pard.server.com.chapter6.service.UserDetailService;
import static org.springframework.boot.security.autoconfigure.web.servlet.PathRequest.toH2Console;

/*WebSecurityConfig는 **Spring Security의 “보안 설정 파일”**이야.
스프링이 기본으로 제공하는 보안 동작(모든 요청 차단/기본 로그인 등)을 내 앱 요구사항에 맞게 바꾸기 위해 만들고,
여기서 “어떤 요청을 허용/차단할지”, “로그인을 어떻게 할지”, “로그아웃/CSRF/인증 방식” 같은 걸 설정해.*/
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final UserDetailService userService;

    //스프링시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**"); // ✅ AntPathRequestMatcher 제거
    }

    // 특정 http 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http //이 코드는 **“어떤 URL은 누구나 접근 가능, 그 외는 로그인한 사람만 접근 가능”**이라는 인가(Authorization) 규칙을 정하는 부분이야.
                .authorizeHttpRequests(auth -> auth //들어오는 모든 HTTP 요청에 대해“이 요청을 허용할지 / 막을지” 인가 규칙을 설정
                        .requestMatchers("/login", "/signup", "/user").permitAll() // “이 URL 패턴에 해당하는 요청이면” “로그인 안 해도 누구나 통과”
                        .anyRequest().authenticated() //위에서 지정한 것들 말고 나머지 모든 요청은 authenticated() = 인증된 사용자(로그인 성공한 사용자)만 허용
                )
                .formLogin(formLogin -> formLogin //폼기반 로그인 설정
                        .loginPage("/login") //로그인 페이지 경로 설정
                        .defaultSuccessUrl("/articles") //로그인이 완료되었을 때 이동할 경로 설정
                        .permitAll()
                )
                .logout(logout -> logout //로그아웃 설정
                        .logoutSuccessUrl("/login") //로그아웃이 완료되었을 때 경로를 설정
                        .invalidateHttpSession(true) //로그아웃 이후에 세션을 전체삭제할지 여부를 설정
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable) //csrf 비활성화 csrf 공격방지를 위해 활성화가 좋지만 실습에서는 편하게 비활
                .build();
    }


    @Bean //인증관리자 관련 설정. 로그인 검증은 이렇게 해라하고 설정
    //“로그인할 때 아이디/비번 검증을 누가 어떻게 할지”를 시큐리티에게 알려주는 인증설정
    //AuthenticationManager: 스프링 시큐리티에서 “인증(로그인) 처리를 총괄하는 매니저” 로그인 요청이 들어오면 결국 AuthenticationManager.authenticate(...)가 호출돼서
    //“아이디/비번이 맞는지”를 판단해.
    public AuthenticationManager authenticationManager(BCryptPasswordEncoder bCryptPasswordEncoder) {
        //AuthenticationManager가 직접 DB를 뒤지지 않아. 대신 Provider에게 맡겨.
        //DaoAuthenticationProvider는 “DB/저장소에서 사용자 정보를 가져와서 비번을 비교하는” 표준 Provider야.
        //provider는 userService 내부에 loadUserByUsername를 통해 사용자 정보를 가져온다.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userService); // ✅ 생성자 주입
        authProvider.setPasswordEncoder(bCryptPasswordEncoder); //비번을 암호화하기 위한 인코더를 설정
        //비밀번호는 보통 BCrypt로 해시해서 DB에 저장해놨지?
        //로그인할 때도 입력한 평문 비번을 BCrypt로 처리해서 비교해야 하니까
        //“비번 비교 규칙”으로 BCrypt를 지정하는 거야.
        return new ProviderManager(authProvider);

        /*요약
        “로그인 검증은 이렇게 해라”를 정의함.
        동작: 로그인 시 userService.loadUserByUsername(...)로 사용자 조회
        DB에 저장된 비번(BCrypt 해시)과 입력 비번을 BCrypt로 비교, 맞으면 인증 성공
        * */
    }

    @Bean //패스워드 인코더를 빈으로 등록
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
