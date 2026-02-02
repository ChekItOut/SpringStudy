package pard.server.com.chapter6.dto;
import lombok.*;
import pard.server.com.chapter6.domain.Article;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateArticleRequest {
    private String title;
    private String content;
}
