package com.whatacook.cookers.view;

import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.users.UserDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class ActivationService {

    private final ActivationDao DAO;

    public ActivationService(ActivationDao dao) { DAO = dao; }

    public Mono<ActivationDto> createNew(UserDTO userDTO) { return DAO.save(ActivationDto.to(userDTO)); }

    public Mono<ActivationDto> findById(String id) {
        return DAO.findById(id);
    }

    public Mono<ActivationDto> findByCode(String code) { return DAO.findByCode(code); }

    public Mono<Void> deleteById(String id) {
        return DAO.deleteById(id);
    }

}
