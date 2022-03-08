package com.yjxxt.controller;


import com.yjxxt.base.BaseController;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.bean.SaleChance;
import com.yjxxt.mapper.SaleChanceMapper;
import com.yjxxt.query.SaleChanceQuery;
import com.yjxxt.service.SaleChanceService;
import com.yjxxt.service.UserService;
import com.yjxxt.utlis.AssertUtil;
import com.yjxxt.utlis.LoginUserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Autowired
    private UserService userService;

//    @Autowired(required = false)
//    private SaleChanceMapper saleChanceMapper;

    //多条件分页查询营销机会
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery query) {
        return saleChanceService.querySaleChanceByParams(query);
    }

    //进入营销机会页面
    @RequestMapping("index")
    public String index() {
        return "saleChance/sale_chance";
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(HttpServletRequest req, SaleChance saleChance) {
        //指定分配人
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //查询用户信息
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        saleChance.setAssignMan(trueName);
        //调用方法添加
        saleChanceService.insertSelective(saleChance);
        //返回目标对象
        return success("添加成功了");
    }
    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id, Model model){
        return "saleChance/add_update";
    }

    @RequestMapping("delete")
    @ResponseBody
    //删除营销机会的数据
    public ResultInfo deleteSaleChance(Integer[]ids){
        //删除营销机会数据
        saleChanceService.deleteSaleChance(ids);
        return  success("营销机会数据删除成功！");
    }

//    多条件分页查询营销机会
//    @RequestMapping("list")
//    @ResponseBody
//    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery query,HttpServletRequest request,Integer flag){
//        if(null!=null&&flag==1){
//            //获取当前用户登录id
//            Integer userId =LoginUserUtil.releaseUserIdFromCookie(request);
//            query.setAssignMan(userId);
//        }
//        return saleChanceService.querySaleChanceByParams(query);
//    }
    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String,Object>> saySales(){
        return userService.findSales();
    }


}
