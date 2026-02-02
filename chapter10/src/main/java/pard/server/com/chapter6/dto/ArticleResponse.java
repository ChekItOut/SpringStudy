package pard.server.com.chapter6.dto;

import lombok.*;
import pard.server.com.chapter6.domain.Article;

@AllArgsConstructor
@Getter
public class ArticleResponse {
    private final String title;
    private final String content;

    public ArticleResponse(Article article) {
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
