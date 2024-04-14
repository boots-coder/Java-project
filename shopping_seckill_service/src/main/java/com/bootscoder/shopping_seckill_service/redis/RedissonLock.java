package com.bootscoder.shopping_seckill_service.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLock {
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 加锁
     * @param key 锁的名字，也就是redis中的键
     * @param expireTime 键的过期时间，也就是锁的过期时间
     * @return 加锁结果
     */
    public boolean lock(String key,long expireTime){
        // 拿到锁的名字，redis的key，即锁的名字为秒杀商品的商品id
        RLock lock = redissonClient.getLock("lock:" + key);
        try {
            /**
             * 尝试获取锁，如果别人没有占用这把锁，则拿到锁
             * 参数代表redis过期时间，即如果没有主动释放锁，到期会自动释放，避免服务器出现问题后引发死锁。
             */
            return lock.tryLock(expireTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            /**
             * 没有拿到锁，证明别人占用，中断当前线程
             */
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // 释放锁
    public void unlock(String key){
        // 拿到锁的名字，redis的key，即锁的名字为秒杀商品的商品id
        RLock lock = redissonClient.getLock("lock:" + key);
        // 如果锁被占用，释放锁
        if (lock.isLocked()){
            lock.unlock();
        }
    }

}
