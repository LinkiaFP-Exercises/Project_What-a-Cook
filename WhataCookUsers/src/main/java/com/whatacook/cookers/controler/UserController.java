package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.model.users.UserJustToSave;
import com.whatacook.cookers.view.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("${security.jwt.sign-in-url}")
    @ResponseStatus(HttpStatus.CREATED)
    public Response register(@Valid @RequestBody UserJustToSave userJson) { return service.createOne(userJson); }

    @GetMapping("${app.endpoint.users-check-email}")
    public Response existsByEmail(@Valid @RequestBody UserJson userJson) {
        return service.existsByEmail(userJson);
    }

    @GetMapping("${app.endpoint.find-by-email}")
    @PreAuthorize("hasRole('USER')")
    public Response readOne(@Valid @RequestBody UserJson userJson) { return service.readOne(userJson); }

    @PutMapping("${app.endpoint.users}")
    @PreAuthorize("hasRole('USER')")
    public Response update(@RequestBody UserJson userJson) { return service.updateOne(userJson); }

    @DeleteMapping("${app.endpoint.users}")
    @PreAuthorize("hasRole('USER')")
    public Response deleteOne(@RequestBody UserJson userJson) { return service.deleteOne(userJson); }

}

