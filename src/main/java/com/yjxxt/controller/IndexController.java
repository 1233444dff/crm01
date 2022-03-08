package com.yjxxt.controller;

import com.yjxxt.base.BaseController;
import com.yjxxt.bean.Permission;
import com.yjxxt.bean.User;
import com.yjxxt.service.PermissionService;
import com.yjxxt.service.UserService;
import com.yjxxt.utlis.LoginUserUtil;
import com.yjxxt.utlis.UserIDBase64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class IndexController extends BaseController {
    @Autowired
    public UserService userService;

    @Resource
    private PermissionService permissionService;

    @GetMapping("index")
    public String index(){
        return "index";
    }
    @GetMapping("welcome")
    public  String welcome(){
        return "welcome";
    }
    @GetMapping("main")
    public String main(HttpServletRequest request){
        //通过工具类，从cookie中获取userid
       Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
       //调用对应service层的方法.通过userid主键查询用户对象
        User user=userService.selectByPrimaryKey(userId);
        //将用户对象设置到request作用域中
        request.setAttribute("user",user);
        //统计用户拥有的权限码
        List<String> permisssions=permissionService.queryUserHasRolesHasPermissions(userId);
        System.out.println("--->"+permisssions.size());
        //储蓄
        request.getSession().setAttribute("permisssions",permisssions);
        return "main";
    }


}
