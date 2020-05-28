package com.vehicle300.demo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TaskScheduler {

    @Resource
    private CacheService cacheService;

    /**
     * 定时清除过期缓存，方便测试，每10秒执行一次
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void cleanCacheCron(){
        try {
            cacheService.cleanCache();
        } catch (Exception e) {
            //log sth.
        }
    }

}