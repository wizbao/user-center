package com.yupi.usercenter;

import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * 启动类测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class UserCenterApplicationTests {

    // https://yupi.icu/

    @Test
    void testDigest() throws NoSuchAlgorithmException {
        String newPassword = DigestUtils.md5DigestAsHex(("abcd" + "mypassword").getBytes());
        System.out.println(newPassword);
    }

    @Resource
    private UserService userService;

    @Test
    void testTags() {
        List<User> users = userService.searchUserByTags(Arrays.asList("java", "开发"));
        System.out.println(users);
        Assertions.assertNotEquals(users.size(), 0);
    }

    @Test
    void testString() {
        List<String> strList = Arrays.asList("a", "b");
        System.out.println(strList);
        Object[] array = strList.toArray();
        System.out.println(array);
        String[] array1 = strList.toArray(new String[]{});
        System.out.println(Arrays.toString(array1));
    }

    /**
     * 批量插入用户
     */
    @Test
    void testLoadUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int insertNum = 100000;
        List<User> users = new ArrayList<>();
        for (int i = 0; i < insertNum; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("xxx");
            user.setAvatarUrl("xxx");
            user.setProfile("xxx");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("3243244543");
            user.setEmail("237489037@gmail.com");
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("xxx");
            users.add(user);
        }
        userService.saveBatch(users, 10000);
        stopWatch.stop();
        System.out.println("批量插入" + insertNum + "条用户数据耗时：" + stopWatch.getTotalTimeMillis() + "毫秒"); // 批量插入100000条用户数据耗时：24020毫秒
    }

    private ExecutorService executorService = new ThreadPoolExecutor(100,1000,1000, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10000));

    /**
     * 并发批量插入用户
     */
    @Test
    void testConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int batchSize = 5000;
        int loop = 100;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            List<User> users = new ArrayList<>();
            for (int j = 0; j < batchSize; j++) {
                User user = new User();
                user.setUsername("假用户");
                user.setUserAccount("xxx");
                user.setAvatarUrl("xxx");
                user.setProfile("xxx");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("3243244543");
                user.setEmail("237489037@gmail.com");
                user.setTags("[]");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("xxx");
                users.add(user);
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(users, batchSize);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        stopWatch.stop();
        System.out.println("批量插入" + batchSize * loop + "条用户数据耗时：" + stopWatch.getTotalTimeMillis() + "毫秒");
    }
}

