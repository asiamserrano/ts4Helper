package ts4.helper.TS4Downloader.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ts4.helper.TS4Downloader.enums.ResponseEnum;
import ts4.helper.TS4Downloader.utilities.OkHttpUtility;

import java.util.Collections;

import static ts4.helper.TS4Downloader.enums.ResponseEnum.SUCCESSFUL;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.FAILURE;
import static ts4.helper.TS4Downloader.enums.ResponseEnum.UNKNOWN;

import static ts4.helper.TS4Downloader.constants.ControllerConstants.CURSE_FORGE_CONTROLLER_REQUEST_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.CURSE_FORGE_CONTROLLER_COOKIE_STATUS_GET_MAPPING;
import static ts4.helper.TS4Downloader.constants.ControllerConstants.CURSE_FORGE_CONTROLLER_UPDATE_COOKIE_POST_MAPPING;

import static ts4.helper.TS4Downloader.enums.WebsiteEnum.CURSE_FORGE_CAS;

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
