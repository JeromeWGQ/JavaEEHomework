package cn.edu.bjtu.weibo.controller;

import cn.edu.bjtu.weibo.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 后台管理页面的Controller
 *
 * @author 王国桥
 */
@Controller
public class BackgroundController {
    private UserManageService userManageService;

    /**
     * 构造器，自动注入需要的Service
     *
     * @param userManageService 要注入的loginService
     */
    @Autowired
    public BackgroundController(UserManageService userManageService) {
        this.userManageService = userManageService;
    }

    /**
     * 通过userId封禁用户
     *
     * @param userId 要封禁用户的用户Id
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 封禁成功；2 - 封禁失败
     */
    @RequestMapping("/banUserById")
    public String banUserById(String userId) {
        if (userManageService.banUser(userId)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 通过userId解封用户
     *
     * @param userId 要封禁用户的用户Id
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 封禁成功；2 - 封禁失败
     */
    @RequestMapping("/unBanUserById")
    public String unBanUserById(String userId) {
        if (userManageService.unBanUser(userId)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 通过userName封禁用户
     *
     * @param userName 要封禁用户的用户名
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 封禁成功；2 - 封禁失败
     */
    @RequestMapping("/banUserByName")
    public String banUserByName(String userName) {
        if (userManageService.banUser(userName)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 通过userName解封用户
     *
     * @param userName 要封禁用户的用户名
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 封禁成功；2 - 封禁失败
     */
    @RequestMapping("/unBanUserByName")
    public String unBanUserByName(String userName) {
        if (userManageService.unBanUser(userName)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }
}
