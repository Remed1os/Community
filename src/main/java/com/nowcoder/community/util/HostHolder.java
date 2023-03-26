package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author: remedios
 * @Description:持有用户信息用户代替用户对象
 * @create: 2022-11-27 13:18
 */

@Component
public class HostHolder {

    private ThreadLocal<User> local = new ThreadLocal<>();

    public void setUser(User user){
        local.set(user);
    }

    public User getUser(){
        User user = local.get();
        return user;
    }

    public void clear(){
        local.remove();
    }

}
