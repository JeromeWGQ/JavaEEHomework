//=========================================================
//    除Spring外，此文件还需依赖Google的gson-2.3.1.jar
//=========================================================
package cn.edu.bjtu.weibo.controller;

import cn.edu.bjtu.weibo.model.Picture;
import cn.edu.bjtu.weibo.model.User;
import cn.edu.bjtu.weibo.service.*;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 老师说这是 -> 显示个人信息页面的Controller</br>
 * 我觉得这是 -> 所有用户相关的操作都要我负责 =_=</br>
 * <p>
 * 目前已实现的功能：</br>
 * 1. 用户登录</br>
 * 2. 用户注册</br>
 * 3. 根据用户名查找用户Id</br>
 * 4. 获取当前用户名/用户Id</br>
 * 5. 获取某一用户关注的人/粉丝列表（分页/不分页）</br>
 * 6. 获取某一用户关注/粉丝数</br>
 * 7. 获取/更新用户个人信息</br>
 * 8. 获取/更新用户头像
 *
 * @author 王国桥
 */
@Controller
public class UserController {
    /**
     * service中的很多方法都是以分页的形式定义的，要一次性获取所有页上的所有
     * 信息，需要指定一个足够大的numberPerPage参数，DEFAULT_NUMBER_PER_PAGE
     * 是这个参数的默认值
     */
    private static final int DEFAULT_NUMBER_PER_PAGE = 100;
    /**
     * 获取用户Id的方法在LoginService中，需要两个参数，username和password，而很多时候
     * 我们需要的是通过username取得用户Id，这里的userMap会保存登录和注册过程中涉及到的
     * 用户密码，方便获取Id
     */
    private static Map<String, String> userMap = new HashMap<>();
    /**
     * 保存当前登录的用户名
     */
    private static String currentUser;
    private LoginService loginService;
    private RegisterService registerService;
    private LogoutService logoutService;
    private UserProfileService userProfileService;
    private UserAvatarsService userAvatarsService;
    private FollowerUsersService followerUsersService;
    private FollowingUsersService followingUsersService;
    private FollowerNumberService followerNumberService;
    private FollowingNumberService followingNumberService;

    /**
     * 构造器，自动注入需要的Service
     *
     * @param loginService           要注入的loginService
     * @param registerService        要注入的registerService
     * @param logoutService          要注入的logoutService
     * @param userProfileService     要注入的userProfileService
     * @param userAvatarsService     要注入的userAvatarsService
     * @param followerUsersService   要注入的followerUsersService
     * @param followingUsersService  要注入的followingUsersService
     * @param followerNumberService  要注入的followerNumberService
     * @param followingNumberService 要注入的followingNumberService
     */
    @Autowired
    public UserController(LoginService loginService, RegisterService registerService, LogoutService logoutService, UserProfileService userProfileService, UserAvatarsService userAvatarsService, FollowerUsersService followerUsersService, FollowingUsersService followingUsersService, FollowerNumberService followerNumberService, FollowingNumberService followingNumberService) {
        this.loginService = loginService;
        this.registerService = registerService;
        this.logoutService = logoutService;
        this.userProfileService = userProfileService;
        this.userAvatarsService = userAvatarsService;
        this.followerUsersService = followerUsersService;
        this.followingUsersService = followingUsersService;
        this.followerNumberService = followerNumberService;
        this.followingNumberService = followingNumberService;
    }

    /**
     * 用户登录
     *
     * @param name     用户名
     * @param password 密码
     * @return json串
     * <p>
     * json串格式：
     * {
     * "status": "success",
     * "tipCode": 200,
     * "tipMsg": "ok",
     * "data": "value"
     * }
     * 其中value：成功为所需要跳转的页面的html路径，如："/Main.html"；失败为 1
     */
    @RequestMapping("/login")
    public String login(String name, String password) {
//        loginService = new LoginServiceImpl();
        // 判断用户是否存在
        if (!loginService.isUserExisted(name, password)) {
            return wrapLoginOrRegisterWithValue("1");
        }
        // 用户存在，保存信息至userMap
        userMap.put(name, password);
        // 记录当前登录用户
        currentUser = name;
        return wrapLoginOrRegisterWithValue("/mainPage.html");
    }

    /**
     * 用户注册
     *
     * @param name     用户名
     * @param password 密码
     * @return json串
     * <p>
     * json串格式：
     * {
     * "status": "success",
     * "tipCode": 200,
     * "tipMsg": "ok",
     * "data": "value"
     * }
     * 其中value：成功为1，失败为0
     */
    @RequestMapping("/signin")
    public String signin(String name, String password) {
        // 失败返回的字符串
        String failedStr = wrapLoginOrRegisterWithValue("0");
//        registerService = new RegisterServiceImpl();
        // 判断用户名是否被占用
        if (registerService.isUserNameExisted(name))
            return failedStr;
        // 开始注册
        boolean registerSuccess;
        registerSuccess = registerService.registerNewUser(name, password);
        if (registerSuccess) {
            // 保存至userMap
            userMap.put(name, password);
            // 创建新用户
            User user = new User();
            user.setName(name);
            user.setAge("20");
            user.setBirthday("2000-1-1");
            user.setEducation("本科");
            user.setEmail("14301xxx@bjtu.edu.cn");
            user.setIntroduction("BJTU");
            user.setLastWeiboId("0");
            user.setLocation("Beijing");
            user.setPhone("010-88888888");
            user.setQq("123456");
            user.setSex("男");
            userProfileService.createNewUser(user);
            return wrapLoginOrRegisterWithValue("1");
        }
        return failedStr;
    }

    /**
     * 获取当前登录用户的用户名
     *
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value是当前登录用户的用户名
     */
    @RequestMapping("/currentUserName")
    public String getCurrentUserName() {
        return "[\"" + currentUser + "\"]";
    }

    /**
     * 获取当前登录用户的用户Id
     *
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value是当前登录用户的用户Id
     */
    @RequestMapping("/currentUserId")
    public String getCurrentUserId() {
        return "[\"" + getUserIdByUsername(currentUser) + "\"]";
    }

    /**
     * 查找某用户关注的人的用户Id列表
     *
     * @param userId 要查找的用户的用户Id
     * @return json串
     * <p>
     * json串格式：
     * ["id1", "id2", ...]
     * 其中id1, id2, ...是关注的人的用户Id列表（全部）
     */
    @RequestMapping("/followingUserIdList")
    public String getFollowingUserIdList(String userId) {
        List<String> list = followingUsersService.getFollowingUserIdList(userId, 0, DEFAULT_NUMBER_PER_PAGE);
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * 查找某用户关注的人的用户Id列表（以分页形式）
     *
     * @param userId        要查找的用户的用户Id
     * @param pageIndex     要获取数据的页的index
     * @param numberPerPage 每一页的数据条数
     * @return json串
     * <p>
     * json串格式：
     * ["id1", "id2", ...]
     * 其中id1, id2, ...是在每页数据有numberPerPage条的情况下，index
     * 为pageIndex的页的用户Id为userId的用户关注的人的用户Id列表
     */
    @RequestMapping("/pagedFollowingUserIdList")
    public String getPagedFollowingUserIdList(String userId, int pageIndex, int numberPerPage) {
        List<String> list = followingUsersService.getFollowingUserIdList(userId, pageIndex, numberPerPage);
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * 查找某用户粉丝的用户Id列表
     *
     * @param userId 要查找的用户的用户Id
     * @return json串
     * <p>
     * json串格式：
     * ["id1", "id2", ...]
     * 其中id1, id2, ...是关注的人的粉丝列表（全部）
     */
    @RequestMapping("/followerUserIdList")
    public String getFollowerUserIdList(String userId) {
        List<String> list = followerUsersService.getFollowerUserIdList(userId, 0, DEFAULT_NUMBER_PER_PAGE);
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * 查找某用户粉丝的用户Id列表（以分页形式）
     *
     * @param userId        要查找的用户的用户Id
     * @param pageIndex     要获取数据的页的index
     * @param numberPerPage 每一页的数据条数
     * @return json串
     * <p>
     * json串格式：
     * ["id1", "id2", ...]
     * 其中id1, id2, ...是在每页数据有numberPerPage条的情况下，index
     * 为pageIndex的页的用户Id为userId的用户粉丝的用户Id列表
     */
    @RequestMapping("/pagedFollowerUserIdList")
    public String getPagedFollowerUserIdList(String userId, int pageIndex, int numberPerPage) {
        List<String> list = followerUsersService.getFollowerUserIdList(userId, pageIndex, numberPerPage);
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * 获取用户粉丝数量
     *
     * @param userId 要获取的用户的用户Id
     * @return json串
     * <p>
     * json串格式：
     * [number]
     * 其中number是用户粉丝数，数值型
     */
    @RequestMapping("/followerNum")
    public String getFollowerNum(String userId) {
        Gson gson = new Gson();
        int[] array = {followerNumberService.getFollowerNumber(userId)};
        return gson.toJson(array);
    }

    /**
     * 获取用户关注的人数量
     *
     * @param userId 要获取的用户的用户Id
     * @return json串
     * <p>
     * json串格式：
     * [number]
     * 其中number是用户关注的人数，数值型
     */
    @RequestMapping("/followingNum")
    public String getFollowingNum(String userId) {
        Gson gson = new Gson();
        int[] array = {followingNumberService.getFollowingNumber(userId)};
        return gson.toJson(array);
    }

    /**
     * 注销
     *
     * @param userId 要注销的userId
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/logout")
    public String logout(String userId) {
        if (logoutService.logout(userId)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 获取用户个人信息
     *
     * @param userId 要获取的用户Id
     * @return json串
     * <p>
     * json串格式：
     * ["name", "age", "birthday", "education", "email", "introduction", "lastWeiboId", "location", "phone", "qq", "sex"]
     */
    @RequestMapping("/userProfile")
    public String getUserProfile(String userId) {
        User user = userProfileService.getUserProfile(userId);
        String[] list = new String[11];
        list[0] = user.getName();
        list[1] = user.getAge();
        list[2] = user.getBirthday();
        list[3] = user.getEducation();
        list[4] = user.getEmail();
        list[5] = user.getIntroduction();
        list[6] = user.getLastWeiboId();
        list[7] = user.getLocation();
        list[8] = user.getPhone();
        list[9] = user.getQq();
        list[10] = user.getSex();
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * 更新用户Name
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserName")
    public String updateUserName(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 更新用户Introduction
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserIntroduction")
    public String updateUserIntroduction(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 更新用户PhoneNumber
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserPhoneNumber")
    public String updateUserPhoneNumber(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 更新用户Sex
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserSex")
    public String updateUserSex(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 更新用户Age
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserAge")
    public String updateUserAge(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 更新用户Birthday
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserBirthday")
    public String updateUserBirthday(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 更新用户Location
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserLocation")
    public String updateUserLocation(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 更新用户Email
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserEmail")
    public String updateUserEmail(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 更新用户QQ
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserQQ")
    public String updateUserQQ(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 更新用户Education
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserEducation")
    public String updateUserEducation(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 更新用户LastWeiboId
     *
     * @param userId 要更新的用户Id
     * @param str    新值
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/updateUserLastWeiboId")
    public String updateUserLastWeiboId(String userId, String str) {
        if (userProfileService.updateUserName(userId, str)) {
            return "[\"1\"]";
        } else {
            return "[\"0\"]";
        }
    }

    /**
     * 获取用户头像列表
     *
     * @param userId        用户Id
     * @param pageIndex     页码索引
     * @param numberPerPage 每页的记录数
     * @return json串
     * <p>
     * json串格式：
     * ["value1", "value2", ... ]
     */
    @RequestMapping("/userAvatarList")
    public String getUserAvatarList(String userId, int pageIndex, int numberPerPage) {
        List<Picture> list = userAvatarsService.getUserAvatarList(userId, pageIndex, numberPerPage);
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * 更新用户头像
     *
     * @param userId 要更新的用户Id
     * @param file   上传的头像文件
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 更新成功；0 - 更新失败
     */
    @RequestMapping("/uploadUserAvatar")
    public String uploadUserAvatar(String userId, @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty())
            if (userAvatarsService.uploadUserAvatar(userId, file))
                return "[\"1\"]";
        return "[\"0\"]";
    }

    /**
     * 生成格式为：
     * {
     * "status": "success",
     * "tipCode": 200,
     * "tipMsg": "ok",
     * "data": "value"
     * }
     * 的json串，替换value的值，登录注册用
     *
     * @param value 要替换的value值
     * @return 生成的json串
     */
    private String wrapLoginOrRegisterWithValue(String value) {
        return "{\n" +
                "    \"status\": \"success\",\n" +
                "    \"tipCode\": 200,\n" +
                "    \"tipMsg\": \"ok\",\n" +
                "    \"data\": \"" + value + "\"\n" +
                "}";
    }

    /**
     * 根据用户名查找用户Id
     *
     * @param username 用户名
     * @return 成功：用户Id 失败：-1
     */
    private String getUserIdByUsername(String username) {
        // 如果尚未保存该用户密码信息
        if (!userMap.containsKey(username))
            return "-1";
//        loginService = new LoginServiceImpl();
        return loginService.getLoginUserId(username, userMap.get(username));
    }
}
