package com.lotte.danuri.auth.oauth.common;

import com.lotte.danuri.auth.oauth.kakao.KakaoService;
import com.lotte.danuri.auth.oauth.naver.NaverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
@Slf4j
public class OAuthAttributeService {

    private final KakaoService kakaoService;
    private final NaverService naverService;

    public OAuthAttributeService(KakaoService kakaoService, NaverService naverService) {
        this.kakaoService = kakaoService;
        this.naverService = naverService;
    }

    public MultiValueMap<String, String> getParams(String code, String service) {

        switch (service) {
            case "kakao":
                return kakaoService.of(code);
            case "naver":
                return naverService.of(code);
            default:
                throw new RuntimeException();
        }
    }

    public String getTokenURI(String service) {

        switch (service) {
            case "kakao" :
                return kakaoService.getTokenURI();
            case "naver" :
                return naverService.getTokenURI();
            default:
                throw new RuntimeException();
        }
    }

    public String getUserInfoURI(String service) {

        switch (service) {
            case "kakao" :
                return kakaoService.getUserInfoURI();
            case "naver" :
                return naverService.getUserInfoURI();
            default:
                throw new RuntimeException();
        }
    }

    public String getAdminKey(String service) {

        switch (service) {
            case "kakao" :
                return kakaoService.getAdminKey();
            case "naver" :
                return naverService.getAdminKey();
            default:
                throw new RuntimeException();
        }
    }

}
