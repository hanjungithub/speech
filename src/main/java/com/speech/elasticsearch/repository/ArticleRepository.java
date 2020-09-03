package com.speech.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

/**
 * @author hanjun
 * @date 2019/2/14
 */
public interface ArticleRepository extends ElasticsearchRepository<com.speech.model.Article,UUID> {
}
