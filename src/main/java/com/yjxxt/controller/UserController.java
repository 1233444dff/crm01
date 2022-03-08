package com.yjxxt.controller;

import com.yjxxt.annotation.RequiredPermission;
import com.yjxxt.base.BaseController;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.bean.User;
import com.yjxxt.exceptions.ParamsException;
import com.yjxxt.mapper.UserMapper;
import com.yjxxt.model.UserModel;
import com.yjxxt.query.UserQuery;
import com.yjxxt.service.UserService;
import com.yjxxt.utlis.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Map;

@Controller
public class UserController extends BaseController {
    @Resource
    private UserService userService;
//    用户登录
        @RequestMapping ("user/login")
        @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){
        ResultInfo       resultInfo  = new ResultInfo();
//        try{
        UserModel userModel =userService.userLogin(userName,userPwd);
        resultInfo.setResult(userModel);
//
//        }catch (ParamsException e){
//            //自定义异常
//            e.printStackTrace();
//            //设置状态码和提示信息
//            resultInfo.setCode(e.getCode());
//            resultInfo.setMsg(e.getMsg());
//            return resultInfo;
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//            resultInfo.setCode(500);
//            resultInfo.setMsg("操作失败");
//            return resultInfo;
//        }
        return  resultInfo;
    }

    @RequestMapping("user/updatePassword")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,String oldPassword,String newPassword,String confirmPassword){
            ResultInfo resultInfo =new ResultInfo();
            //通过try catch 捕获 service 层抛出的异常
//    try {
              Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
//            //通过service层的密码修改
           userService.updateUserPassword(userId,oldPassword,newPassword,confirmPassword);
//        }catch (ParamsException e){
//            e.printStackTrace();
//            //设置状态码和提示信息
//            resultInfo.setCode(e.getCode());
//            resultInfo.setMsg(e.getMsg());
//        }catch (Exception e){
//            e.printStackTrace();
//            resultInfo.setCode(500);
//            resultInfo.setMsg("操作失败！");
//        }


        return  resultInfo;

}

        @RequestMapping("user/toPasswordPage")
        public  String toPasswordPage(){
            return "/user/password";
        }

        @RequestMapping("user/list")
        @ResponseBody
        @RequiredPermission(code = "6010")
        public Map<String,Object>list(UserQuery query){
            //查询条件
            Map<String,Object> map = userService.findAll(query);
            //返回map
            return map;
        }

        @RequestMapping("user/index")
        public String index(){
            return "user/user";
        }

        @RequestMapping("user/save")
        @ResponseBody
        public ResultInfo save(User user){
            userService.saveUser(user);
            return success("添加成功了");

        }

    @RequestMapping("user/update")
    @ResponseBody
    public ResultInfo upDate(User user){
        userService.changeUser(user);
        return success("添加成功了");

    }

    @RequestMapping("user/addOrUpdateDialog")
    public String addOrUpdate(Integer userId, Model model){
            if(userId!=null){
            User    user =userService.selectByPrimaryKey(userId);
                System.out.println(user);
            model.addAttribute("user",user);
            }

        return "user/add_update";
    }

    @RequestMapping("user/delete")
    @ResponseBody
    public ResultInfo delete(Integer[]ids){
        userService.removeId(ids);
        return success("删除成功了");

    }


}

