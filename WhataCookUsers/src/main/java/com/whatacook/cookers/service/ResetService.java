package com.whatacook.cookers.service;

import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.users.UserDto;
import com.whatacook.cookers.service.contracts.ResetDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class ResetService {

    private final ResetDao DAO;

    public Mono<ResetDto> createNew(UserDto userDTO) {return DAO.save(ResetDto.to(userDTO));}

    public Mono<ResetDto> findById(String id) {
        return DAO.findById(id);
    }

    public Mono<ResetDto> findByCode(String code) { return DAO.findByCode(code); }

    public Mono<Void> deleteById(String id) {
        return DAO.deleteById(id);
    }

}
