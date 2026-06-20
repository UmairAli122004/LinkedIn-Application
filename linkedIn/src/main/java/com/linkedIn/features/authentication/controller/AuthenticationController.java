package com.linkedIn.features.authentication.controller;

import com.linkedIn.features.authentication.dto.AuthenticationRequestBody;
import com.linkedIn.features.authentication.dto.AuthenticationResponseBody;
import com.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.features.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/authentication")
@RestController
@Validated
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    /*
    *
    * @RequestAttribute("authenticationUser") is used to read an attribute
    *  that was already added to the current HTTP request by some earlier
    * component, usually a Filter, Interceptor, or another controller.
    *
    *
    * authenticationUser is not coming from the client request body, path variable, or query parameter.
    * Instead, Spring looks inside the HttpServletRequest and retrieves an attribute named "authenticationUser".
    *
    * */
    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("authenticationUser") AuthenticationUser authenticationUser){
        return authenticationService.getUser(authenticationUser.getEmail());
    }

    @PostMapping("/login")
    public AuthenticationResponseBody login(@Valid @RequestBody AuthenticationRequestBody registerRequestBody){
        return authenticationService.login(registerRequestBody);
    }

    @PostMapping("/register")
    public AuthenticationResponseBody register(@Valid @RequestBody AuthenticationRequestBody loginRequestBody){
        return authenticationService.register(loginRequestBody);
    }
}
