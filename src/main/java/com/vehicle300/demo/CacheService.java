package com.vehicle300.demo;

import com.sun.istack.internal.NotNull;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 缓存类
 * 在这里配置缓存最大数量和缓存默认过期时间
 *
 * @author : LL
 * @date : 2020/5/27
 */
@Service
public class CacheService {

    /**
     * 定义512大小map，此处不会进行扩容，loadfactor设置为1
     */
    private static final ConcurrentHashMap<String, Map<String, Object>> cacheMap = new ConcurrentHashMap<>(2 ^ 9, 1);

    /**
     * 最大缓存容量
     */
    private static final int MAX_CACHE_SIZE = 500;
    /**
     * 默认缓存过期时间
     */
    private static final long DEFAULT_TIME_OUT=5*60*1000;

    /**
     * 设置缓存
     *
     * @param key   缓存key
     * @param value 缓存值
     * @param ex    过期时间-ms，传0默认为5分钟
     */
    public void put(@NotBlank String key, @NotNull Object value, long ex) {
        if (ex <= 0) {
            //默认缓存
            ex = DEFAULT_TIME_OUT;
        }
        //尝试清除过期cache
        if (cacheMap.size() >= MAX_CACHE_SIZE) {
            cleanCache();
        }
        if (cacheMap.size() >= MAX_CACHE_SIZE) {
            return;
        }
        Map<String, Object> valueMap = new HashMap<>(4, 1);
        valueMap.put("timestamp", System.currentTimeMillis());
        valueMap.put("value", value);
        valueMap.put("ex", ex);
        cacheMap.put(key, valueMap);
    }

    /**
     * 查询缓存
     *
     * @param key 缓存key
     */
    public Object get(@NotNull String key) {
        if (!cacheMap.containsKey(key)) {
            return null;
        }
        Map<String, Object> cacheValue = cacheMap.get(key);
        //是否过期
        if ((System.currentTimeMillis() - (Long) cacheValue.get("timestamp")) >
                (Long) cacheValue.get("ex")) {
            return null;
        } else {
            return cacheValue.get("value");
        }
    }

    public void cleanCache() {
        System.out.println("开始清除过期key...");
        AtomicInteger count= new AtomicInteger();
        cacheMap.forEach((key, map) -> {
            if ((System.currentTimeMillis() - (Long) map.get("timestamp")) >
                    (Long) map.get("ex")) {
                System.out.println("准备清除key："+key);
                cacheMap.remove(key);
                count.getAndIncrement();
            }
        });
        System.out.println("清除完成，本次共清理:"+count+"个缓存");
    }
}
