package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.UserService;
import com.whatacook.cookers.utilities.ValidEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@Validated
public class UserController {

    private final UserService service;

    @PostMapping("${app.endpoint.users-check-email}")
    @Operation(summary = "Check if user email exists in database", operationId = "existsByEmail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response with success status indicating if user exists or not.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = {
                                    @ExampleObject(name = "userExists", summary = "User already exists", value = "{\"success\": true, \"message\": \"User already exists\", \"content\": true}"),
                                    @ExampleObject(name = "userDoesNotExist", summary = "User does not exist yet", value = "{\"success\": true, \"message\": \"User does not exist yet\", \"content\": false}")
                            }))
    })
    public Mono<Response> existsByEmail(@Valid @RequestBody UserJson userJson) { return service.existsByEmail(userJson); }

    @GetMapping("${app.endpoint.find-by-email}")
    @PreAuthorize("hasRole('USER')")
    public Mono<Response> readOne(@Valid @RequestBody UserJson userJson) { return service.readOne(userJson); }

    @PutMapping("${app.endpoint.users}")
    @PreAuthorize("hasRole('USER')")
    public Mono<Response> update(@RequestBody UserJson userJson) { return service.updateOne(userJson); }

    @DeleteMapping("${app.endpoint.users}")
    @PreAuthorize("hasRole('USER')")
    public Mono<Response> deleteOne(@RequestBody UserJson userJson) { return service.deleteOne(userJson); }

    @GetMapping("${app.endpoint.users-activate}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> activate(@RequestParam("activationCode") String activationCode) {
        return service.activateAccount(activationCode);
    }

    @GetMapping("${app.endpoint.users-resend}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Response> resendActivation(@ValidEmail @RequestParam("emailToResend") String emailToResend) {
        return service.resendActivateCode(emailToResend);
    }

    @GetMapping("${app.endpoint.reset-pass}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> resetPassword(@RequestParam("resetCode") String resetCode) {
        return service.resetPasswordByCode(resetCode);
    }

    @PostMapping("${app.endpoint.set-new-pass}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> setNewPassword(@RequestBody UserJson userJson) {
        return service.setNewPasswordByCode(userJson);
    }

}

