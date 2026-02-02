package pard.server.com.chapter6.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import pard.server.com.chapter6.config.jwt.TokenProvider;
import pard.server.com.chapter6.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import pard.server.com.chapter6.config.oauth.OAuth2SuccessHandler;
import pard.server.com.chapter6.config.oauth.OAuth2UserCustomService;
import pard.server.com.chapter6.repository.RefreshTokenRepository;
import pard.server.com.chapter6.service.RefreshTokenService;
import pard.server.com.chapter6.service.UserService;

import static org.springframework.boot.security.autoconfigure.web.servlet.PathRequest.toH2Console;

@Configuration
@RequiredArgsConstructor
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                // ✅ Spring Boot가 기본으로 제공하는 정적 리소스 위치들( /static, /public, /resources, /META-INF/resources )
                PathRequest.toStaticResources().atCommonLocations(),

                // ✅ 네가 직접 쓰는 정적 경로들
                PathPatternRequestMatcher.withDefaults().matcher("/img/**"),
                PathPatternRequestMatcher.withDefaults().matcher("/css/**"),
                PathPatternRequestMatcher.withDefaults().matcher("/js/**")
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // ✅ RequestMatcher를 PathPattern 기반으로 통일 (최신 권장)
        var apiToken = PathPatternRequestMatcher.withDefaults().matcher("/api/token");
        var apiAll = PathPatternRequestMatcher.withDefaults().matcher("/api/**");

        return http //토큰 방식으로 인증하기 때문에 기존 폼 로그인, 세션 기능을 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //직접 만든 헤더를 확인 할 필터를 추가
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                // ✅ authorizeRequests -> authorizeHttpRequests 로 변경
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(apiToken).permitAll() //토큰 재발급 url은 인증 없이 접근 가능하도록 설정
                        .requestMatchers(apiAll).authenticated() //나머지 api url은 인증 필요하도록 설정
                        .anyRequest().permitAll()
                )

                .oauth2Login(oauth2 -> oauth2
                        //OAuth2 로그인 기능을 활성화하고, 그 로그인 과정에서 사용할 세부 옵션들을 설정하는 곳이야
                        .loginPage("/login")
                        .authorizationEndpoint(ae -> ae
                                .authorizationRequestRepository(
                                        oAuth2AuthorizationRequestBasedOnCookieRepository()
                                )//“OAuth2 로그인 과정에서 OAuth2AuthorizationRequest(state 등 포함) 를 세션 대신 쿠키에 저장/복원하기 위한 저장소”
                        )
                        .userInfoEndpoint(uie -> uie //구글 로그인 성공 후
                                .userService(oAuth2UserCustomService)//사용자 정보를 가져오는데, 그 “사용자 정보 가져온 뒤 처리”를 담당하는 서비스
                        )
                        .successHandler(oAuth2SuccessHandler()) //OAuth2 로그인에 성공했을 때 실행되는 로직
                        //로그인을 성공하면 JWT(AccessToken, RefreshToken)을 발급해야하기 때문에 이를 발급하는 로직
                        //JWT(Access/Refresh)를 발급하고, refresh는 쿠키+DB에 저장한 다음 프론트로 리다이렉트까지 해주는 “마무리 담당”
                )

                //"api"로 시작하는 url인 경우 인증 실패 시 401 상태코드를 반환
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                apiAll
                        )
                )
                .build();
    }




    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }


    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }


    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

}
