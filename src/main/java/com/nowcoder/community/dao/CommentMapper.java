package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: remedios
 * @Description:
 * @create: 2022-11-28 19:33
 */

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType,int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

}
