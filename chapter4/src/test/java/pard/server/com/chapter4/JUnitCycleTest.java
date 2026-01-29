package pard.server.com.chapter4;

import org.junit.jupiter.api.*;

public class JUnitCycleTest {
    @BeforeAll //전체 테스트를 시작하기 전에 처음으로 한번만 실행
    // DB에 연결하거나 테스트환경을 초기 세팅할때 필요
    //전체 테스트 실행주기에서 한번만 호출되어야함으로 static으로 선언
    static void beforeAll() {
        System.out.println("@BeforeAll");
    }

    @BeforeEach
    //각 테스트 케이스를 시작하기 전에 매번 실행한다.
    //예를 들어 테스트 메서드에서 사용하는 객체를 초기화하거나 테스트에 필요한 값을 미리 넣을 때 사용
    public void beforeEach() {
        System.out.println("@BeforeEach");
    }

    @Test
    public void test1() {
        System.out.println("test1");
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Test
    public void test3() {
        System.out.println("test3");
    }

    @AfterAll //전체 테스트를 마치고 종료하기전에 한번만 실행
    //예를 들어 DB연결을 종료하고, 공통적으로 해제해야하는 자원이 있을때
    //전체 테스트 실행주기에서 한번만 호출되어야함으로 static으로 선언
    static void afterAll() {
        System.out.println("@AfterAll");
    }

    @AfterEach //각 테스트 케이스를 종료하기 전에 매번 실행한다.
    //테스트 이후에 특정데이터를 삭제해야하는 경우
    public void afterEach() {
        System.out.println("@AfterEach");
    }
}
