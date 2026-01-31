package pard.server.com.chapter6.domain;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

/*
왜 필요해?

Spring Security 내부 로직은 다음 정보가 필요해:
아이디(username)
비밀번호(password, 해시된 값)
권한(roles/authorities)
계정 상태(잠김/만료/비활성 등)
이걸 모든 프로젝트에서 공통으로 쓰게 하기 위해 UserDetails라는 “사용자 정보 규격”을 만든 거야.*/

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails { //spring security가 제공하는 인터페이스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Builder
    public User(String email, String password, String auth) {
        this.email = email;
        this.password = password;
    }

    @Override //권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override // 사용자의 id를 반환
    public String getUsername() {
        return email;
    }

    @Override //사용자의 패스워드 반환
    public String getPassword() {
        return password;
    }
    @Override //계정 만료여부 반환
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override //계정 잠금여부 반환
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override //패스워드의 만료여부 반환
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override //계정 사용가능여부 반환
    public boolean isEnabled() {
        return true;
    }
}
