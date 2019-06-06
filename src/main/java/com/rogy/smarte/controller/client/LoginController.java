package com.rogy.smarte.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.rogy.smarte.entity.db1.User;
import com.rogy.smarte.entity.db1.UserKey;
import com.rogy.smarte.repository.db1.UserDao;
import com.rogy.smarte.resp.ResponseCode;
import com.rogy.smarte.resp.ServerResponse;
import com.rogy.smarte.service.IUserRolePermissionsService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.*;

@RestController
public class LoginController {
    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private final IUserRolePermissionsService IUserRolePermissionsService;
    @Resource
    private UserDao userDao;

    @Autowired
    public LoginController(IUserRolePermissionsService IUserRolePermissionsService) {
        this.IUserRolePermissionsService = IUserRolePermissionsService;
    }

    /**
     * 登录
     *
     * @param appWebUser 用户
     * @return ServerResponse
     */
    @CrossOrigin
    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public ServerResponse ajaxLogin(@RequestBody @Valid User appWebUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = String.format("登陆失败，%s。", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return ServerResponse.customError(ResponseCode.AUTH_FAILED, message);
        }
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(appWebUser.getUsername(), appWebUser.getPassword());
        try {
//            String sessionsId = subject.getSession().getId().toString().replace("-", "");
            subject.login(token);
            if (subject.isAuthenticated()) {
                List<User> users = userDao.findByUsername(appWebUser.getUsername());
                // flush time
                if (users != null && !users.isEmpty()) {
                    User user = users.get(0);
                    user.setTimeMills(BigInteger.valueOf(System.currentTimeMillis()));
                    user = userDao.saveAndFlush(user);
                    // get useKey
                    UserKey userKey = new UserKey();
                    userKey.setUserID(user.getId());
                    userKey.setUserLoginID(user.getUsername());
                    userKey.setPassword(user.getPassword());
                    userKey.setTimeMillis(user.getTimeMills().longValue());
                    userKey.Valid(true);
                    Object principal = subject.getPrincipal();
//                    logger.warn("ajaxLogin() User=[{}] userKey=[{}] userKeyObj=[{}] ", principal, userKey.toUserKeyString(), userKey);
                    return ServerResponse.success(ResponseCode.AUTH_SUCCESS, null, userKey.toUserKeyString());
                }
            }
        } catch (IncorrectCredentialsException e) {
            return ServerResponse.success(ResponseCode.AUTH_FAILED);
        } catch (LockedAccountException e) {
            return ServerResponse.success(ResponseCode.AUTH_FAILED_USER_LOCK);
        } catch (AuthenticationException e) {
            return ServerResponse.success(ResponseCode.AUTH_FAILED_NO_USER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.success(ResponseCode.AUTH_FAILED);
    }

    /**
     * 获取user info
     *
     * @param token sessionId
     * @return ServerResponse
     */
    @CrossOrigin
    @RequestMapping(value = "/auth/info", method = RequestMethod.GET)
    public Object getUserInfo(@RequestParam(value = "token", required = false) String token) {
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        if (principal == null) {
            return ServerResponse.customError(ResponseCode.UNAUTHORIZED);
        }
        Set<String> roles = IUserRolePermissionsService.getRoles(principal.toString());
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", principal);
        map.put("roles", roles);
        map.put("permissions", IUserRolePermissionsService.getPermissions(roles));
        map.put("avatar", IUserRolePermissionsService.getAvatarByUsername(principal.toString()));
        map.put("token", token);
//        logger.warn("getUserInfo() User=[{}] token=[{}] isLogin=[{}] ", principal, token, subject.isAuthenticated());
        return map;
//        return ServerResponse.success(ResponseCode.QUERY_SUCCESS, map);
    }

    /**
     * 登出
     *
     * @return ServerResponse
     */
    @CrossOrigin
    @RequestMapping(value = "/auth/logout", method = RequestMethod.POST)
    public ServerResponse ajaxLogout() {
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        subject.logout();
//        logger.warn("ajaxLogout() User=[{}] isLogin=[{}] ", principal, subject.isAuthenticated());
        return ServerResponse.success(ResponseCode.OUT_SUCCESS, null, null);
    }

    /**
     * 未登录
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/un_auth", method = RequestMethod.GET)
    public ServerResponse unAuth() {
        return ServerResponse.customError(ResponseCode.UNAUTHORIZED);
    }

    /**
     * 被踢出后跳转的页面
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/auth/kickout", method = RequestMethod.GET)
    public ServerResponse kickOut() {
        return ServerResponse.customError(ResponseCode.UNAUTHORIZED);
    }

    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_miniLogin.do", method = RequestMethod.GET)
    public void loginByOpenId(String js_code, String enc, String iv) {
        System.out.println(js_code);
        System.out.println(enc);
        System.out.println(iv);

        final String fuwuAppid = "wx718ec2dbc3762994";
        final String fuwuSecret = "44896e5be0d36b1bb8891d9082d9987c";

        String access_token;//token
        String openId;//openid
        String UnionID;//UnionID
        Integer expires_in;//token 过期时间


        //校验数据的合法性,是否为空
        if (StringUtils.isBlank(js_code)) {
            throw new ServiceException("code empty");
        }


        //调用微信接口获取
        RestTemplate restTemplate = new RestTemplate();
        //获取openId
        String httpOpenIdUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=" + fuwuAppid + "&secret=" + fuwuSecret + "&js_code=" + js_code + "&grant_type=authorization_code";
        ResponseEntity<String> response = restTemplate.getForEntity(httpOpenIdUrl, String.class);
        if (200 != response.getStatusCodeValue()) {
            logger.error("请求失败，获取openId失败");
        }
        String r = response.getBody();
        JSONObject body = JSONObject.parseObject(r);
        System.out.println("1 " + body);

        if ((Integer) body.get("errcode") != null) {
            logger.error("请求成功，但是获取openId失败，错误码：" + body);
        }
        //网页授权token 不一定能用得上
        //access_token = (String) body.get("access_token");
        openId = (String) body.get("openid");

        JedisPool jedisPool = new JedisPool("192.168.3.19");
        //获取基础access_token
        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.select(CommonConstant.REDIS_YANCODE);
            access_token = jedis.get("access_token");

            /** 不存在则重新去请求*/
            if (StringUtils.isBlank(access_token)) {

                //获取access_token
                String httpTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + fuwuAppid + "&secret=" + fuwuSecret;
                ResponseEntity<Map> response2 = restTemplate.getForEntity(httpTokenUrl, Map.class);
                if (200 != response2.getStatusCodeValue()) {
                    logger.error("请求失败，获取access_token失败");
                }
                Map body2 = response2.getBody();
                System.out.println("2 " + body2);

                if ((Integer) Objects.requireNonNull(body2).get("errcode") != null) {
                    logger.error("请求成功，但是获取access_token失败，错误码：" + body2.get("errcode"));
                }
                access_token = (String) body2.get("access_token");
                expires_in = (Integer) body2.get("expires_in");


                //把新的access_token放入redis缓存
                jedis.set("access_token", access_token);
                jedis.expire("access_token", expires_in);


            }
            System.out.println(access_token);
        }

        //获取unionId
        String httpUnionIDUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_token + "&openid=" + openId + "&lang=zh_CN";
        ResponseEntity<String> response1 = restTemplate.getForEntity(httpUnionIDUrl, String.class);
        if (200 != response1.getStatusCodeValue()) {
            logger.error("请求失败，获取UnionId失败");
        }
        String s = response1.getBody();
        JSONObject body3 = JSONObject.parseObject(s);
        System.out.println("3 " + body3);

        if ((Integer) body3.get("errcode") != null) {
            logger.error("请求成功，但是获取UnionId失败，错误码：" + body3);
        }
        UnionID = (String) body3.get("unionid");
        //返回前端数据
        HashMap<String, Object> map = new HashMap<>();
        map.put("UnionID", UnionID);

        System.out.println(map);
    }

}
