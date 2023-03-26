package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: remedios
 * @Description:
 * @create: 2022-11-27 18:43
 */

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try(
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword = reader.readLine()) != null){
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("敏感词文件读取失败！");
        }
    }

    //过滤敏感词
    public String filter(String text){

        if(StringUtils.isBlank(text)){
            return null;
        }

        //指针一
        TrieNode tempNode = new TrieNode();
        //指针二
        int begin = 0;
        //指针三
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()){

            if(position < text.length()) {

                Character c = text.charAt(position);
                //跳过符号
                if (isSymbol(c)) {//判断为特殊字符
                    if (tempNode == rootNode) {
                        sb.append(c);
                        begin++;
                    }
                    position++;
                    continue;
                }

                //检查下级树节点
                tempNode = tempNode.getSubNode(c);
                if(tempNode == null){
                    //以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    //进入下一个位置
                    position = ++begin;
                    //重新指向根节点
                    tempNode = rootNode;
                }else if(tempNode.isKeywordEnd()){//发现敏感词
                    sb.append(REPLACEMENT);
                    begin = ++position;
                    tempNode = rootNode;
                }else{//检查下一个节点
                    position++;
                }

            }else{ // position遍历越界仍未匹配到敏感词
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }
        }
            return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        // 0x2E80 ~ 0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //将一个敏感词添加到前缀树中去
    public void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);

            //判断是否已经存在此节点
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            //移动指针，指向子节点
            tempNode = subNode;

            //设置结束标识
            if(i == keyword.length() -1 ){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    //前缀树
    private class TrieNode{

        //关键词结束标识
        private boolean isKeywordEnd;

        //子节点(key是下一级字符)
        private Map<Character,TrieNode> subNode = new HashMap<>();

        public boolean isKeywordEnd(){
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd){
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNode.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNode.get(c);

        }
    }

}
