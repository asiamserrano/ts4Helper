package org.example.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.example.ts4package.enums.ResponseEnum;
import org.example.ts4package.utilities.OkHttpUtility;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import static org.example.ts4package.constants.ControllerConstants.*;
import static org.example.ts4package.enums.ResponseEnum.*;
import static org.example.ts4package.enums.WebsiteEnum.CURSE_FORGE_CAS;

@RestController
@RequestMapping(CURSE_FORGE_CONTROLLER_REQUEST_MAPPING)
@Slf4j
@AllArgsConstructor
public class CurseForgeController {

    private OkHttpClient client;
    private CookieJar cookieJar;

    @GetMapping(CURSE_FORGE_CONTROLLER_COOKIE_STATUS_GET_MAPPING)
    public ResponseEnum cookieStatus() {
        ResponseEnum responseEnum;
        try {
            HttpUrl httpUrl = OkHttpUtility.createHttpUrl(CURSE_FORGE_CAS);
            Response response = OkHttpUtility.sendRequest(httpUrl, client);
            boolean bool = response.isSuccessful();
            response.close();
            responseEnum = bool ? SUCCESSFUL : FAILURE;
            log.info("curse forge cookie is {}", bool ? "active" : "inactive");
        } catch (Exception e) {
            log.error("cannot check status of curse forge cookie. Exception: {} ", e.getMessage());
            responseEnum = UNKNOWN;
        }
        return responseEnum;
    }

    @PostMapping(CURSE_FORGE_CONTROLLER_UPDATE_COOKIE_POST_MAPPING)
    public ResponseEnum updateCookie(@RequestBody String body) {
        String curseForgeCookie = body.strip();
        HttpUrl httpUrl = CURSE_FORGE_CAS.getHttpUrl();
        Cookie cookie = OkHttpUtility.createCookie(curseForgeCookie, httpUrl);
        cookieJar.saveFromResponse(httpUrl, Collections.singletonList(cookie));
        log.info("curse forge cookie set to {}", curseForgeCookie);
        return cookieStatus();
    }

}
