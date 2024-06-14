package com.whatacook.cookers.service;

import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.users.UserDto;
import com.whatacook.cookers.service.contracts.ActivationDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service class for handling activation-related operations.
 * <p>
 * Annotations:
 * - @Slf4j: Enables logging.
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Service: Indicates that this class is a Spring service.
 * <p>
 * Fields:
 * - DAO: The activation data access object for interacting with the database.
 * <p>
 * Methods:
 * - createNew(UserDto userDTO): Creates a new activation entry for a given user.
 * - findById(String id): Finds an activation entry by its ID.
 * - findByCode(String code): Finds an activation entry by its code and logs the result.
 * - deleteById(String id): Deletes an activation entry by its ID.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ActivationDao
 * @see ActivationDto
 * @see UserDto
 * @see Mono
 * @see Service
 * @see AllArgsConstructor
 * @see Slf4j
 */
@Slf4j
@AllArgsConstructor
@Service
public class ActivationService {

    private final ActivationDao DAO;

    public Mono<ActivationDto> createNew(UserDto userDTO) {
        return DAO.save(ActivationDto.to(userDTO));
    }

    public Mono<ActivationDto> findById(String id) {
        return DAO.findById(id);
    }

    public Mono<ActivationDto> findByCode(String code) {
        return DAO.findByCode(code)
                .doOnNext(dto -> log.info("ActivationDto encontrado: {}", dto))
                .doOnError(e -> log.error("Error al buscar ActivationDto", e));
    }

    public Mono<Void> deleteById(String id) {
        return DAO.deleteById(id);
    }

}
