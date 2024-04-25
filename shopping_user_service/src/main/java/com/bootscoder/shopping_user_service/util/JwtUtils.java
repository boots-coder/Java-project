package com.bootscoder.shopping_user_service.util;

import lombok.SneakyThrows;
import org.jose4j.json.JsonUtil;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    // 公钥
    public final static String PUBLIC_JSON = "{\"kty\":\"RSA\",\"n\":\"x7d-nurDAVRUygUsu7C3-Rr3ziJ67mPCfuNUUCCHF3Nr1gnNx7DoI26M_LpM0eVEeRiUpbbmvI3mS5oZHBIagXAKy4-veHHL_MOCSHDsTdMaxYix1ngvmYha98eX6oV2q1e2Zpxea2r1h9eIsgscg35aKPetQFJO3Z8Fh3GCJGq_Q1d2KwVAhXjsb_wRZkq8hAvJFPka-qSm0QO60AkQy09aHATWHzoPsqBUKweu7lwh9eXLRJQVmhM2Lzv7sQjo5yFpg3eXRpOnYfirqGHQEef1gJ1OrWu8dFv8lIinLmwoC5HY-7hfzgRSPZ0RGXiBKPZX5y3i_alI_ehKxnU1Lw\",\"e\":\"AQAB\"}";
    // 私钥
    public final static String PRIVATE_JSON = "{\"kty\":\"RSA\",\"n\":\"x7d-nurDAVRUygUsu7C3-Rr3ziJ67mPCfuNUUCCHF3Nr1gnNx7DoI26M_LpM0eVEeRiUpbbmvI3mS5oZHBIagXAKy4-veHHL_MOCSHDsTdMaxYix1ngvmYha98eX6oV2q1e2Zpxea2r1h9eIsgscg35aKPetQFJO3Z8Fh3GCJGq_Q1d2KwVAhXjsb_wRZkq8hAvJFPka-qSm0QO60AkQy09aHATWHzoPsqBUKweu7lwh9eXLRJQVmhM2Lzv7sQjo5yFpg3eXRpOnYfirqGHQEef1gJ1OrWu8dFv8lIinLmwoC5HY-7hfzgRSPZ0RGXiBKPZX5y3i_alI_ehKxnU1Lw\",\"e\":\"AQAB\",\"d\":\"VxJQgX9vklpOdx9Hb_LtWo0f4b6Vou5aYxDAdwifbSF0r8XpcfjWVHBDxoGAsXgH4NXPJOp3cVhaQbbG4L6h0LHk-vfDdDsEyMgCOowE8i8p-loM0qjmc8UHiAR8XpJeePhOPTFKVwG0V0uoPJtsjIWfUEIfRi48VRIaCoF50F7179cGXOczAY56zcACWQBlNMrVo24mHqnQ5nLrqHM0DBRldYev367A-VSdc4pceK7OcRKtI_No6msRnFyjCVEdFlASwPN7te3WAD5ryD4UI-LJGEt7dkKR55M8VU_3AiHVC6yGz0LhCcjEKr3iyfzj4fUdMM-ucFJFHlwiF-rfjQ\",\"p\":\"9j0j5MegNZWjW28W1RBdAGsIfWGfVslezHMjTaOWpypPTtQblbJYMim3V4qw9-e_QYTUJjtXXnu4Gpqp_Cw2qb0zvLe6-0TN-BNSCaaP-6gs0cZPkse6qikp25I7l-UprMRfqSFwsfFW4bmT-CzxV_oFvLrOFasjslliBEQZj_s\",\"q\":\"z6I-SCg3LvmNFqX037gyMZWHiVc4Z5Ukb_a-WzupY55xVGfmesD_uyNNX3M0wx74vTIIOwrIQkARF3kUcDR3FmXhbTDjpSxm4PTcwgguqEqdYVVr70iAyCkBJbQTdIVCb2bqxilxAL8UvGUkY2JlLZMwiDKLSj4QpuTzgfFUBV0\",\"dp\":\"HgJXhIM7qCwja3o_axoCa5GfyEAEfjwuXHZPwB-Gtbq3TaEV94lzrDFfUcDqTlwk9-QrQrmKrU4gosVkoAH3a3pOetpdZE__VhNstRZih8X4KFylx_qlaiV4H3VT4_AewxpvmhokNNt3viyyttUrWJB08IpHoWZpBExWYLS39M8\",\"dq\":\"beODLPNDyXO_zRXGFMVKo11tHy3vJzsSzsL7GhPoYVXQywg2Pg95CRT09Shm60GwCYm6O21IcW-w3Ahz3bKWuF7xQLXYNWnbhQFy-KoV73GV-5aXCkwIBGdXORomFcwVThpc9MWoDeoTUmYBLSw9OjKlQ9_kQkW40GtpyQLtIe0\",\"qi\":\"JuTj2Hi_Hf310ysPZnPtdaVv9Rc_9YVryguQ9gSIx9pPkG_MxNs1aemy24dqB0BYK-ytzcAuAyp79sYYRmuq2ZOGJ9Cms8QXoFPlZmz7sVDo5hIxwG9ZTG9x6wREsschYolqAJBft0bgivpxE1JlcK3F3Svukhmru5hMyrBAP7A\"}";

    /**
     * 生成token
     *
     * @param userId       用户id
     * @param username 用户名字
     * @return
     */
    @SneakyThrows
    public static String sign(Long userId, String username) {
        // 1、 创建jwtclaims  jwt内容载荷部分
        JwtClaims claims = new JwtClaims();
        // 是谁创建了令牌并且签署了它
        claims.setIssuer("boots");
        // 令牌将被发送给谁
        claims.setAudience("audience");
        // 失效时间长（分钟）
        claims.setExpirationTimeMinutesInTheFuture(60 * 24);
        // 令牌唯一标识符
        claims.setGeneratedJwtId();
        // 当令牌被发布或者创建
        claims.setIssuedAtToNow();
        // 再次之前令牌无效
        claims.setNotBeforeMinutesInThePast(2);
        // 主题
        claims.setSubject("shopping");
        // 可以添加关于这个主题得声明属性
        claims.setClaim("userId", userId);
        claims.setClaim("username", username);

        // 2、签名
        JsonWebSignature jws = new JsonWebSignature();
        //赋值载荷
        jws.setPayload(claims.toJson());

        // 3、jwt使用私钥签署
        PrivateKey privateKey = new RsaJsonWebKey(JsonUtil.parseJson(PRIVATE_JSON)).getPrivateKey();
        jws.setKey(privateKey);

        // 4、设置关键 kid
        jws.setKeyIdHeaderValue("keyId");

        // 5、设置签名算法
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        // 6、生成jwt
        String jwt = jws.getCompactSerialization();
        return jwt;
    }


    /**
     * 解密token，获取token中的信息
     *
     * @param token
     */
    @SneakyThrows
    public static Map<String, Object> verify(String token){
        // 1、引入公钥
        PublicKey publicKey = new RsaJsonWebKey(JsonUtil.parseJson(PUBLIC_JSON)).getPublicKey();
        // 2、使用jwtcoonsumer  验证和处理jwt
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() //过期时间
                .setAllowedClockSkewInSeconds(30) //允许在验证得时候留有一些余地 计算时钟偏差  秒
                .setRequireSubject() // 主题生命
                .setExpectedIssuer("boots") // jwt需要知道谁发布得 用来验证发布人
                .setExpectedAudience("audience") //jwt目的是谁 用来验证观众
                .setVerificationKey(publicKey) // 用公钥验证签名  验证密钥
                .setJwsAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256))
                .build();
        // 3、验证jwt 并将其处理为 claims
        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            return jwtClaims.getClaimsMap();
        }catch (Exception e){
            return new HashMap();
        }
    }


    public static void main(String[] args){
        // 生成
        String token = sign(1001L, "boots");
        System.out.println(token);

        Map<String, Object> stringObjectMap = verify(token);
        System.out.println(stringObjectMap.get("userId"));
        System.out.println(stringObjectMap.get("username"));
    }
}