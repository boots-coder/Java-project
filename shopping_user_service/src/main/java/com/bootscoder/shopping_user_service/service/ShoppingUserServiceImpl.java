//package com.bootscoder.shopping_user_service.service;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.bootscoder.shopping_common.pojo.ShoppingUser;
//import com.bootscoder.shopping_common.result.MyException;
//import com.bootscoder.shopping_common.result.ResultCode;
//import com.bootscoder.shopping_common.service.ShoppingUserService;
//import com.bootscoder.shopping_common.utils.Md5Util;
//import com.bootscoder.shopping_user_service.mapper.ShoppingUserMapper;
//import com.bootscoder.shopping_user_service.util.JwtUtils;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//@DubboService
//public class ShoppingUserServiceImpl implements ShoppingUserService {
//    @Autowired
//    private RedisTemplate redisTemplate;
//    @Autowired
//    private ShoppingUserMapper shoppingUserMapper;
//
//    @Override
//    public void saveRegisterCheckCode(String phone, String checkCode) {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        // redis中保存的建为手机号，值为验证码，过期时间5分钟
//        valueOperations.set("registerCode:"+phone,checkCode,300, TimeUnit.SECONDS);
//    }
//
//
//    @Override
//    public void registerCheckCode(String phone, String checkCode) {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        Object checkCodeRedis = valueOperations.get("registerCode:" + phone);
//        if (!checkCode.equals(checkCodeRedis)){
//            throw new MyException(ResultCode.REGISTER_CODE_ERROR);
//        }
//    }
//
//    @Override
//    public void register(ShoppingUser shoppingUser) {
//        // 1.验证手机号是否存在
//        String phone = shoppingUser.getPhone();
//        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
//        queryWrapper.eq("phone",phone);
//        List<ShoppingUser> shoppingUsers = shoppingUserMapper.selectList(queryWrapper);
//        if (shoppingUsers != null && shoppingUsers.size() > 0){
//            throw new MyException(ResultCode.REGISTER_REPEAT_PHONE_ERROR);
//        }
//        // 2.验证用户名是否存在
//        String username = shoppingUser.getUsername();
//        QueryWrapper<ShoppingUser> queryWrapper1 = new QueryWrapper();
//        queryWrapper1.eq("username",username);
//        List<ShoppingUser> shoppingUsers1 = shoppingUserMapper.selectList(queryWrapper1);
//        if (shoppingUsers1 != null && shoppingUsers1.size() > 0){
//            throw new MyException(ResultCode.REGISTER_REPEAT_NAME_ERROR);
//        }
//        // 3.新增用户
//        shoppingUser.setStatus("Y");
//        shoppingUser.setPassword(Md5Util.encode(shoppingUser.getPassword()));
//        shoppingUserMapper.insert(shoppingUser);
//    }
//
//    @Override
//    public String loginPassword(String username, String password) {
//        // 1.验证用户名
//        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
//        queryWrapper.eq("username",username);
//        ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
//        if (shoppingUser == null){
//            throw new MyException(ResultCode.LOGIN_NAME_PASSWORD_ERROR);
//        }
//        // 2.验证密码
//        boolean verify = Md5Util.verify(password, shoppingUser.getPassword());
//        if (!verify){
//            throw new MyException(ResultCode.LOGIN_NAME_PASSWORD_ERROR);
//        }
//        // 3.生成JWT令牌，返回令牌
//        String sign = JwtUtils.sign(shoppingUser.getId(), shoppingUser.getUsername());
//        return sign;
//    }
//
//    @Override
//    public void saveLoginCheckCode(String phone, String checkCode) {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        // redis的键为手机号，值为验证码，过期时间5分钟
//        valueOperations.set("loginCode:"+phone,checkCode,300,TimeUnit.SECONDS);
//    }
//
//    @Override
//    public String loginCheckCode(String phone, String checkCode) {
//        // 1.验证用户传入的手机号验证码是否在redis中存在
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        Object checkCodeRedis = valueOperations.get("loginCode:" + phone);
//        if (!checkCode.equals(checkCodeRedis)){
//            throw new MyException(ResultCode.LOGIN_CODE_ERROR);
//        }
//        // 2.登录成功，根据手机号查询用户
//        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
//        queryWrapper.eq("phone",phone);
//        ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
//        // 3.生成JWT令牌，返回令牌
//        String sign = JwtUtils.sign(shoppingUser.getId(), shoppingUser.getUsername());
//        return sign;
//    }
//
//    @Override
//    public String getName(String token) {
//        Map<String, Object> verify = JwtUtils.verify(token);
//        String username = (String) verify.get("username");
//        return username;
//    }
//
//    @Override
//    public ShoppingUser getLoginUser(String token) {
//        // 拿到令牌中的用户id
//        Map<String, Object> verify = JwtUtils.verify(token);
//        Long userId = (Long) verify.get("userId");
//        // 根据id查询用户
//        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
//        queryWrapper.eq("id",userId);
//        ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
//        return shoppingUser;
//    }
//
//    @Override
//    public void checkPhone(String phone) {
//        // 1.判断手机号是否存在
//        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
//        queryWrapper.eq("phone",phone);
//        ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
//        if (shoppingUser == null){
//            throw new MyException(ResultCode.LOGIN_NOPHONE_ERROR);
//        }
//        // 2.判断用户状态是否正常
//        if (!"Y".equals(shoppingUser.getStatus())){
//            throw new MyException(ResultCode.LOGIN_USER_STATUS_ERROR);
//        }
//    }
//
//
//}
