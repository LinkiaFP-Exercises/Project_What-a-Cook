package com.whatacook.cookers.service;

import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.users.UserDto;
import com.whatacook.cookers.service.contracts.ActivationDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class ActivationService {

    private final ActivationDao DAO;

    public Mono<ActivationDto> createNew(UserDto userDTO) { return DAO.save(ActivationDto.to(userDTO)); }

    public Mono<ActivationDto> findById(String id) {
        return DAO.findById(id);
    }

    public Mono<ActivationDto> findByCode(String code) { return DAO.findByCode(code); }

    public Mono<Void> deleteById(String id) {
        return DAO.deleteById(id);
    }

}
