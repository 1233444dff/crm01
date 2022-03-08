package com.yjxxt.controller;

import com.yjxxt.base.BaseController;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.bean.Role;
import com.yjxxt.query.RoleQuery;
import com.yjxxt.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.management.Query;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    @Autowired(required = false)
    private RoleService roleService;

    @RequestMapping("roles")
    @ResponseBody
    public List<Map<String,Object>> sayRoles(Integer userId){
        return  roleService.findRoles(userId);

    }
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> findRoleByParam(RoleQuery query){
        Map<String,Object>        map  =roleService.findAllRole(query);
        return map;
    }
    @RequestMapping("index")
    public String index(){
        return "role/role";
    }

    @RequestMapping("addOrUpdateRole")
    public String addOrUpdate(Integer roleId, Model model){
        if(roleId==null){
            model.addAttribute("role",roleService.selectByPrimaryKey(roleId));
        }
        return "role/add_update";
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(Role role){
        //调用方法
        roleService.addRole(role);
        return success("角色添加成功");

    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(Role role){
        roleService.changeRole(role);
        return success("角色修改成功了");
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo del(Integer roleId){
        roleService.removeRoleById(roleId);
        return success("角色删除成功了");

    }

    @RequestMapping("toRoleGrantPage")
    public  String sayGrand(Integer roleId,Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";

    }
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo grant(Integer roleId,String [] mids){
        roleService.addGrant(roleId,mids);
        return success("角色售权成功");
    }
}
