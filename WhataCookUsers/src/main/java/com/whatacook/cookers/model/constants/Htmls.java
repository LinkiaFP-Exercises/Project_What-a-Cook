package com.whatacook.cookers.model.constants;

/**
 * Enumeration representing different HTML templates used in the application.
 * <p>
 * Templates:
 * - ActivationEmail: HTML template for account activation email.
 * - FailActivation: HTML template for failed account activation.
 * - SuccessActivation: HTML template for successful account activation.
 * - ResetPasswordMail: HTML template for password reset email.
 * - FailReset: HTML template for failed password reset.
 * - FormToSendNewPassword: HTML template for the form to send a new password.
 * - FailSetNewPassword: HTML template for failed password setting.
 * - SuccessSetNewPassword: HTML template for successful password setting.
 * <p>
 * Methods:
 * - get(): Returns the HTML code associated with the template.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public enum Htmls {

    ActivationEmail("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Activación de Cuenta</title>
            </head>
            <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                    <img src="LOGO_WAC" alt="Logo WhataCook" style="width: 100px; height: auto;"/>
                    <h1 style="color: #4F81BD;">Bienvenido a WhataCook, USER_NAME!</h1>
                    <p>Para activar su cuenta, por favor haga clic en el siguiente botón:</p>
                    <a href="ACTIVATION_LINK" style="background-color: #4F81BD; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">Activar Cuenta</a>
                    <p style="font-size: 12px; margin-top: 15px;">Si no puede hacer clic en el botón, copie y pegue este enlace en su navegador:</p>
                    <p style="font-size: 12px;"><a href="ACTIVATION_LINK">ACTIVATION_LINK</a></p>
                </div>
            </body>
            </html>
            """
    ),
    FailActivation("""
                <!DOCTYPE html>
                    <html lang="es">
                    <head>
                        <meta charset="UTF-8">
                        <title>Activación de Cuenta Fallida</title>
                    </head>
                    <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                        <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #dda; border-radius: 8px; background-color: #FFEEDD;">
                            <img src="LOGO_WAC" alt="Logo WhataCook" style="width: 200px; height: auto; margin-bottom: 20px;"/>
                            <h1 style="color: #CC7700;">Lamentamos mucho, pero no fue posible activar su cuenta.</h1>
                            <p style="font-size: 17px">
                            Por favor, intenta activar tu cuenta nuevamente utilizando el enlace proporcionado en el correo electrónico de activación.</p>
                            <p style="font-size: 13px";>
                                También puede
                                <a href="PATH_TO_RESEND" style="color: #FFA500;">solicitar un nuevo código de activación</a>.</p>
                            <p style="font-size: 11px";>
                                Si sigues teniendo problemas,\s
                                <a href="mailto:EMAIL_WAC"" style="color: #FFA500;">contacta con soporte</a>.</p>
                        </div>
                    </body>
                    </html>
            """
    ),

    SuccessActivation("""
            <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Cuenta Activada</title>
                </head>
                <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                    <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                        <img src="LOGO_WAC" alt="Logo WhataCook" style="width: 200px; height: auto; margin-bottom: 20px;"/>
                            <h1 style="color: #4F81BD;">¡Hola, USER_NAME!</h1>
                                <h3>Su cuenta ha sido activada exitosamente.</h3>
                                    <p>Puede volver a la aplicación y continuar con el inicio de sesión.</p>
                    </div>
                </body>
                </html>
            """
    ),

    ResetPasswordMail("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Restablecimiento de Contraseña</title>
            </head>
            <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                    <img src="LOGO_WAC" alt="Logo WhataCook" style="width: 100px; height: auto;"/>
                    <h1 style="color: #4F81BD;">¡Hola, USER_NAME!</h1>
                    <h2 style="color: #4F81BD;">Restablecimiento de Contraseña</h2>
                    <p>Hemos recibido una solicitud para restablecer la contraseña de su cuenta.</p>
                    <p>Para restablecer su contraseña, haga clic en el siguiente botón:</p>
                    <a href="ACTIVATION_LINK" style="background-color: #4F81BD; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">Restablecer Contraseña</a>
                    <p style="font-size: 12px; margin-top: 15px;">Si no solicitó restablecer su contraseña, por favor ignore este correo.</p>
                    <p style="font-size: 12px;">Si tiene problemas para hacer clic en el botón, copie y pegue el siguiente enlace en su navegador:</p>
                    <p style="font-size: 12px;"><a href="ACTIVATION_LINK">ACTIVATION_LINK</a></p>
                </div>
            </body>
            </html>
            """
    ),

    FailReset("""
                <!DOCTYPE html>
                    <html lang="es">
                    <head>
                        <meta charset="UTF-8">
                        <title>Reinicio de Contraseña Fallido</title>
                    </head>
                    <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                        <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #dda; border-radius: 8px; background-color: #FFEEDD;">
                            <img src="LOGO_WAC" alt="Logo WhataCook" style="width: 200px; height: auto; margin-bottom: 20px;"/>
                            <h1 style="color: #CC7700;">No fue posible procesar tu solicitud de reinicio de contraseña.</h1>
                            <p style="font-size: 17px">
                            Por favor, intenta reiniciar tu contraseña nuevamente utilizando el enlace proporcionado en el correo electrónico de reinicio.</p>
                            <p style="font-size: 13px";>
                                Si sigues teniendo problemas,\s
                                <a href="mailto:EMAIL_WAC" style="color: #FFA500;">contacta con soporte</a>.</p>
                                <input type="hidden" id="errorDescription" value="errorDescriptionValue">
                        </div>
                    </body>
                    </html>
            """
    ),

    FormToSendNewPassword("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Restablecimiento de Contraseña</title>
                <script>
                    window.onload = function() {
                        document.getElementById("resetPasswordForm").addEventListener('submit', function(e) {
                            e.preventDefault();
                        
                            var userId = document.getElementById('user_id').value;
                            var newPassword = document.getElementById("newPassword").value;
                            var confirmPassword = document.getElementById("confirmPassword").value;
                            var regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!¡|'´`¨^*+@·#$%&/{}()=-_:.;,<>?¿])[A-Za-z\\d!¡|'´`¨^*+@·#$%&/{}()=-_:.;,<>?¿]{8,}$/;
                        
                            // Verifica si las contraseñas coinciden
                            if (newPassword !== confirmPassword) {
                                alert("Las contraseñas no coinciden.");
                                return;
                            } else if (!regex.test(newPassword)) {
                                alert("La contraseña no cumple con los requisitos mínimos.");
                                return;
                            }
                        
                        
                        
                            var data = {
                                "_id": userId,
                                "newPassword": newPassword
                            };
                        
                        
                            fetch('ENDPOINT_RESET_PASS', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json'
                                },
                                body: JSON.stringify(data)
                            })
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error('Network response was not ok');
                                }
                                return response.json();
                            })
                            .then(data => {
                                // Manejar respuesta del servidor
                                console.log('Success:', data);
                            })
                            .catch((error) => {
                                console.error('Error:', error);
                            });
                        
                        });
                    };
                </script>
            </head>
            <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                    <img src="LOGO_WAC" alt="Logo WhataCook" style="width: 100px; height: auto;"/>
                    <h1 style="color: #4F81BD;">¡Hola!</h1>
                    <h2 style="color: #4F81BD;">Restablecimiento de Contraseña</h2>
                    <p>Hemos recibido una solicitud para restablecer la contraseña de su cuenta.</p>
                    <form id="resetPasswordForm" action="ENDPOINT_RESET_PASS" method="POST">
                        <input type="hidden" id="user_id" value="RESET_CODE">
                        <p>Por favor, ingrese su nueva contraseña:</p>
                        <input type="password" id="newPassword" name="newPassword" placeholder="Nueva Contraseña" required>
                        <input type="password" id="confirmPassword" placeholder="Confirmar Nueva Contraseña" required>
                        <div style="margin-top: 20px;">
                            <button type="submit" style="background-color: #4F81BD; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">Restablecer Contraseña</button>
                        </div>
                    </form>
                    <p style="font-size: 12px; margin-top: 15px;">Si no solicitó restablecer su contraseña, por favor ignore este correo.</p>
                </div>
            </body>
            </html>
            """
    ),

    FailSetNewPassword("""
                <!DOCTYPE html>
                    <html lang="es">
                    <head>
                        <meta charset="UTF-8">
                        <title>Restabelecer nueva contraseña</title>
                    </head>
                    <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                        <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #dda; border-radius: 8px; background-color: #FFEEDD;">
                            <img src="LOGO_WAC" alt="Logo WhataCook" style="width: 200px; height: auto; margin-bottom: 20px;"/>
                            <h1 style="color: #CC7700;">Lamentamos mucho, pero no fue posible restabelecer su contraseña.</h1>
                            <p style="font-size: 17px">
                            Por favor, intenta restabelecer su contraseña nuevamente utilizando el enlace proporcionado en el correo electrónico de restabelicimiento.</p>
                            <p style="font-size: 13px";>
                                También puede
                                <a href="URL_FORGOT_PASS" style="color: #FFA500;">solicitar un nuevo código de activación</a>.</p>
                            <p style="font-size: 11px";>
                                Si sigues teniendo problemas,s
                                <a href="mailto:EMAIL_WAC" style="color: #FFA500;">contacta con soporte</a>.</p>
                                <input type="hidden" id="errorDescription" value="errorDescriptionValue">
                        </div>
                    </body>
                    </html>
            """
    ),
    SuccessSetNewPassword("""
                <!DOCTYPE html>
                    <html lang="es">
                    <head>
                        <meta charset="UTF-8">
                        <title>Contraseña Restabelecida</title>
                    </head>
                    <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                        <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                            <img src="LOGO_WAC" alt="Logo WhataCook" style="width: 200px; height: auto; margin-bottom: 20px;"/>
                                <h1 style="color: #4F81BD;">¡Hola, USER_NAME!</h1>
                                    <h3>Su contraseña ha sido restabelecida exitosamente.</h3>
                                        <p>Puede volver a la aplicación y continuar con el inicio de sesión.</p>
                        </div>
                    </body>
                    </html>
            """
    );

    Htmls(String code) {
        this.code = code;
    }

    private final String code;

    public String get() {
        return code;
    }
}
