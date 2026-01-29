package pard.server.com.chapter4;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuizController {
    @GetMapping("/quiz")
    public ResponseEntity<String> quiz(@RequestParam("code") int code) {
        switch (code){
            case 1://응답코드가 201일때
                return ResponseEntity.created(null).body("Created!");
            case 2://응답 코드가 400일때
                return ResponseEntity.badRequest().body("Bad Request!");
            default: //응답코드가 200일때
                return ResponseEntity.ok().body("OK");
        }
    }

    @PostMapping("/quiz")
    public ResponseEntity<String> quiz2(@RequestBody Code code) {
        switch (code.value()) {
            case 1:
                return ResponseEntity.status(403).body("Forbidden!");
            default:
                return ResponseEntity.ok().body("OK!");
        }
    }
}
record Code(int value) {
    //자바에서 **“데이터만 담는 불변(immutable) 객체”**를 아주 간단하게 만드는 문법이야.
    //(클래스 대신 record를 쓰면 DTO처럼 쓰기 좋음)
    //getter를 만들어주는데 getValue가 아니라 그냥 value로 호출
    //equals / hashCode / toString도 만들어준다.

}


