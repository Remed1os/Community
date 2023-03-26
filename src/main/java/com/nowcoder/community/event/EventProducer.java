package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: remedios
 * @Description:
 * @create: 2022-12-01 19:19
 */
@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event){
        String topic = event.getTopic();
        Object o = JSONObject.toJSON(event);

        kafkaTemplate.send(topic,o);

    }

}
