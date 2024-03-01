package com.whatacook.cookers.service;

import jakarta.annotation.PostConstruct;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailTestService {

    private final JavaMailSender emailSender;

    public EmailTestService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @PostConstruct
    public void sendTestEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("wh4tac00k@gmail.com"); // Asegúrate de que este sea el correo configurado
            message.setTo("wh4tac00k@gmail.com"); // Cambia esto por tu dirección de correo electrónico real para probar
            message.setSubject("Prueba de Configuración de Correo");
            message.setText("Si recibes este correo, ¡la configuración funciona correctamente!");

//            emailSender.send(message);
//            System.out.println("Correo de prueba enviado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo de prueba.");
        }
    }
}

