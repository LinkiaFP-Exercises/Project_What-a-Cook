package com.whatacook.cookers.utilities;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for global values used in the application.
 * <p>
 * Annotations:
 * - @Getter: Generates getters for all fields.
 * - @ConfigurationProperties: Binds the class fields to properties prefixed with "global.values".
 * <p>
 * Fields:
 * - urlWacLogoPngSmall: URL for the small WAC logo.
 * - baseIp: Base IP address for the application.
 * - urlActivationAccount: URL for account activation.
 * - urlForgotPassword: URL for forgotten password.
 * - urlResetPassword: URL for resetting password.
 * - urlSetNewPassword: URL for setting a new password.
 * - mailToWac: Email address for WAC.
 * - pathToResendActvationMail: Path to resend activation email.
 * - pathToCheckIfEmailAlreadyExists: Path to check if email already exists.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
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

    @Value("${app.endpoint.javadoc}")
    private String pathToJavaDoc;

    @Value("${global.values.directory.javadoc}")
    private String pathToDirectoryJavadoc;

}
