package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/admin/user/{userId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(
            @PathVariable("userId") final String userId,
            @RequestHeader("authorization") final String authorization
    ) throws AuthorizationFailedException, UserNotFoundException {

        String authCode = extractBearerToken(authorization);

        userService.deleteUserByUuid(userId, authCode);

        return new ResponseEntity<>(
                new UserDeleteResponse().id(userId).status("USER SUCCESSFULLY DELETED"),
                HttpStatus.OK
        );
    }

    private String extractBearerToken (String authorizationHeader) {
        try {
            return authorizationHeader.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException | NullPointerException excep) {
            return ""; // will we throw bad request here?
        }
    }


}
