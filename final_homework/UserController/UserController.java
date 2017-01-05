//===========================================================
//    除Spring外，此文件还需依赖Google的gson-2.3.1.jar
//===========================================================
package cn.edu.bjtu.weibo.controller;

import cn.edu.bjtu.weibo.model.*;
import cn.edu.bjtu.weibo.service.*;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 老师说这是 -> 显示个人信息页面的Controller<br>
 * 我觉得这是 -> 所有用户相关的操作都要我负责 =_=<br>
 * <p/>
 * 目前已实现的功能：<br>
 * 1. 用户登录<br>
 * 2. 用户注册<br>
 * 3. 根据用户名查找用户Id<br>
 * 4. 获取当前用户名/用户Id<br>
 * 5. 获取某一用户关注的人/粉丝列表（分页/不分页）<br>
 * 6. 获取某一用户关注/粉丝数<br>
 * 7. 获取/更新用户个人信息<br>
 * 8. 获取/更新用户头像<br>
 * 9. 查询两个用户之间关注/粉丝关系<br>
 * 10. 某用户被赞列表<br>
 * 11. 他的关注、共同关注、他的兴趣主页、他的粉丝、我关注的人也在关注他<br>
 * 12. 通过token获取用户Id<br>
 * 13. 通过用户Id获取用户名<br>
 * <p/>
 * ==================================<br>
 * 【醒目！！！】关于token<br>
 * ==================================<br>
 * 很多人问我在我的UserController中，token身份验证的问题是怎么处理的，这里我介绍
 * 一下我这面目前的情况。<br>
 * 我们的系统在登录的时候，会向后台传入两个参数：用户名和密码，后台会根据这两个参数
 * 生成一个token，这个token中会包括userId的信息。生成的token会返回前端，并存储到
 * 本地localStorage中。具体存储的方式请 @王雪。关于token的简单介绍参见：<br>
 * https://ninghao.net/blog/2834<br>
 * 在UserController中有很多参数为userId的方法，多数情况下这个userId要填写的是当前
 * 登录用户。理想情况下，也就是如果严格按照老师要求，这些userId参数是应该省略的，而
 * 你们的每次请求要在HTTP请求头中携带token，我的这些方法也会各自配备一个拦截器，把
 * 请求头中的token解析出来，得到其中的userId信息。<br>
 * 但是理想很丰满，现实不太丰满o_o。我们在接到使用token验证的时候我这面一千多行代码
 * 已经基本完工了，也发布了几次文档，很多前端同学的Http请求也按照接口对接好了。所以无
 * 论是我这面还是前端同学们在短期内修改出来应该比较吃力，这里说一下目前的解决方案吧。<br>
 * 从Http请求头中解析token，有这样功能的函数我这面只有一个，就是getCurrentUserId()
 * 方法。前端同学在需要使用userId，即当前登录用户时，请先发送一个Get请求，调用
 * /currentUserId 接口，并在请求头中携带token信息。这个函数会从token中解析出当前
 * 登录用户的userId并返回。前端同学再使用这个userId去调用UserController的其他方法
 * 就可以了。<br>
 * 额多说一句，前端我们的token验证方法是JWT验证，前端的代码不会写的话可以参照：<br>
 * http://bbs.csdn.net/wap/topics/392006333 (注意是不会写的参照这个 =_=，我也不
 * 知道这个对不对）<br>
 * 以上的解决方案没有严格按照老师的要求，也存在很大的安全漏洞（所以大家配合一下 =_=，
 * 别捅娄子）。那么如果后续还有时间的话，我准备把我这面的每一个方法都完善一下，加上拦
 * 截器，作为安全验证的一部分。<br>
 * 额我再多说一下…其实由于我现在被username, name, userId这几个属性实在绕晕了，而且
 * service和dao缺少一些方法，getCurrentUserId()这个方法我还没有实现…（我还打着TODO
 * 标签呢o_o）不过大家放心按照上面写…接口和流程是不会变的。这些问题我需要在这两天联系
 * 老师和助教（估计又要自己写service了），再加上拦截器这块我还没有彻底搞明白…所以上面
 * 说的“安全验证”在答辩前多半不会出现了，大家应该不用担心还要改代码了…而且这次的接口
 * 应该可以做到以不变应万变吧，如果需求再变动的话，我这面改一改应该就可以了，应该扛得
 * 住……吧，反正我尽量不动接口。<br>
 * 经过这些天的工作说好的几十行的controller已经过千行了……虽然一大半是注释哈哈哈。<br>
 * 废话有点多 =_=，大家新年快乐，加油加油。<br>
 *
 * @author 王国桥
 */
@RestController
@RequestMapping("/u")
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
    @Deprecated
    private static Map<String, String> userMap = new HashMap<>();
//    /**
//     * 保存当前登录的用户名
//     */
//    private static String currentUser;

    // =========================================
    //     诸多Service
    // =========================================
//    @Autowired
//    private LoginService loginService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private LogoutService logoutService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserAvatarsService userAvatarsService;
    @Autowired
    private FollowerUsersService followerUsersService;
    @Autowired
    private FollowingUsersService followingUsersService;
    @Autowired
    private FollowerNumberService followerNumberService;
    @Autowired
    private FollowingNumberService followingNumberService;
    @Autowired
    private LikedService likedService;
    @Autowired
    private WeiboPictureServie weiboPictureServie;
    @Autowired
    private FavoriteWeiboService favoriteWeiboService;

    /**
     * 用户登录
     * <p>
     * 请使用LoginController中的login方法
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
    @Deprecated
//    @RequestMapping("/login")
    public String login(String name, String password) {
//        loginService = new LoginServiceImpl();
        // 判断用户是否存在
//        if (!loginService.isUserExisted(name, password)) {
//            return wrapLoginOrRegisterWithValue("1");
//        }
        // 用户存在，保存信息至userMap
        userMap.put(name, password);
        // 记录当前登录用户
//        currentUser = name;
        return wrapLoginOrRegisterWithValue("/mainPage.html");
    }

    /**
     * 用户注册
     * <p>
     * 请使用LoginController中的register方法
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
    @Deprecated
//    @RequestMapping("/signin")
    public String signin(String name, String password) {
        // 失败返回的字符串
        String failedStr = wrapLoginOrRegisterWithValue("0");
//        registerService = new RegisterServiceImpl();
        // 判断用户名是否被占用
        if (registerService.isUserNameExisted(name))
            return failedStr;
        // 开始注册
        boolean registerSuccess;
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername(name);
        loginUser.setPassword(password);
        registerSuccess = registerService.registerNewUser(loginUser);
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
//            userProfileService.createNewUser(user);
            return wrapLoginOrRegisterWithValue("1");
        }
        return failedStr;
    }

    /**
     * 获取当前登录用户的用户名（见getCurrentUserId()方法）
     *
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value是当前登录用户的用户名
     */
    @Deprecated
//    @RequestMapping(value = "/currentUserName", method = RequestMethod.GET)
    public String getCurrentUserName() {
//        return "[\"" + currentUser + "\"]";
        return null;
    }

    /**
     * 获取当前登录用户的用户Id
     * <p>
     * 我们使用的是token来验证用户身份，这里封装了通过token
     * 获取当前用户Id的方法，方便需要的人使用。注意，这个函数
     * 不需要携带参数，但是Http请求的请求头中要放入
     *
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value是当前登录用户的用户Id
     */
    @RequestMapping(value = "/currentUserId", method = RequestMethod.GET)
    public String getCurrentUserId() {
//        return "[\"" + getUserIdByUsername(currentUser) + "\"]";
        // TODO: 2016/12/30 使用token的方法获取
        return null;
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
    @RequestMapping(value = "/followingUserIdList", method = RequestMethod.GET)
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
    @RequestMapping(value = "/pagedFollowingUserIdList", method = RequestMethod.GET)
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
    @RequestMapping(value = "/followerUserIdList", method = RequestMethod.GET)
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
    @RequestMapping(value = "/pagedFollowerUserIdList", method = RequestMethod.GET)
    public String getPagedFollowerUserIdList(String userId, int pageIndex, int numberPerPage) {
        List<String> list = followerUsersService.getFollowerUserIdList(userId, pageIndex, numberPerPage);
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * 获取两个用户A和B之间关系
     *
     * @param userAId 用户A的userId
     * @param userBId 用户B的userId
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：0 - A和B没有任何关系；1 - A关注B（B是A的粉丝）；2 - B关注A（A是B的粉丝）
     */
    @RequestMapping(value = "/relationOfTwoUsers", method = RequestMethod.GET)
    public String getRelationOfTwoUsers(String userAId, String userBId) {
        // 用户A关注的用户列表
        List<String> listFollowing = followingUsersService.getFollowingUserIdList(userAId, 0, DEFAULT_NUMBER_PER_PAGE);
        // 用户A的粉丝列表
        List<String> listFollowed = followerUsersService.getFollowerUserIdList(userAId, 0, DEFAULT_NUMBER_PER_PAGE);
        if (listFollowing.contains(userBId))
            return "[\"1\"]";
        else if (listFollowed.contains(userBId))
            return "[\"2\"]";
        else
            return "[\"0\"]";
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
    @RequestMapping(value = "/followerNum", method = RequestMethod.GET)
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
    @RequestMapping(value = "/followingNum", method = RequestMethod.GET)
    public String getFollowingNum(String userId) {
        Gson gson = new Gson();
        int[] array = {followingNumberService.getFollowingNumber(userId)};
        return gson.toJson(array);
    }

    /**
     * 获取某一用户被赞的列表
     * （1. 时间；2. 点赞的用户；3. 微博图片；4. 微博内容）
     * 本来想获取上面四项内容，但是4微博内容暂时没找到方法获取；跳转链接暂时没
     * 找到负责的前端队友，现在先显示数据吧，以后找到链接再补上
     *
     * @param userId 要获取的用户的用户Id
     * @return json串
     * <p>
     * json串格式：
     * [
     * {
     * "time": time1,
     * "name": name1,
     * "picture": picture1
     * },
     * {
     * "time": time2,
     * "name": name2,
     * "picture": picture2
     * },
     * ...
     * ]
     * 其中：
     * time - 点赞时间
     * name - 点赞用户姓名
     * picture - 微博图片地址（指定为image标签的src属性即可）
     */
    @RequestMapping(value = "/userLikedList", method = RequestMethod.GET)
    public String getUserLikedList(String userId) {
        List<String> resultList = new ArrayList<>();
        List<Like> likeList = likedService.getAll(userId);
        String rTime, rName, rPic, rContent;
        for (Like l : likeList) {
            // 点赞时间
            rTime = l.time;
            // 点赞用户姓名
            rName = userProfileService.getUserProfile(l.userActionId).getName();
            // 微博图片和微博内容
            String weiboId = l.WeiboIdOrCommentId;
            rPic = weiboPictureServie.getWeiboPictureList(weiboId).get(0).getPicurl();
            resultList.add(new oneLikedContentNew(rTime, rName, rPic).toString());
        }
        Gson gson = new Gson();
        return gson.toJson(resultList);
    }

    /**
     * 单条记录，获取用户被赞列表时用，并提供了toString()方法转换为json串
     */
    private class oneLikedContentNew {
        private String time;
        private String name;
        private String picture;

        private oneLikedContentNew(String time, String name, String picture) {
            this.time = time;
            this.name = name;
            this.picture = picture;
        }

        @Override
        public String toString() {
            return "{ \"time\": " + time + ", \"name\": " + name + ", \"picture\": " + picture + " }";
        }
    }

    @Deprecated
    private class oneLikedContent {
        public String name;
        public String homepageHref;
        public String likedWeiboHref;
        public String likedTime;

        /**
         * 构造器
         *
         * @param name           点赞用户姓名
         * @param homepageHref   点赞用户的主页链接
         * @param likedWeiboHref 被点赞的微博链接
         * @param likedTime      点赞时间
         */
        public oneLikedContent(String name, String homepageHref, String likedWeiboHref, String likedTime) {
            this.name = name;
            this.homepageHref = homepageHref;
            this.likedWeiboHref = likedWeiboHref;
            this.likedTime = likedTime;
        }
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
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(String userId) {
        return logoutService.logout(userId) ? "[\"1\"]" : "[\"0\"]";
    }

    /**
     * 由用户Id获取用户名
     *
     * @param userId 要获取的用户的用户Id
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value表示用户名
     */
    @RequestMapping(value = "/userNameById", method = RequestMethod.GET)
    public String getUserNameById(String userId) {
        User user = userProfileService.getUserProfile(userId);
        return user.getName();
    }

    /**
     * 获取用户个人信息
     *
     * @param userId 要获取的用户Id
     * @return json串
     * <p>
     * json串格式：
     * {
     * "name": "n1",
     * "age": "a1",
     * "birthday": [
     * "yyyy",
     * "mm",
     * "dd"
     * ],
     * "education": "e1",
     * "email": "e2",
     * "introduction": "i1",
     * "lastWeiboId": "l1",
     * "location": [
     * "l1",
     * "l2",
     * "l3"
     * ],
     * "phone": "p1",
     * "qq": "q1",
     * "sex": "s1"
     * }
     */
    @RequestMapping(value = "/userProfile", method = RequestMethod.GET)
    public String getUserProfile(String userId) {
        User user = userProfileService.getUserProfile(userId);
        String name = user.getName();
        String age = user.getAge();
        String[] bir = user.getBirthday().split("-");
        String edu = user.getEducation();
        String email = user.getEmail();
        String intro = user.getIntroduction();
        String last = user.getLastWeiboId();
        String[] location = user.getLocation().split("-");
        String phone = user.getPhone();
        String qq = user.getQq();
        String sex = user.getSex();
        return "{\n" +
                "    \"name\": \"" + name + "\",\n" +
                "    \"age\": \"" + age + "\",\n" +
                "    \"birthday\": [\n" +
                "        \"" + bir[0] + "\",\n" +
                "        \"" + bir[1] + ",\n" +
                "        \"" + bir[2] + "\"\n" +
                "    ],\n" +
                "    \"education\": \"" + edu + "\",\n" +
                "    \"email\": \"" + email + "\",\n" +
                "    \"introduction\": \"" + intro + "\",\n" +
                "    \"lastWeiboId\": \"" + last + "\",\n" +
                "    \"location\": [\n" +
                "        \"" + location[0] + "\",\n" +
                "        \"" + location[1] + "\",\n" +
                "        \"" + location[2] + "\"\n" +
                "    ],\n" +
                "    \"phone\": \"" + phone + "\",\n" +
                "    \"qq\": \"" + qq + "\",\n" +
                "    \"sex\": \"" + sex + "\"\n" +
                "}";
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
    @RequestMapping(value = "/updateUserName", method = RequestMethod.GET)
    public String updateUserName(String userId, String str) {
        return userProfileService.updateUserName(userId, str) ? "[\"1\"]" : "[\"0\"]";
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
    @RequestMapping(value = "/updateUserIntroduction", method = RequestMethod.GET)
    public String updateUserIntroduction(String userId, String str) {
        return userProfileService.updateUserIntroduction(userId, str) ? "[\"1\"]" : "[\"0\"]";
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
    @RequestMapping(value = "/updateUserPhoneNumber", method = RequestMethod.GET)
    public String updateUserPhoneNumber(String userId, String str) {
        return userProfileService.updateUserPhoneNumber(userId, str) ? "[\"1\"]" : "[\"0\"]";
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
    @RequestMapping(value = "/updateUserSex", method = RequestMethod.GET)
    public String updateUserSex(String userId, String str) {
        return userProfileService.updateUserSex(userId, str) ? "[\"1\"]" : "[\"0\"]";
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
    @RequestMapping(value = "/updateUserAge", method = RequestMethod.GET)
    public String updateUserAge(String userId, String str) {
        return userProfileService.updateUserAge(userId, str) ? "[\"1\"]" : "[\"0\"]";
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
    @RequestMapping(value = "/updateUserBirthday", method = RequestMethod.GET)
    public String updateUserBirthday(String userId, String str) {
        return userProfileService.updateUserBirthday(userId, str) ? "[\"1\"]" : "[\"0\"]";
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
    @RequestMapping(value = "/updateUserLocation", method = RequestMethod.GET)
    public String updateUserLocation(String userId, String str) {
        return userProfileService.updateUserLocation(userId, str) ? "[\"1\"]" : "[\"0\"]";
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
    @RequestMapping(value = "/updateUserEmail", method = RequestMethod.GET)
    public String updateUserEmail(String userId, String str) {
        return userProfileService.updateUserEmail(userId, str) ? "[\"1\"]" : "[\"0\"]";
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
    @RequestMapping(value = "/updateUserQQ", method = RequestMethod.GET)
    public String updateUserQQ(String userId, String str) {
        return userProfileService.updateUserQQ(userId, str) ? "[\"1\"]" : "[\"0\"]";
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
    @RequestMapping(value = "/updateUserEducation", method = RequestMethod.GET)
    public String updateUserEducation(String userId, String str) {
        return userProfileService.updateUserEducation(userId, str) ? "[\"1\"]" : "[\"0\"]";
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
    @RequestMapping(value = "/updateUserLastWeiboId", method = RequestMethod.GET)
    public String updateUserLastWeiboId(String userId, String str) {
        return userProfileService.updateUserLastWeiboId(userId, str) ? "[\"1\"]" : "[\"0\"]";
    }

    /*{
    "name": "name1",
    "age": "age1",
    "birthday": "bir1",
    "education": "edu1",
    "email": "email1",
    "introduction": "intro1",
    "lastWeiboId": "last1",
    "location": "loca1",
    "phone": "phone1",
    "qq": "qq1",
    "sex": "sex1"
    }*/

    /**
     * 更新用户全部信息
     *
     * @param userId       要更新的用户Id
     * @param name         新的name
     * @param age          新的age
     * @param birthday     新的birthday（格式为 yyyy-mm-dd）
     * @param education    新的education
     * @param email        新的email
     * @param introduction 新的introduction
     * @param lastWeiboId  新的lastWeiboId
     * @param location     新的location（格式为 l1-l2-l3）
     * @param phone        新的phone
     * @param qq           新的qq
     * @param sex          新的sex
     * @return json串
     * <p>
     * json串格式：
     * ["value"]
     * 其中value：1 - 成功；0 - 失败
     */
    @RequestMapping(value = "/userProfile", method = RequestMethod.POST)
    public String updateUserProfile(String userId, String name, String age, String birthday, String education, String email, String introduction, String lastWeiboId, String location, String phone, String qq, String sex) {
        String failStr = "[\"0\"]";
        String successStr = "[\"1\"]";
        if (!userProfileService.updateUserName(userId, name)) return failStr;
        if (!userProfileService.updateUserAge(userId, age)) return failStr;
        if (!userProfileService.updateUserBirthday(userId, birthday)) return failStr;
        if (!userProfileService.updateUserEducation(userId, education)) return failStr;
        if (!userProfileService.updateUserEmail(userId, email)) return failStr;
        if (!userProfileService.updateUserIntroduction(userId, introduction)) return failStr;
        if (!userProfileService.updateUserLastWeiboId(userId, lastWeiboId)) return failStr;
        if (!userProfileService.updateUserLocation(userId, location)) return failStr;
        if (!userProfileService.updateUserPhoneNumber(userId, phone)) return failStr;
        if (!userProfileService.updateUserQQ(userId, qq)) return failStr;
        if (!userProfileService.updateUserSex(userId, sex)) return failStr;
        return successStr;
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
    @RequestMapping(value = "/userAvatarList", method = RequestMethod.GET)
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
    @RequestMapping(value = "/uploadUserAvatar", method = RequestMethod.GET)
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
    @Deprecated
    private String getUserIdByUsername(String username) {
        // 如果尚未保存该用户密码信息
        if (!userMap.containsKey(username))
            return "-1";
//        loginService = new LoginServiceImpl();
//        return loginService.getLoginUserId(username, userMap.get(username));
        return "-1";
    }

    /**
     * 他的关注
     *
     * @param MUserId “我”的userId
     * @param HUserId “他”的userId
     * @return json串
     * <p>
     * json串格式：
     * [
     * {
     * "avatar": "avatar1",
     * "name": "name1",
     * "addr": "addr1",
     * "followingNum": "followingNum1",
     * "followedNum": "followedNum1",
     * "isIFollowHim": "isFollow1"
     * },
     * {
     * "avatar": "avatar2",
     * "name": "name2",
     * "addr": "addr2",
     * "followingNum": "followingNum2",
     * "followedNum": "followedNum2",
     * "isIFollowHim": "isFollow2"
     * },
     * ...
     * ]
     * 其中：<br>
     * avatar - 字符型 - 用户头像的缩略图url，指定为image标签的src属性即可<br>
     * name - 字符型 - 用户名<br>
     * addr - 字符型 - 地址<br>
     * followingNum - 字符型 - 关注的人数量<br>
     * followedNum - 字符型 - 粉丝数量<br>
     * isIFollowHim - 字符型 - 1 表示“我”关注他，否则为 0<br>
     */
    @RequestMapping(value = "/HConcerns", method = RequestMethod.GET)
    public String getHConcerns(String MUserId, String HUserId) {
        List<User> userList = followingUsersService.getFollowingUserList(HUserId, 0, DEFAULT_NUMBER_PER_PAGE);
        List<String> userIdList = followingUsersService.getFollowingUserIdList(HUserId, 0, DEFAULT_NUMBER_PER_PAGE);
        List<String> resultList = generatePersonResult(MUserId, HUserId, userIdList, userList);
        Gson gson = new Gson();
        return gson.toJson(resultList);
    }

    /**
     * 共同关注
     *
     * @param MUserId “我”的userId
     * @param HUserId “他”的userId
     * @return json串
     * <p>
     * json串格式：
     * [
     * {
     * "avatar": "avatar1",
     * "name": "name1",
     * "addr": "addr1",
     * "followingNum": "followingNum1",
     * "followedNum": "followedNum1",
     * "isIFollowHim": "isFollow1"
     * },
     * {
     * "avatar": "avatar2",
     * "name": "name2",
     * "addr": "addr2",
     * "followingNum": "followingNum2",
     * "followedNum": "followedNum2",
     * "isIFollowHim": "isFollow2"
     * },
     * ...
     * ]
     * 其中：<br>
     * avatar - 字符型 - 用户头像的缩略图url，指定为image标签的src属性即可<br>
     * name - 字符型 - 用户名<br>
     * addr - 字符型 - 地址<br>
     * followingNum - 字符型 - 关注的人数量<br>
     * followedNum - 字符型 - 粉丝数量<br>
     * isIFollowHim - 字符型 - 1 表示“我”关注他，否则为 0<br>
     */
    @RequestMapping(value = "/CConcerns", method = RequestMethod.GET)
    public String getCConcerns(String MUserId, String HUserId) {
        List<User> myFollowingList = followingUsersService.getFollowingUserList(MUserId, 0, DEFAULT_NUMBER_PER_PAGE);
        List<String> myFollowingIdList = followingUsersService.getFollowingUserIdList(MUserId, 0, DEFAULT_NUMBER_PER_PAGE);
        List<String> hisFollowingIdList = followingUsersService.getFollowingUserIdList(HUserId, 0, DEFAULT_NUMBER_PER_PAGE);
        // 比对用户Id，查找共同关注
        int i = 0;
        List<Integer> rList = new ArrayList<>();
        outLoop:
        for (String s1 : myFollowingIdList) {
            for (String s2 : hisFollowingIdList)
                if (s1 == s2) {
                    rList.add(i);
                    i++;
                    continue outLoop;
                }
            i++;
        }
        // 获取用户Id结果列表
        List<String> resultIdList = new ArrayList<>();
        for (int i1 : rList)
            resultIdList.add(myFollowingIdList.get(i1));
        // 获取用户信息列表
        List<User> resultUserList = new ArrayList<>();
        for (int i1 : rList)
            resultUserList.add(myFollowingList.get(i1));
        // 生成结果
        List<String> resultList = new ArrayList<>();
        Gson gson = new Gson();
        return gson.toJson(resultList);
    }

    /**
     * 他的兴趣主页
     * 【注意！】这个方法和其他四个方法不同，返回的是“他”感
     * 兴趣的微博列表 @石文文
     *
     * @param HUserId “他”的userId
     * @return json串
     * <p>
     * json串格式：
     * [
     * {
     * "content": "content1",
     * "userid": "userid1",
     * "date": "date1"
     * },
     * {
     * "content": "content2",
     * "userid": "userid2",
     * "date": "date2"
     * },
     * ...
     * ]
     */
    @RequestMapping(value = "/Intersts", method = RequestMethod.GET)
    public String getIntersts(String HUserId) {
        List<Weibo> weiboList = favoriteWeiboService.getFavoriteWeiboList(HUserId, 0, DEFAULT_NUMBER_PER_PAGE);
        List<String> resultList = new ArrayList<>();
        for (Weibo w : weiboList)
            resultList.add(new OneWeibo(w.getContent(), w.getUserId(), w.getDate()).toString());
        Gson gson = new Gson();
        return gson.toJson(resultList);
    }

    private class OneWeibo {
        private String content;
        private String userId;
        private String date;

        /**
         * 构造器
         *
         * @param content 微博内容
         * @param userId  用户Id
         * @param date    微博时间
         */
        private OneWeibo(String content, String userId, String date) {
            this.content = content;
            this.userId = userId;
            this.date = date;
        }

        @Override
        public String toString() {
            return "{\n" +
                    "    \"content\": \"" + content + "\",\n" +
                    "    \"userid\": \"" + userId + "\",\n" +
                    "    \"date\": \"" + date + "\"\n" +
                    "}";
        }
    }

    /**
     * 他的粉丝
     *
     * @param MUserId “我”的userId
     * @param HUserId “他”的userId
     * @return json串
     * <p>
     * json串格式：
     * [
     * {
     * "avatar": "avatar1",
     * "name": "name1",
     * "addr": "addr1",
     * "followingNum": "followingNum1",
     * "followedNum": "followedNum1",
     * "isIFollowHim": "isFollow1"
     * },
     * {
     * "avatar": "avatar2",
     * "name": "name2",
     * "addr": "addr2",
     * "followingNum": "followingNum2",
     * "followedNum": "followedNum2",
     * "isIFollowHim": "isFollow2"
     * },
     * ...
     * ]
     * 其中：<br>
     * avatar - 字符型 - 用户头像的缩略图url，指定为image标签的src属性即可<br>
     * name - 字符型 - 用户名<br>
     * addr - 字符型 - 地址<br>
     * followingNum - 字符型 - 关注的人数量<br>
     * followedNum - 字符型 - 粉丝数量<br>
     * isIFollowHim - 字符型 - 1 表示“我”关注他，否则为 0<br>
     */
    @RequestMapping(value = "/HFans", method = RequestMethod.GET)
    public String getHFans(String MUserId, String HUserId) {
        List<String> idList = followerUsersService.getFollowerUserIdList(HUserId, 0, DEFAULT_NUMBER_PER_PAGE);
        List<User> userList = followerUsersService.getFollowerUserList(HUserId, 0, DEFAULT_NUMBER_PER_PAGE);
        List<String> resultList = generatePersonResult(MUserId, HUserId, idList, userList);
        Gson gson = new Gson();
        return gson.toJson(resultList);
    }

    /**
     * 我关注的人也在关注他
     *
     * @param MUserId “我”的userId
     * @param HUserId “他”的userId
     * @return json串
     * <p>
     * json串格式：
     * [
     * {
     * "avatar": "avatar1",
     * "name": "name1",
     * "addr": "addr1",
     * "followingNum": "followingNum1",
     * "followedNum": "followedNum1",
     * "isIFollowHim": "isFollow1"
     * },
     * {
     * "avatar": "avatar2",
     * "name": "name2",
     * "addr": "addr2",
     * "followingNum": "followingNum2",
     * "followedNum": "followedNum2",
     * "isIFollowHim": "isFollow2"
     * },
     * ...
     * ]
     * 其中：<br>
     * avatar - 字符型 - 用户头像的缩略图url，指定为image标签的src属性即可<br>
     * name - 字符型 - 用户名<br>
     * addr - 字符型 - 地址<br>
     * followingNum - 字符型 - 关注的人数量<br>
     * followedNum - 字符型 - 粉丝数量<br>
     * isIFollowHim - 字符型 - 1 表示“我”关注他，否则为 0<br>
     */
    @RequestMapping(value = "/Gallery", method = RequestMethod.GET)
    public String getGallery(String MUserId, String HUserId) {
        // 获取“我”关注的人
        List<String> idList = followingUsersService.getFollowingUserIdList(MUserId, 0, DEFAULT_NUMBER_PER_PAGE);
        List<User> userList = followingUsersService.getFollowingUserList(MUserId, 0, DEFAULT_NUMBER_PER_PAGE);
        // 获取“我”关注的人中，关注“他”的人
        List<Integer> indexList = new ArrayList<>();
        int i = 0;
        for (String id : idList) {
            List<String> idList1 = followingUsersService.getFollowingUserIdList(id, 0, DEFAULT_NUMBER_PER_PAGE);
            if (idList1.contains(HUserId))
                indexList.add(i);
            i++;
        }
        List<String> rIdList = new ArrayList<>();
        List<User> rUserList = new ArrayList<>();
        for (int i1 : indexList) {
            rIdList.add(idList.get(i1));
            rUserList.add(userList.get(i1));
        }
        List<String> resultList = new ArrayList<>();
        resultList = generatePersonResult(MUserId, HUserId, rIdList, rUserList);
        Gson gson = new Gson();
        return gson.toJson(resultList);
    }

    /**
     * 生成person列表
     *
     * @param MUserId  “我”的UserId
     * @param HUserId  “他”的UserId
     * @param idList   要统计的用户Id列表
     * @param userList 要统计的用户信息列表
     * @return person信息列表，单挑信息为person转换为字符串后的结果
     */
    private List<String> generatePersonResult(String MUserId, String HUserId, List<String> idList, List<User> userList) {
        Iterator<String> idIter = idList.iterator();
        List<String> resultList = new ArrayList<>();
        for (User u : userList) {
            String currentUserId = idIter.next();
            List<Picture> pictureList = userAvatarsService.getUserAvatarList(currentUserId, 0, DEFAULT_NUMBER_PER_PAGE);
            String avatar = pictureList.get(0).getPicurl();
            String name = u.getName();
            String addr = u.getLocation();
            String followingNum = getFollowingNum(currentUserId);
            String followedNum = getFollowerNum(currentUserId);
            List<String> followerIdList = followerUsersService.getFollowerUserIdList(HUserId, 0, DEFAULT_NUMBER_PER_PAGE);
            String isIFollowHim = idList.contains(MUserId) ? "1" : "0";
            OnePerson person = new OnePerson(avatar, name, addr, followingNum, followedNum, isIFollowHim);
            resultList.add(person.toString());
        }
        return resultList;
    }

    /**
     * 单个人的记录，用于他的关注、共同关注、他的兴趣主页、他的粉丝、
     * 我关注的人也在关注他五个方法
     */
    private class OnePerson {
        private String avatar;
        private String name;
        private String addr;
        private String followingNum;
        private String followedNum;
        private String isIFollowHim;

        /**
         * 构造器
         *
         * @param avatar       用户头像的缩略图url
         * @param name         用户名
         * @param addr         地址
         * @param followingNum 关注的人数量
         * @param followedNum  粉丝数量
         * @param isIFollowHim “我”是否关注“他”
         */
        private OnePerson(String avatar, String name, String addr, String followingNum, String followedNum, String isIFollowHim) {
            this.avatar = avatar;
            this.name = name;
            this.addr = addr;
            this.followingNum = followingNum;
            this.followedNum = followedNum;
            this.isIFollowHim = isIFollowHim;
        }

        @Override
        public String toString() {
            return "{\n" +
                    "    \"avatar\": \"" + avatar + "\",\n" +
                    "    \"name\": \"" + name + "\",\n" +
                    "    \"addr\": \"" + addr + "\",\n" +
                    "    \"followingNum\": \"" + followingNum + "\",\n" +
                    "    \"followedNum\": \"" + followedNum + "\",\n" +
                    "    \"isIFollowHim\": \"" + isIFollowHim + "\"\n" +
                    "}";
        }
    }
}
