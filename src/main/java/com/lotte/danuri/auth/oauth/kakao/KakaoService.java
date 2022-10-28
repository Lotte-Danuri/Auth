package com.lotte.danuri.auth.oauth.kakao;

import com.lotte.danuri.auth.oauth.common.OAuthDetailService;
import com.lotte.danuri.auth.oauth.common.SignUpByOAuthDto;
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
public class KakaoService implements OAuthDetailService {

    private final KakaoAttribute attribute;

    public KakaoService(KakaoAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public MultiValueMap<String, String> of(String code) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "authorization_code");
        params.add("client_id", attribute.getClientId());
        params.add("redirect_uri", attribute.getRedirectURI());
        params.add("code", code);
        params.add("client_secret", attribute.getClientSecret());

        return params;
    }

    @Override
    public SignUpByOAuthDto getUserInfo(String body) {

        JSONParser parser = new JSONParser();
        JSONObject object;
        JSONObject nameObj;
        JSONObject obj;

        try {
            object = (JSONObject) parser.parse(body);
            nameObj = (JSONObject) parser.parse(object.get("properties").toString());
            obj = (JSONObject) parser.parse(object.get("kakao_account").toString());

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return SignUpByOAuthDto.builder()
            .id(String.valueOf(object.get("id")))
            .name((String) nameObj.get("nickname"))
            .email((String) obj.get("email"))
            .gender((String) obj.get("gender"))
            .birthday((String) obj.get("birthday"))
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
