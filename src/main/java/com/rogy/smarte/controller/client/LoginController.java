package com.rogy.smarte.controller.client;

import com.rogy.smarte.entity.db1.User;
import com.rogy.smarte.entity.db1.UserKey;
import com.rogy.smarte.repository.db1.UserDao;
import com.rogy.smarte.resp.ResponseCode;
import com.rogy.smarte.resp.ServerResponse;
import com.rogy.smarte.service.IUserRolePermissionsService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
}
