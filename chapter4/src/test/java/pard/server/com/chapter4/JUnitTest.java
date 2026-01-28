package pard.server.com.chapter4;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JUnitTest {
    @DisplayName("1+2는 3이다.") //테스트 이름을 명시
    @Test //테스트를 수행하는 메서드가 된다.
    public void junitTest() {
        int a = 1;
        int b = 2;
        int sum = 3;

        //JUnit에서 제공하는 검증메서드
        Assertions.assertEquals(sum, a+b);// 값이 같은지 확인
    }

    @DisplayName("두번째 테스트")
    @Test //테스트를 수행하는 메서드가 된다.
    public void junitTes2() {
        int a = 1;
        int b = 2;
        int sum = 3;

        //JUnit에서 제공하는 검증메서드
        Assertions.assertEquals(sum, a+b);// 값이 같은지 확인
    }
}
