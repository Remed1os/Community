package com.nowcoder.community;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.nowcoder.community.util.CommunityConstant.TOPIC_LIKE;

/**
 * @author: remedios
 * @Description:
 * @create: 2022-12-02 21:28
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class test {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private EventProducer eventProducer;

    public static void main(String[] args) {

        test test = new test();
        test.testjason();

    }


    @Test
    public void testjason(){
        Event event = new Event()
                .setTopic(TOPIC_LIKE)
                .setUserId(1)
                .setEntityType(1)
                .setEntityId(1)
                .setEntityUserId(1)
                .setData("postId",1);
        eventProducer.fireEvent(event);
    }
}


