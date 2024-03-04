package com.whatacook.cookers.service.components;

import com.whatacook.cookers.service.ActivationService;
import com.whatacook.cookers.service.EmailService;
import com.whatacook.cookers.service.contracts.UserDao;
import com.whatacook.cookers.utilities.GlobalValues;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ResetComponent {

    private final ActivationService activationService;
    private final EmailService emailService;
    private final GlobalValues globalValues;
    private final UserDao DAO;


}
