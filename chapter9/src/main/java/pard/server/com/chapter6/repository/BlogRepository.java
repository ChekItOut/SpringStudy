package pard.server.com.chapter6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pard.server.com.chapter6.domain.Article;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
