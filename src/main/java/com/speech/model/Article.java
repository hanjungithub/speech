package com.speech.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Score;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;

/**
 * This is Description
 *
 * @author wangbo
 * @date 2019/02/14
 */
@Setter
@Getter
@Setting(settingPath = "elasticsearch/settings.json")
@Mapping(mappingPath = "elasticsearch/mappings.json")
@Document(indexName = "article",type = "info")
public class Article  implements Serializable {

    private static final long serialVersionUID = -2130080434328938808L;
    @Id
    private String id;
    private String title;
    private String description;
    private String picUrl;
    private String url;
    @Score
    private float score;
}
