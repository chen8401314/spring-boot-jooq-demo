package com.huagui.common.base.util;

import com.huagui.common.base.context.CommonException;
import com.huagui.common.base.context.ServiceContext;
import com.huagui.common.base.context.ThreadLocalContextAccessor;
import com.huagui.common.base.context.UserToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.compression.DeflateCompressionCodec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JWTUtils {

    private JWTUtils() {
    }

    static String keyStr;

    static {
        String key = System.getenv("KEYSTROKE");
        if (key == null || key.trim().isEmpty()) {
            key = "ThIsIsAsEcUrItYkEy";
        }
        keyStr = Base64.getEncoder().encodeToString(key.trim().getBytes(StandardCharsets.UTF_8));
    }

    public static final SignatureAlgorithm signatureAlgorithm256 = SignatureAlgorithm.HS256;
    private static final JwtParser DEFAULT_PARSER = Jwts.parser().setSigningKey(keyStr);


    private static final DeflateCompressionCodec deflateCodec = new DeflateCompressionCodec();

    public static String createToken(String id, String loginName) {
        return createToken(id, loginName, null, LocalDateTime.now().plusHours(16L), signatureAlgorithm256);
    }


    public static String createToken(String id, String loginName, Map<String, Object> claims, LocalDateTime expireDate) {
        return createToken(id, loginName, claims, expireDate, signatureAlgorithm256);
    }

    public static String createToken(String id, String loginName, Map<String, Object> claims, LocalDateTime expireDate, SignatureAlgorithm algo) {
        return Jwts.builder()
                .signWith(algo, keyStr)
                .setClaims(claims == null ? new DefaultClaims() : claims)
                .setId(id)
                .setSubject(loginName)
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expireDate.atZone(ZoneId.systemDefault()).toInstant()))
                .compressWith(deflateCodec)
                .compact();
    }

    public static Jws<Claims> parseToken(String token) {
        return DEFAULT_PARSER.parseClaimsJws(token);
    }

    public static Boolean validateToken(String claimsJws) {
        boolean flag;
        try {
            flag = parseToken(claimsJws).getBody() != null;
        } catch (Exception e) {
            log.warn("Error in validating token：{}", e.getMessage());
            throw new CommonException(401, "Please login", e, 401);
        }
        return flag;
    }

    public static UserToken extractToken(String claimsJws) {
        Claims claims;
        try {
            claims = parseToken(claimsJws).getBody();
        } catch (Exception e) {
            log.warn("Error in validating token：{}", e.getMessage());
            throw new CommonException(401, "Please login", e, 401);
        }

        if (StringUtils.isEmpty(claims.getId())) {
            throw new CommonException(401, "id is empty", null, 401);
        }

        UserToken.UserTokenBuilder builder = UserToken.builder();
        builder.id(claims.getId())
                .name(claims.getSubject())
                .expireAt(LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault()))
                .signAt(LocalDateTime.ofInstant(claims.getIssuedAt().toInstant(), ZoneId.systemDefault()))
                .version((Integer) claims.get(UserToken.VERSION));


        Object code = claims.get(UserToken.CODE);
        if (code != null) {
            builder.code(Long.parseLong(code.toString()));
        }

        Map<String, Object> props = (HashMap<String, Object>) claims.get(UserToken.PROPS);
        if (props != null) {
            builder.properties(props);
        }
        return builder.build();
    }

    public static String getLoginName(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public static String getUserId(String token) {
        return parseToken(token).getBody().getId();
    }

    public static void setServiceContext(String token) {
        UserToken userToken = UserToken.builder().id(getUserId(token)).name(getLoginName(token)).build();
        ThreadLocalContextAccessor.setServiceContext(new ServiceContext(userToken));
    }

/*    public static void main(String[] args) {
        String token = createToken("0210dd81c50b35b0da0bc4144186a5d7","test");
        System.out.println(token);
    }*/
}
