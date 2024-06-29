package com.yupi.usercenter.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description 缓存预热任务
 * @Author zhangweibao
 * @Date 2024/6/29 23:42
 * @Version 1.0
 **/
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 0 0 * * ?")
    private void preUserCache() {
        log.info("开始缓存预热");
        String key = String.format("%s:%s:%s", "ally-link", "user", "recommend");
        Page<User> page = userService.page(Page.of(1, 100), null);
        Gson gson = new Gson();
        try {
            stringRedisTemplate.opsForValue().set(key, gson.toJson(page));
        } catch (Exception e) {
            log.error("PreCacheJob[preUserCache]用户缓存预热失败");
        }
    }

}