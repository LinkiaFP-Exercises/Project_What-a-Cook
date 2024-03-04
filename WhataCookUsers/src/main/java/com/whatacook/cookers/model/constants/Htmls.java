package com.whatacook.cookers.model.constants;

public enum Htmls {

    FailActivation("""
                <!DOCTYPE html>
                    <html lang="es">
                    <head>
                        <meta charset="UTF-8">
                        <title>Activación de Cuenta Fallida</title>
                    </head>
                    <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                        <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #dda; border-radius: 8px; background-color: #FFEEDD;">
                            <img src="%s" alt="Logo WhataCook" style="width: 200px; height: auto; margin-bottom: 20px;"/>
                            <h1 style="color: #CC7700;">Lamentamos mucho, pero no fue posible activar su cuenta.</h1>
                            <p style="font-size: 17px">
                            Por favor, intenta activar tu cuenta nuevamente utilizando el enlace proporcionado en el correo electrónico de activación.</p>
                            <p style="font-size: 13px";>
                                También puede
                                <a href="%s" style="color: #FFA500;">solicitar un nuevo código de activación</a>.</p>
                            <p style="font-size: 11px";>
                                Si sigues teniendo problemas,\s
                                <a href="mailto:%s" style="color: #FFA500;">contacta con soporte</a>.</p>
                        </div>
                    </body>
                    </html>
            """),

    SuccessActivation("""
            <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Cuenta Activada</title>
                </head>
                <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                    <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                        <img src="%s" alt="Logo WhataCook" style="width: 200px; height: auto; margin-bottom: 20px;"/>
                            <h1 style="color: #4F81BD;">¡Hola, %s!</h1>
                                <h3>Su cuenta ha sido activada exitosamente.</h3>
                                    <p>Puede volver a la aplicación y continuar con el inicio de sesión.</p>
                    </div>
                </body>
                </html>
            """),

    ActivationEmail("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Activación de Cuenta</title>
            </head>
            <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                    <img src="%s" alt="Logo WhataCook" style="width: 100px; height: auto;"/>
                    <h1 style="color: #4F81BD;">Bienvenido a WhataCook, %s!</h1>
                    <p>Para activar su cuenta, por favor haga clic en el siguiente botón:</p>
                    <a href="%s" style="background-color: #4F81BD; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">Activar Cuenta</a>
                    <p style="font-size: 12px; margin-top: 15px;">Si no puede hacer clic en el botón, copie y pegue este enlace en su navegador:</p>
                    <p style="font-size: 12px;"><a href="%s">%s</a></p>
                </div>
            </body>
            </html>
            """);

    Htmls (String code) {this.code = code;}
    private final String code;
    public String get() {return code;}
}