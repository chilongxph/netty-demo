package com.hhly.controller;

/**
 * TODO
 *
 * @author xuph-1028
 * @date 2018/3/28 18:35
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hhly.vo.UserInfo;

@Controller
public class LoginController {
    /**
     * 跳转登录页面
     * @return
     */
    @RequestMapping(value = {"","/","index"}, method = RequestMethod.GET)
    public String index(){
        return "index.jsp";
    }

    /**
     * 用户登录
     * @param user
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String doLogin(UserInfo user) {
        UserInfo.map.put(user.getPhone(), user);
        //return "redirect:/msg/list?token="+user.getPhone();
        return "redirect:/chat/list?token="+user.getPhone();
    }
}
