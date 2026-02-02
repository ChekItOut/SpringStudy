package pard.server.com.chapter6.controller;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pard.server.com.chapter6.domain.Article;
import pard.server.com.chapter6.dto.AddArticleRequest;
import pard.server.com.chapter6.dto.ArticleResponse;
import pard.server.com.chapter6.dto.UpdateArticleRequest;
import pard.server.com.chapter6.service.BlogService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogApiController {
    private final BlogService blogService;

    @PostMapping("/api/articles")
    //ResponseEntity가 “HTTP 응답 전체(상태코드+헤더+바디)”를 담는 래퍼라는 거, 그리고 <Article>은 바디에 담길 타입이라는 거.
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)//HTTP 상태코드를 201 Created로 세팅
                .body(savedArticle); //응답 바디에 savedArticle을 담음, 실제로 클라이언트가 JSON 형태로 받게됨
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()//서비스에서 findAll로 찾은 리스트를 하나씩 꺼내서 처리할 수 있는 흐름으로 만든다.
                .map(ArticleResponse::new)//스트림의 각 요소를 다른 타입으로 변환
                .toList();
        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);
        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);
        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id,
                                                 @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.update(id, request);
        return ResponseEntity.ok()
                .body(updatedArticle);
    }
}
