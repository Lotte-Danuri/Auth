package com.lotte.danuri.auth.oauth.naver;

import com.lotte.danuri.auth.oauth.common.OAuthDetailService;
import com.lotte.danuri.auth.oauth.common.SignUpByOAuthDto;
import java.math.BigInteger;
import java.security.SecureRandom;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@Transactional
@Slf4j
public class NaverService implements OAuthDetailService {

    private final NaverAttribute attribute;

    public NaverService(NaverAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public MultiValueMap<String, String> of(String code) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "authorization_code");
        params.add("client_id", attribute.getClientId());
        params.add("client_secret", attribute.getClientSecret());
        params.add("code", code);
        params.add("state", new BigInteger(130, new SecureRandom()).toString());

        return params;
    }

    @Override
    public SignUpByOAuthDto getUserInfo(String body) {

        JSONParser parser = new JSONParser();
        JSONObject obj;

        try {
            JSONObject object = (JSONObject) parser.parse(body);
            obj = (JSONObject) parser.parse(object.get("response").toString());

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return SignUpByOAuthDto.builder()
            .id((String) obj.get("id"))
            .name((String) obj.get("name"))
            .email((String) obj.get("email"))
            .gender((String) obj.get("gender"))
            .birthday((String) obj.get("birthday"))
            .phone((String) obj.get("mobile"))
            .role(0).build();
    }

    public String getTokenURI() {
        return attribute.getTokenURI();
    }

    public String getAdminKey() {
        return attribute.getClientSecret();
    }

    public String getUserInfoURI() {
        return attribute.getUserInfoURI();
    }

}
