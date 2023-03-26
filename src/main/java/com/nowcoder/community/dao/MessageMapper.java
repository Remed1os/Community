package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: remedios
 * @Description:
 * @create: 2022-11-29 17:17
 */

@Mapper
public interface MessageMapper {

    //查询当前用户的会话列表，针对每个会话只显示一条消息
    List<Message> selectConversations(int userId,int offset,int limit);

    //查询某个会话所包含的会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversations,int offset,int limit);

    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversations);

    //查询未读私信的数量
    int selectLetterUnreadCount(int userId,String conversationId);

    // 新增消息
    int insertMessage(Message message);

    // 修改消息的状态
    int updateStatus(List<Integer> ids, int status);

    //查询某个主题下最新的通知
    Message selectLatestNotice(int userId,String topic);

    //查询某个主题所包含的通知数量
    int selectNoticeCount(int userId,String topic);

    //查询未读的通知的数量
    int selectUnreadNoticeCount(int userId,String topic);

    //查询某个主题所包含的通知列表
    List<Message> selectNotices(int userId,String topic,int offset,int limit);



}
