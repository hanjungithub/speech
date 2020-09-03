package com.speech.controller;

import com.speech.elasticsearch.repository.ArticleRepository;
import com.speech.model.Article;
import com.speech.model.ImageTextMessageInfo;
import com.speech.model.WeixinMessageInfo;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is Description
 *
 * @author wangbo
 * @date 2019/02/13
 */
@Controller
@Slf4j
public class WeChatController {
    @Autowired
    private ArticleRepository articleRepository;

    @RequestMapping("/messageFromWeChat")
    public void messageFromWeChat(HttpServletRequest request, HttpServletResponse response, String signature, String echostr) throws Exception {
        System.out.println(request.getRequestURI());
//        if (signature!=null){
//            return echostr;
//        }
        String msg = "success";
        //解析消息
        Map<String, String> map = parseXml(request);
        if (map != null) {
            //回复消息
              //  msg =sendImgMsg(map);
            msg =sendMsg(map);
        }
        //响应消息
        PrintWriter out = null;
        try {
            //设置回复内容编码方式为UTF-8，防止乱码
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            //我们这里将用户发送的消息原样返回
            out.print(msg);
            log.info("==============响应成功==================");
        } catch (Exception e) {
            log.error("获取输出流失败", e);
        } finally {
            if (out != null) {
                out.close();
                out = null;
            }
        }
    }

    /**
     * 封装需要发送的信息
     *
     * @param map
     * @return
     */
    private String sendMsg(Map<String, String> map) {
        WeixinMessageInfo weixinMessageInfo = new WeixinMessageInfo();
        // xml分析
        // 调用消息工具类MessageUtil解析微信发来的xml格式的消息，解析的结果放在HashMap里；
        // 发送方账号
        String fromUserName = map.get("FromUserName");
        weixinMessageInfo.setToUserName(fromUserName);
        System.out.println("fromUserName--->" + fromUserName);
        // 接受方账号（公众号）
        String toUserName = map.get("ToUserName");
        weixinMessageInfo.setFromUserName(toUserName);
        System.out.println("toUserName----->" + toUserName);
        // 消息类型
        String msgType = map.get("MsgType");
        log.info("fromUserName is:" + fromUserName + " toUserName is:" + toUserName + " msgType is:" + msgType);
        //回复类型为文本
        weixinMessageInfo.setMsgType("text");

        String recognition = map.get("Recognition");
        if (StringUtils.isEmpty(recognition)){
            recognition=map.get("Content");
        }
        List<Article> articleList=msgList(recognition);
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("识别到的文字【");
        stringBuffer.append(map.get("Content")==null?map.get("Recognition"):map.get("Content"));
        stringBuffer.append("】为你匹配如下帮助:\n");
        int i=1;
        for (Article article : articleList) {
            stringBuffer.append(i++);
            stringBuffer.append("：<a href=\"");
            stringBuffer.append(article.getUrl());
            stringBuffer.append("\">");
            stringBuffer.append(article.getTitle());
            stringBuffer.append("</a>\n");
        }
        stringBuffer.append("\n\n找不到需要的？试试联系客服电话：10086");
        System.out.println(stringBuffer.toString());
        weixinMessageInfo.setContent(stringBuffer.toString());

        weixinMessageInfo.setCreateTime(System.currentTimeMillis());

        xstream.alias("xml", WeixinMessageInfo.class);
        String toXML = xstream.toXML(weixinMessageInfo);
        System.out.println(toXML);
        return toXML;
    }

    /**
     * 封装发送的图文信息
     *
     * @param map
     * @return
     */
    private String sendImgMsg(Map<String, String> map) {
        ImageTextMessageInfo weixinMessageInfo = new ImageTextMessageInfo();
        // xml分析
        // 调用消息工具类MessageUtil解析微信发来的xml格式的消息，解析的结果放在HashMap里；
        // 发送方账号
        String fromUserName = map.get("FromUserName");
        weixinMessageInfo.setToUserName(fromUserName);
        System.out.println("fromUserName--->" + fromUserName);
        // 接受方账号（公众号）
        String toUserName = map.get("ToUserName");
        weixinMessageInfo.setFromUserName(toUserName);
        System.out.println("toUserName----->" + toUserName);
        // 消息类型
        String msgType = map.get("MsgType");
        log.info("fromUserName is:" + fromUserName + " toUserName is:" + toUserName + " msgType is:" + msgType);
        //回复类型为文本
        weixinMessageInfo.setMsgType("news");

        weixinMessageInfo.setFuncFlag(0);
        // map.get("Recognition");

        String recognition = map.get("Recognition");
        if (StringUtils.isEmpty(recognition)){
           recognition=map.get("Content");
        }
        List<Article> articleList=msgList(recognition);
        //没有匹配到数据
        if (articleList==null||articleList.size()==0){
            Article article=new Article();
            article.setTitle("无匹配数据：用户无法登陆怎么办");
            article.setDescription("请不要心急，慢慢解决");
            article.setPicUrl("https://oimagec6.ydstatic.com/image?id=1348092523780696786&product=dict-homepage&w=200&h=150&fill=0&cw=200&ch=150&sbc=0&cgra=CENTER&of=jpeg");
            article.setUrl("https://lakemoonstar.iteye.com/blog/1471532");
            articleList.add(article);
        }
        articleList.get(0).setDescription(articleList.get(0).getDescription()+"|||识别到的文字信息："+map.get("Content")+"|||识别到的语音信息："+map.get("Recognition"));
        weixinMessageInfo.setArticleCount(articleList.size());
        weixinMessageInfo.setArticles(articleList);



        weixinMessageInfo.setCreateTime(System.currentTimeMillis());

        xstream.alias("xml", ImageTextMessageInfo.class);
        xstream.alias("item",Article.class);
        String toXML = xstream.toXML(weixinMessageInfo);
        System.out.println(toXML);
        return toXML;
    }

    /**
     * 通过后台检索图文消息返回
     * @param recognition
     * @return
     */
    private List<Article> msgList(String recognition){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchQuery("title",recognition));
        Page<Article> articles = articleRepository.search(queryBuilder.build());
        return new ArrayList<>(articles.getContent());
    }





    /**
     * 把流中的xml解析为map
     *
     * @param request
     * @return
     * @throws Exception
     */
    public Map<String, String> parseXml(HttpServletRequest request) throws Exception {

        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
        // 从request中得到输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到XML的根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        @SuppressWarnings("unchecked")
        List<Element> elementList = root.elements();
        // 判断又没有子元素列表
        if (elementList.size() == 0) {
            map.put(root.getName(), root.getText());
        } else {
            for (Element e : elementList)
                map.put(e.getName(), e.getText());
        }
        // 释放资源
        inputStream.close();
        System.out.println("---------xml转换为map-----:" + map);
        return map;



    }

    /**
     * 构造xml转化器
     */
    private XStream xstream = new XStream(new XppDriver() {
        @Override
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记

                @Override
                @SuppressWarnings("rawtypes")
                public void startNode(String name, Class clazz) {
                    if(!"xml".equals(name) && !"item".equals(name)){
                        name = toUpperCaseFirstOne(name);
                    }
                    super.startNode(name, clazz);
                }

                @Override
                protected void writeText(QuickWriter writer, String text) {
                    if (!StringUtils.isNumeric(text)) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });



    //首字母转大写
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }



}
