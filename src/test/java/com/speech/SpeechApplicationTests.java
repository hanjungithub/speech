package com.speech;

import com.speech.elasticsearch.repository.ArticleRepository;
import com.speech.model.Article;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpeechApplicationTests {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void contextLoads() {
    }
    @Test
    public void createIndex(){
        elasticsearchTemplate.createIndex(Article.class);
    }
    @Test
    public void search(){

        //List<Article> articles = articleRepository.findByTitle("搜索隐情。");

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "搜索隐情。"));
        Page<Article> articles = articleRepository.search(queryBuilder.build());

/*
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title","搜索隐情。")).build();
        Page<Article> articles = articleRepository.search(searchQuery);*/
        // elasticsearchTemplate.queryForList(searchQuery, Article.class);
        for (Article a :articles){
            System.out.println(a.getDescription());
        }
    }

    @Test
    public void addArticle(){
        Article article = new Article();
        article.setTitle("我是百度搜索引擎");
        article.setDescription("百度搜索引擎");
        article.setPicUrl("https://www.sogou.com/images/logo_440x140.v.4.png");
        article.setUrl("https://www.baidu.com/");
        articleRepository.save(article);
        Article article1 = new Article();
        article1.setTitle("我是谷歌搜索引擎");
        article1.setDescription("谷歌搜索引擎");
        article1.setPicUrl("https://www.sogou.com/images/logo_440x140.v.4.png");
        article1.setUrl("https://www.google.com/");
        articleRepository.save(article1);
        Article article2 = new Article();
        article2.setTitle("我是搜狗搜索引擎");
        article2.setDescription("搜狗搜索引擎");
        article2.setPicUrl("https://www.sogou.com/images/logo_440x140.v.4.png");
        article2.setUrl("https://www.sogou.com/");
        articleRepository.save(article2);
        Article article3 = new Article();
        article3.setTitle("我是必应搜索引擎");
        article3.setDescription("必应搜索引擎");
        article3.setPicUrl("https://www.sogou.com/images/logo_440x140.v.4.png");
        article3.setUrl("https://www.bing.com/");
        articleRepository.save(article3);

    }

}

