package com.whatacook.cookers.utilities;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "global.values")
public class GlobalValues {

    @Value("${global.values.links.wac.logo.small.png}")
    private String urlWacLogoPngSmall;

    @Value("${global.values.links.base-ip}")
    private String baseIp;

    @Value("${global.values.links.activation-account}")
    private String urlActivationAccount;

    @Value("${global.values.links.forgot-password}")
    private String urlForgotPassword;

    @Value("${global.values.links.reset-password}")
    private String urlResetPassword;

    @Value("${global.values.links.set-new-password}")
    private String urlSetNewPassword;

    @Value("${global.values.email.wac}")
    private String mailToWac;

    @Value("${global.values.url.resend.confirmation.mail}")
    private String pathToResendActvationMail;

    @Value("${global.values.url.check.email.is.used}")
    private String pathToCheckIfEmailAlreadyExists;

}
