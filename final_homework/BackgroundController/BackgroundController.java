package cn.edu.bjtu.weibo.controller;

import cn.edu.bjtu.weibo.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台管理页面的Controller
 *
 * @author 王国桥
 */
@RestController
@RequestMapping("/b")
public class BackgroundController {
    @Autowired
    private UserManageService userManageService;

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
    @RequestMapping(value = "/banUserById", method = RequestMethod.GET)
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
    @RequestMapping(value = "/unBanUserById", method = RequestMethod.GET)
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
    @RequestMapping(value = "/banUserByName", method = RequestMethod.GET)
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
    @RequestMapping(value = "/unBanUserByName", method = RequestMethod.GET)
    public String unBanUserByName(String userName) {
        if (userManageService.unBanUser(userName)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }
}
