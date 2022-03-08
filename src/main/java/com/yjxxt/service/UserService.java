package com.yjxxt.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.yjxxt.base.BaseService;
import com.yjxxt.bean.User;
import com.yjxxt.bean.UserRole;
import com.yjxxt.mapper.UserMapper;
import com.yjxxt.mapper.UserRoleMapper;
import com.yjxxt.model.UserModel;
import com.yjxxt.query.UserQuery;
import com.yjxxt.utlis.AssertUtil;
import com.yjxxt.utlis.Md5Util;
import com.yjxxt.utlis.PhoneUtil;
import com.yjxxt.utlis.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.PrivateKey;
import java.util.*;

@Service
public class UserService extends BaseService<User, Integer> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    public UserModel userLogin(String userName, String userPwd) {
        CheckLoginParams(userName, userPwd);
        User user = userMapper.SelectUserByName(userName);
        AssertUtil.isTrue(user == null, "用户已注销或不存在");
        checkLoginPwd(userPwd, user.getUserPwd());
        return builderUserInfo(user);
    }

    //构建用户对象
    private UserModel builderUserInfo(User user) {
        //实例化
        UserModel userModel = new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTureName(user.getUserPwd());
        return userModel;
    }

    //   校验用户名和密码是和数据库用户名和密码是否匹配
    private void checkLoginPwd(String userPwd, String userPwd1) {
        String encode = Md5Util.encode(userPwd);
        //比对密码是否正确
        AssertUtil.isTrue(
                !(encode.equals(userPwd1)),
                "用户密码不正确");
    }

    private void CheckLoginParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空");
    }

    public void updateUserPassword(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        //通过userid获取用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        //参数校验
        checkPasswordParams(user, oldPassword, newPassword, confirmPassword);
        //设置用户新密码
        user.setUserPwd(Md5Util.encode(newPassword));
        //执行更新操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "用户密码更新失败！");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void checkPasswordParams(User user, String oldPassword, String newPassword, String confirmPassword) {
        //user对象 非空在验证
        AssertUtil.isTrue(null == user, "用户未登录或不存在！");
        //原始密码 非空
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword), "请输入原始密码");
        //原始密码要与数据库密码保持一致
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))), "新密码不能与原始密码相同！");
        //确认密码 非空校验
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword), "请输入正确密码！");
        //新密码要与确认密码保持一致
        AssertUtil.isTrue(!(newPassword.equals(confirmPassword)), "新密码与确认密码保持一致");
    }

    public List<Map<String,Object>> findSales(){
        return userMapper.selectSales();
    }
     public  Map<String,Object> findAll(UserQuery query){
        //初始化分页数据
         PageHelper.startPage(query.getPage(),query.getLimit());
        //条件查询
        List<User>    ulist =userMapper.selectByParams(query);
        //开始分页
         PageInfo<User> plist =new PageInfo<>(ulist);
        Map<String,Object>  map =new HashMap();
        map.put("code",0);
        map.put("msg","success");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        //返回map
         return  map;
     }
        //添加操作
        //对数据的校验
     public void saveUser(User user){
        checkUser(user.getUserName(),user.getEmail(),user.getPhone());
        //创建日期 更新时间
         user.setCreateDate(new Date());
         user.setUpdateDate(new Date());
         user.setUserPwd(Md5Util.encode("123456"));
         user.setIsValid(1);

         //判断是否登录成功
         AssertUtil.isTrue(userMapper.insertSelective(user)<1,"添加失败");

         System.out.println(user.getId()+"--->"+user.getRoleIds());

         //关联用户和角色id
         relationUserRole(user.getId(),user.getRoleIds());
     }

    private void relationUserRole(Integer userId, String roleIds) {
        //用户赋予角色
        //原来用户没有角色,添加新的角色
        //用户原来存角色，添加新角色
        //？？
        //统计一下用户有多少个角色，删除原来的角色，然后赋予新的角色
        //准备数据
        int count =userRoleMapper.countUserOfRoleByUserId(userId);
        if(count>0){
//            删除用户角色
            AssertUtil.isTrue(userRoleMapper.deleteUserOfRoleByUserId(userId)!=count,"角色分配失败");
        }
        List<UserRole> urlist= new ArrayList<UserRole>();
        //用户角色对象
        if(roleIds!=null&&roleIds.length()>0){
            for (String roleId :roleIds.split(",")){
                UserRole   userRole =  new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());

                urlist.add(userRole);
            }
            AssertUtil.isTrue(userRoleMapper.insertBatch(urlist)!=urlist.size(),"用户角色添加失败");
        }





    }

    //对数据的校验
    private void checkUser(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(email),"用户邮箱不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"用户手机号不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"必须输入正确的手机号");
    }

    public void changeUser(User user){
        User temp =userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(temp==null,"修改的记录不存在");
        checkUser(user.getUserName(),user.getEmail(),user.getPhone());
        user.setUpdateDate(new Date());
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败了");

        relationUserRole(user.getId(),user.getRoleIds());
    }

    public  void  removeId(Integer[] ids){
        AssertUtil.isTrue(ids==null||ids.length==0,"选择正确的数据");
        AssertUtil.isTrue(userMapper.deleteBatch(ids)!=ids.length,"删除异常");
        for (Integer userId: ids) {
            int count =userRoleMapper.countUserOfRoleByUserId(userId);
            if(count>0){

//            删除用户角色
                AssertUtil.isTrue(userRoleMapper.deleteUserOfRoleByUserId(userId)!=count,"角色分配失败");
            }
        }
    }
}
