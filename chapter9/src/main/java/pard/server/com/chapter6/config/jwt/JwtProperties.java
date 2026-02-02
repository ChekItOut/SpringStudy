package pard.server.com.chapter6.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt")
//application.yaml의 jwt 아래 값을 객체 필드에 “바인딩(binding)” 해주는 기능이야.
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
