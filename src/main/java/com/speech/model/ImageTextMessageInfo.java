package com.speech.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 图文消息实体类
 *
 * @author wangbo
 * @date 2019/02/14
 */
@Setter
@Getter
public class ImageTextMessageInfo {

    private String ToUserName;
    private String FromUserName;
    private Long CreateTime;
    /**
     * 消息类型，news
     */
    private String MsgType;
    private Integer FuncFlag;
    /**
     * 图文消息个数
     */
    private Integer ArticleCount;
    /**
     * 图文消息集合
     */
    private List<Article> Articles;




}
