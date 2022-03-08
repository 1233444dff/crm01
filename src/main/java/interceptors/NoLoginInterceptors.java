package interceptors;

import com.yjxxt.exceptions.NoLoginException;
import com.yjxxt.service.UserService;
import com.yjxxt.utlis.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetSocketAddress;

public class NoLoginInterceptors extends HandlerInterceptorAdapter {
    @Resource
    private UserService userService;
//    判断用户是否是登录状态
//    获取cookie对象，解析用户id的值
//    如果用户id部位空，且在数据库中存在对应的用户记录，表示合法
//    否则请求不合法，进行拦截重定向到登录页面

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的用户id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);

        //判断用户id是否不为空，且在数据库中存在对应的用户记录
        if (null==userId||null==userService.selectByPrimaryKey(userId)){
            //抛出未登录异常
            throw  new NoLoginException();
        }
        return  true;
    }
}
