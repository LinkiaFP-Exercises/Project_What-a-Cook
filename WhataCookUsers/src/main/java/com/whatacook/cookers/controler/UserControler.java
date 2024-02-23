package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.view.UserService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
@Validated
public class UserControler {

    private final UserService service;

    public UserControler(UserService service) {
        this.service = service;
    }

    @GetMapping("${app.endpoint.users-check-email}")
    public Response existsByEmail(@Valid @RequestBody UserJson userJson) {
        return service.existsByEmail(userJson);
    }

    @GetMapping("${app.endpoint.find-by-email}")
    public Response readOne(@Valid @RequestBody UserJson userJson) {
        return service.readOne(userJson);
    }

    @GetMapping("${app.endpoint.users}")
    public Response readAll() {
        return service.readAll(null);
    }

    @PostMapping("${app.endpoint.users}")
    public Response create(@Valid @RequestBody UserJson userJson) {
        return service.createOne(userJson);
    }

    @PutMapping("${app.endpoint.users}")
    public Response update(@RequestBody UserDTO user) {
        return null;
    }

    @DeleteMapping("${app.endpoint.users}")
    public Response deleteOne(@RequestBody UserDTO user) {
        return null;

    }

}

