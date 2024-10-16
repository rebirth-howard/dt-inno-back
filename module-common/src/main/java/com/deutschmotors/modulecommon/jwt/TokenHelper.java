package com.deutschmotors.modulecommon.jwt;

import com.deutschmotors.modulecommon.error.AuthErrorCode;
import com.deutschmotors.modulecommon.exception.ApiException;
import com.deutschmotors.modulecommon.redis.CommoneRedisCache;
import com.deutschmotors.modulecommon.redis.RedisCacheHelper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenHelper {

    @Value("${jwt.access-token.secret-key}")
    private String accessSecretKey;

    @Value("${jwt.refresh-token.secret-key}")
    private String refreshSecretKey;

    @Value("${jwt.pre-fix.server}")
    private String server;

    private final RedisCacheHelper redisCacheHelper;
    private final Map<String, CommoneRedisCache> redisKeys = new HashMap<>();

    @PostConstruct
    public void initializeDefaults() {
        // 기본 TTL 설정
        redisKeys.put(CommoneRedisCache.DEFAULT.getKey(), CommoneRedisCache.DEFAULT);
        redisKeys.put(CommoneRedisCache.JWT_ACCESS_TOKEN.getKey(), CommoneRedisCache.JWT_ACCESS_TOKEN);
        redisKeys.put(CommoneRedisCache.JWT_REFRESH_TOKEN.getKey(), CommoneRedisCache.JWT_REFRESH_TOKEN);
    }

    public void addRedisKey(CommoneRedisCache redisKey) {
        redisKeys.put(redisKey.getKey(), redisKey);
    }

    public CommoneRedisCache getRedisKey(String key) {
        return redisKeys.getOrDefault(key, CommoneRedisCache.DEFAULT);
    }


    private SecretKey getAccessTokenSecretKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(accessSecretKey));
    }

    private SecretKey getRefreshTokenSecretKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(refreshSecretKey));
    }

    // 사용자 이름을 추출하는 메서드
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, getAccessTokenSecretKey());
    }

    // JWT 토큰에서 Roles를 추출하는 메서드
    public List<String> extractUserRoles(String token) {
        try {
            Claims claims = extractAllClaims(token, getAccessTokenSecretKey());
            List<?> rolesList = claims.get("roles", List.class);

            // roles 리스트를 문자열 리스트로 변환
            if (rolesList != null) {
                return rolesList.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            }

            // 임시회원 ROLE은 반드시 존재
            throw new ApiException(AuthErrorCode.USER_ACCESS_FORBIDDEN);
//            return Collections.emptyList();
        } catch (Exception e) {
            throw new ApiException(AuthErrorCode.TOKEN_EXCEPTION);
        }
    }

    // JWT 토큰의 만료일을 추출하는 메서드
    public Date extractExpiration(String token, boolean isAccessToken) {
        return extractClaim(token, Claims::getExpiration, isAccessToken ? getAccessTokenSecretKey() : getRefreshTokenSecretKey());
    }

    // 특정 클레임을 추출하는 일반적인 메서드
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, SecretKey key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    // 모든 클레임을 추출하는 메서드
    private Claims extractAllClaims(String token, SecretKey key) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new ApiException(AuthErrorCode.WRONG_TYPE_TOKEN);
        }
    }

    // 토큰의 만료 여부를 확인하는 메서드
    private Boolean isTokenExpired(String token, boolean isAccessToken) {
        return extractExpiration(token, isAccessToken).before(new Date());
    }

    // JWT 토큰의 유효성을 검증하는 메서드 (액세스 토큰)
    public Boolean validateAccessToken(String token, CommonUserDetails userDetails) {
        return validateToken(token, userDetails, getAccessTokenSecretKey());
    }

    // JWT 토큰의 유효성을 검증하는 메서드 (리프레시 토큰)
    public Boolean validateRefreshToken(String token) {
        return validateToken(token, null, getRefreshTokenSecretKey());
    }

    public Boolean validateToken(String token, CommonUserDetails userDetails) {
        return validateToken(token, userDetails, getAccessTokenSecretKey());
    }
    // JWT 토큰의 유효성을 검증하는 일반적인 메서드
    private Boolean validateToken(String token, CommonUserDetails userDetails, SecretKey key) {
        try {
            final String username = extractClaim(token, Claims::getSubject, key);
            if (userDetails != null) {
                return username.equals(userDetails.getUsername()) && !isTokenExpired(token, key.equals(getAccessTokenSecretKey()));
            }
            return !isTokenExpired(token, key.equals(getAccessTokenSecretKey()));
        } catch (JwtException e) {
            log.error("Invalid token validation: {}", e.getMessage());
            return false;
        }
    }

    // 액세스 토큰과 리프레시 토큰을 생성하는 메서드
    public TokenResponse generateToken(CommonUserDetails userDetails) {
        String accessToken = generateToken(userDetails, CommoneRedisCache.JWT_ACCESS_TOKEN, getAccessTokenSecretKey());
        String refreshToken = generateToken(userDetails, CommoneRedisCache.JWT_REFRESH_TOKEN, getRefreshTokenSecretKey());
        // Redis에 토큰 저장 추가
//        storeAccessToken(userDetails.getUsername(), accessToken);
        storeRefreshToken(userDetails.getUsername(), refreshToken);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiredAt(convertDateToLocalDateTime(extractExpiration(accessToken, true)))
                .refreshToken(refreshToken)
                .refreshTokenExpiredAt(convertDateToLocalDateTime(extractExpiration(refreshToken, false)))
                .build();
    }

    // 액세스 토큰을 생성하는 메서드
    public String generateAccessToken(CommonUserDetails userDetails) {
        String token = generateToken(userDetails, CommoneRedisCache.JWT_ACCESS_TOKEN, getAccessTokenSecretKey());
//        storeAccessToken(userDetails.getUsername(), token);
        trackUserSession(userDetails.getUsername(), token, CommoneRedisCache.JWT_ACCESS_TOKEN.getKey());
        return token;
    }

    // 리프레시 토큰을 생성하는 메서드
    public String generateRefreshToken(CommonUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        String refreshToken = generateTokenWithClaims(userDetails.getUsername(), claims, calculateExpiration(CommoneRedisCache.JWT_REFRESH_TOKEN), getRefreshTokenSecretKey());
        storeRefreshToken(userDetails.getUsername(), refreshToken);
        return refreshToken;
    }

    // JWT 토큰을 생성하는 일반적인 메서드
    private String generateToken(CommonUserDetails userDetails,
                                 CommoneRedisCache cache,
                                 SecretKey key) {
        return generateTokenWithClaims(userDetails.getUsername(),
                Map.of("roles", new ArrayList<>(userDetails.getAuthorities())),
                calculateExpiration(cache),
                key);
    }

    // 클레임이 포함된 JWT 토큰을 생성하는 메서드
    private String generateTokenWithClaims(String subject, Map<String, Object> claims, long expiration, SecretKey key) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiration)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String preFix(String type) {
        return server + ":" + type + ":";
    }

    // 사용자 세션을 Redis에 저장하는 메서드
    private void trackUserSession(String username, String token, String cacheKey) {
        CommoneRedisCache cache = redisKeys.get(cacheKey);
        // session:UUID
        redisCacheHelper.setStringValue(KeyType.SESSION.getPreFix() + username, token, cache.getDuration(), cache.getUnit());
    }

    // 단일 기기 관리: 새로운 토큰이 발급될 때 기존 세션을 무효화 (Redis에서 삭제)
    public void invalidatePreviousSession(String username) {
        // session:loginId
        redisCacheHelper.deleteKey(KeyType.SESSION.getPreFix() + username);
    }

    // 액세스 토큰을 Redis에 저장하는 메서드
    private void storeAccessToken(String username, String accessToken) {
        CommoneRedisCache cache = CommoneRedisCache.JWT_ACCESS_TOKEN;
        redisCacheHelper.setStringValue(cache.getKey() + ":" + username, accessToken, cache.getDuration(), cache.getUnit());
    }

    // 리프레시 토큰을 Redis에 저장하는 메서드
    private void storeRefreshToken(String username, String refreshToken) {
        CommoneRedisCache cache = CommoneRedisCache.JWT_REFRESH_TOKEN;
        //jwtRefreshToken:UUID
        redisCacheHelper.setStringValue(cache.getKey() + ":" + username, refreshToken, cache.getDuration(), cache.getUnit());
    }

    // 액세스 토큰 갱신 메서드 (리프레시 토큰을 검증하고 새로운 액세스 토큰 발급)
    public TokenResponse refreshAccessToken(String refreshToken, CommonUserDetails userDetails) {
        if (validateRefreshToken(refreshToken)) {
            String username = extractClaim(refreshToken, Claims::getSubject, getRefreshTokenSecretKey());
            invalidatePreviousSession(username); // 기존 세션 무효화
            String newAccessToken = generateAccessToken(userDetails);
            String newRefreshToken = generateRefreshToken(userDetails);

            return TokenResponse.builder()
                    .accessToken(newAccessToken)
                    .accessTokenExpiredAt(convertDateToLocalDateTime(extractExpiration(newAccessToken, true)))
                    .refreshToken(newRefreshToken)
                    .refreshTokenExpiredAt(convertDateToLocalDateTime(extractExpiration(newRefreshToken, false)))
                    .build();
        }
        throw new ApiException(AuthErrorCode.TOKEN_EXCEPTION);
    }

    // duration과 unit을 사용하여 만료 시간을 계산하는 메서드
    private long calculateExpiration(CommoneRedisCache cache) {
        return cache.getUnit().toMillis(cache.getDuration());
    }

    private LocalDateTime convertDateToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Getter
    @AllArgsConstructor
    private enum KeyType {
        SESSION("session:"),
        ;
        private final String preFix;
    }
}

// TokenHelper 클래스는 액세스 토큰과 리프레시 토큰의 생성, 추출, 검증 등을 수행합니다.
// 비밀키는 각각 액세스 토큰과 리프레시 토큰에 대해 application.yml의 설정값을 동적으로 가져와 보안을 강화했습니다.
// 세션 추적 및 단일기기 관리를 위해 Redis와 RDS를 하이브리드 형태로 사용하여 보안을 강화하고, 데이터 일관성을 유지합니다.