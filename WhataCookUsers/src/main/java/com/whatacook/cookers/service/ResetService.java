package com.whatacook.cookers.service;

import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.users.UserDto;
import com.whatacook.cookers.service.contracts.ResetDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service class for handling password reset operations.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Service: Indicates that this class is a Spring service.
 * <p>
 * Fields:
 * - DAO: The Data Access Object for ResetDto.
 * <p>
 * Methods:
 * - createNew(UserDto userDTO): Creates a new reset entry for a user.
 * - findById(String id): Finds a reset entry by ID.
 * - findByCode(String code): Finds a reset entry by code.
 * - deleteById(String id): Deletes a reset entry by ID.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ResetDao
 * @see ResetDto
 * @see UserDto
 * @see Mono
 * @see AllArgsConstructor
 * @see Service
 */
@AllArgsConstructor
@Service
public class ResetService {

    private final ResetDao DAO;

    /**
     * Creates a new reset entry for a user.
     *
     * @param userDTO the user details
     * @return the created reset entry
     */
    public Mono<ResetDto> createNew(UserDto userDTO) {
        return DAO.save(ResetDto.to(userDTO));
    }

    /**
     * Finds a reset entry by ID.
     *
     * @param id the ID of the reset entry
     * @return the found reset entry
     */
    public Mono<ResetDto> findById(String id) {
        return DAO.findById(id);
    }

    /**
     * Finds a reset entry by code.
     *
     * @param code the reset code
     * @return the found reset entry
     */
    public Mono<ResetDto> findByCode(String code) {
        return DAO.findByCode(code);
    }

    /**
     * Deletes a reset entry by ID.
     *
     * @param id the ID of the reset entry
     * @return a Mono signaling completion
     */
    public Mono<Void> deleteById(String id) {
        return DAO.deleteById(id);
    }
}
