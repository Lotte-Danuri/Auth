package com.lotte.danuri.auth.jwt;

import com.lotte.danuri.auth.jwt.dto.LoginReqDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Transactional
public class JwtServiceImpl implements JwtService {

    private static final String SECRET_KEY = "NMA8JPctFuna59f5";

    @Override
    public String createJwt(Long memberId) {
        return Jwts.builder()
            .setHeaderParam("type", "jwt")
            .claim("memberId", memberId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*24*365)))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();

    }

    @Override
    public String getJwt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    @Override
    public Long getMemberId() throws IllegalAccessException {

        String accessToken = getJwt();
        if(accessToken == null || accessToken.length() == 0) {
            throw new IllegalAccessException();
        };

        Jws<Claims> claims;
        try {
            claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(accessToken);
        }catch (Exception e) {
            throw new IllegalAccessException();
        }

        return claims.getBody().get("memberId", Long.class);
    }
}
