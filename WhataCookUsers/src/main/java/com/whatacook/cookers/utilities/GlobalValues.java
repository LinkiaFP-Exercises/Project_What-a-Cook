package com.whatacook.cookers.utilities;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "global.values")
public class GlobalValues {

    @Value("${global.values.links.wac.logo.small.png}")
    private String wacLogoPngSmall;

    @Value("${global.values.email.wac}")
    private String mailToWac;

    @Value("${global.values.url.resend.confirmation.mail}")
    private String urlToResendActvationMail;

    @Value("${global.values.url.check.email.is.used}")
    private String pathToCheckIfEmailAlreadyExists;

}
