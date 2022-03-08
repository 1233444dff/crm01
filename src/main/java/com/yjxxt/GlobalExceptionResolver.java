package com.yjxxt;

import com.alibaba.fastjson.JSON;
import com.yjxxt.base.ResultInfo;
import com.yjxxt.exceptions.NoLoginException;
import com.yjxxt.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if(ex instanceof NoLoginException){
            //判断异常类型 如果是未登录异常，则先执行相关的拦截操作
            //如果捕获的是未登录异常，重定向到登录页面
            ModelAndView mv =new ModelAndView("redirect:/index");
            return  mv;
        }


        //设置默认异常处理
        ModelAndView mv =new ModelAndView();
        mv.setViewName("error");
        mv.addObject("code",400);
        mv.addObject("msg","系统异常，请稍后登录");
        //判断 handlermethod
        if(handler instanceof HandlerMethod){
            //类型转换
            HandlerMethod handlerMethod=(HandlerMethod)handler;
            //获取方法上的responsbody注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断responsbody注解是否存在（如果存在，表示返回的是试图，如果不存在，表示返回的是json对象
            if(null==responseBody){
                if(ex instanceof ParamsException){
                    ParamsException pe=(ParamsException) ex;
                    mv.addObject("code",pe.getCode());
                    mv.addObject("msg", pe.getMsg());

                }
                return mv;

            }else {
                //方法上返回json对象
                ResultInfo resultInfo =new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试！");
                //如果捕获的是自定义异常
                if (ex instanceof  ParamsException){
                    ParamsException pe =(ParamsException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                //设置相应类型和编码格式 json
                response.setContentType("application/json;charset=utf-8");
                //得到输出流
                PrintWriter out =null;
                try {
                    out =response.getWriter();
                    out.write(JSON.toJSONString(resultInfo));
                    out.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(out!=null){
                        out.close();
                    }
                }
                return null;
            }
        }

        return mv;
    }
}
