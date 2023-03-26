package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author: remedios
 * @Description:
 * @create: 2022-11-29 19:12
 */

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    //获取会话列表
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();

        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //会话列表
        List<Message> conversationsList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationsList != null){
            for (Message message : conversationsList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));
            }
        }
        model.addAttribute("conversations",conversations);

        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }


    // 获取私信详情
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {

        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }


    // 发送私信
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    public String sendLetter(String toName,String content){

        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在！");
        }

        int fromId = hostHolder.getUser().getId();
        int toId = target.getId();
        Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);

        //设置会话id  101-102
        if(fromId > toId){
            message.setConversationId(toId + "-" + fromId);
        }else{
            message.setConversationId(fromId + "-" + toId);
        }

        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        //查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(),TOPIC_COMMENT);
        if(message != null){
            Map<String,Object> messageVo = new HashMap<>();
            messageVo.put("message",messageVo);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> date = JSONObject.parseObject(content,HashMap.class);

            messageVo.put("user",userService.findUserById((Integer) date.get("userID")));
            messageVo.put("entityType",date.get("entityType"));
            messageVo.put("entityId",date.get("entityId"));
            messageVo.put("postId",date.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unread",unread);
            model.addAttribute("commentNotice",messageVo);
        }

        //查询关注类通知
        message = messageService.findLatestNotice(user.getId(),TOPIC_FOLLOW);
        if(message != null){
            Map<String,Object> messageVo = new HashMap<>();
            messageVo.put("message",messageVo);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> date = JSONObject.parseObject(content,HashMap.class);

            messageVo.put("user",userService.findUserById((Integer) date.get("userID")));
            messageVo.put("entityType",date.get("entityType"));
            messageVo.put("entityId",date.get("entityId"));
            messageVo.put("postId",date.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unread",unread);
            model.addAttribute("followNotice",messageVo);
        }


        //查询点赞类通知
        message = messageService.findLatestNotice(user.getId(),TOPIC_LIKE);
        if(message != null){
            Map<String,Object> messageVo = new HashMap<>();
            messageVo.put("message",messageVo);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> date = JSONObject.parseObject(content,HashMap.class);

            messageVo.put("user",userService.findUserById((Integer) date.get("userID")));
            messageVo.put("entityType",date.get("entityType"));
            messageVo.put("entityId",date.get("entityId"));
            messageVo.put("postId",date.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unread",unread);
            model.addAttribute("likeNotice",messageVo);
        }

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/notice";

    }

        @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
        public String getMessageDetails(@PathVariable("topic") String topic,Page page,Model model){
            User user = hostHolder.getUser();

            page.setLimit(5);
            page.setPath("/notice/detail/" + topic);
            page.setRows(messageService.findNoticeCount(user.getId(), topic));

            List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
            List<Map<String,Object>> noticeVoList = new ArrayList<>();
            if(noticeList != null) {
                for (Message notice : noticeList) {
                    HashMap<String, Object> map = new HashMap<>();
                    //通知
                    map.put("notice", notice);

                    //内容
                    String content = HtmlUtils.htmlUnescape(notice.getContent());
                    Map<String, Object> data = JSONObject.parseObject(content, Map.class);
                    map.put("user", userService.findUserById((Integer) data.get("userId")));
                    map.put("entityType", data.get("entityType"));
                    map.put("entityId", data.get("entityId"));
                    map.put("postId", data.get("postId"));

                    //通知作者
                    map.put("fromUser", userService.findUserById((Integer) data.get("fromId")));

                    noticeVoList.add(map);
                }
            }
                model.addAttribute("notices",noticeVoList);

                //设置已读
                List<Integer> ids = getLetterIds(noticeList);
                if(!ids.isEmpty()){
                    messageService.readMessage(ids);
                }

            return "/site/notice-detail";
        }








    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

}
