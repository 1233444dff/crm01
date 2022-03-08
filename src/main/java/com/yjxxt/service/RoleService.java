package com.yjxxt.service;

import com.yjxxt.base.BaseService;
import com.yjxxt.bean.Permission;
import com.yjxxt.bean.Role;
import com.yjxxt.mapper.ModuleMapper;
import com.yjxxt.mapper.PermissionMapper;
import com.yjxxt.mapper.RoleMapper;
import com.yjxxt.mapper.UserMapper;
import com.yjxxt.query.RoleQuery;
import com.yjxxt.utlis.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {
    @Autowired(required = false)
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;


    public List<Map<String,Object>> findRoles(Integer userId){

        return roleMapper.selectRoles(userId);

    }

    public Map<String,Object> findAllRole(RoleQuery query){

      List<Role>  rlist = roleMapper.selectByParams(query);
        Map <String,Object> map =new HashMap<>();
        map.put("code",0);
        map.put("msg","success");
        map.put("count",rlist.size());
        map.put("data",rlist);
        return map;

    }
    public  void addRole(Role role){
        //角色民非空
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名不为空");
        //角色名称已存在
            Role          temp  = roleMapper.selectRoleByName(role.getRoleName());
            AssertUtil.isTrue(temp!=null,"角色名已存在");
            role.setCreateDate(new Date());
            role.setUpdateDate(new Date());
            role.setIsValid(1);
            //是否添加成功
        AssertUtil.isTrue(roleMapper.insertHasKey(role)<1,"角色添加失败");
    }
    public void changeRole(Role role){
        //当前对象roleId

        AssertUtil.isTrue(  roleMapper.selectByPrimaryKey(role.getId())==null || role.getId()==null,"待修改用户不存在");
        //绝色名称要唯一
        Role          temp  = roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(temp!=null && !(temp.getId().equals(role.getId())),"角色名已存在");

        //默认修改时间
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"角色修改失败");
        //是否修改成功


    }

    public void removeRoleById(Integer roleId) {
        AssertUtil.isTrue(roleId==null || roleMapper.selectByPrimaryKey(roleId)==null,"请求删除的数据");
        AssertUtil.isTrue(roleMapper.deleteByPrimaryKey(roleId)<1,"删除失败了");
    }

    public  void addGrant(Integer roleId,String[]mids){
        //验证roleid是否存在
        Role      temp= roleMapper.selectByPrimaryKey(roleId);
        AssertUtil.isTrue(roleId==null,"请选择受传的角色");
        //资源存在
        if(mids!=null || mids.length>0){
            //角色原来是否拥有资源
            //若有资源就新增一部分
            // 删除原来的资源重新分配
           // 统计角色拥有多少个资源，删除重新分配

            int count = permissionMapper.countModuleByRoleId(roleId);
            if(count>0){
                //删除原来的任务信息

                AssertUtil.isTrue(  permissionMapper.deleteModuleByRoleId(roleId)!=count,"操作失败");
            }
            //重新分配资源
            //t_permission t_role

            List<Permission>  plist=  new ArrayList<Permission>();
            for (String mid:mids) {

                Permission        permission   =  new Permission();
                permission.setRoleId(roleId);
                permission.setModuleId(Integer.parseInt(mid));
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());

                //获取当前资源的权限码
                String  optValue =moduleMapper.selectByPrimaryKey(Integer.parseInt(mid)).getOptValue();
                permission.setAclValue(optValue);
                 //添加到当前容器的plist
                plist.add(permission);
            }

            permissionMapper.insertBatch(plist);

        }


    }
}
