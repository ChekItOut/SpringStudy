package pard.server.com.chapter6.config.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pard.server.com.chapter6.domain.User;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
//토큰을 생성하고 올바른 토큰인지 유효성 검사를 하고, 토큰에서 필요한 정보를 가져오는 클래스
public class TokenProvider {

    private final JwtProperties jwtProperties;

    //JWT 토큰 생성 매서드
    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //헤더 typ: JWT
                .setIssuer(jwtProperties.getIssuer()) //yaml에 설정한 issuer값
                .setIssuedAt(now) //iat: 현재 시간
                .setExpiration(expiry) // expiry: 멤버 변수값
                .setSubject(user.getEmail()) //sub: 유저의 이메일
                .claim("id", user.getId()) //클레임 id: 유저 id
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    //JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser() //검증기 (parser)생성
                    .setSigningKey(jwtProperties.getSecretKey()) //jwt에는 signiture가 있는데
                    //signiture를 검증하기위해 signiture를 만들때 사용된 키가 필요하다. secretKey로 토큰의 시그니처가 위조되지 않았느닞 검사할 준비
                    .parseClaimsJws(token); //실제 검증 동작. 만료도 여기서 검증한다.

            return true; //예외가 안터지면 유효한 토큰
        } catch (Exception e) {
            return false; //예외가 터지면 유효하지 않은 토큰
        }
    }

    //토큰을 받아 인증벙보를 담은 객체 authentication을 반환하는 메서드
    //JWT 필터가 받아서 SecurityContext에 꽂아 넣는 데 쓰여.
    //그 다음부터는 스프링 시큐리티가 “이 요청은 인증된 사용자 요청”으로 취급해.
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject
                (), "", authorities), token, authorities);
    }
    
    
    //토큰 기반으로유저 id를 가져오는 메서드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser() //클레임 조회
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}