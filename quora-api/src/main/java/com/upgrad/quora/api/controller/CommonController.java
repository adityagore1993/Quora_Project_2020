package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.UserProfileService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.quora.api.model.UserDetailsResponse;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private UserProfileService userProfileService;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws SignOutRestrictedException, AuthorizationFailedException, UserNotFoundException {
        final UserEntity userProfile = userProfileService.getUserProfile(userId, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().userName(userProfile.getUserName()).firstName(userProfile.getFirstName()).lastName(userProfile.getLastName()).emailAddress(userProfile.getEmail())
                .aboutMe(userProfile.getAboutme()).country(userProfile.getCountry()).dob(userProfile.getDob()).contactNumber(userProfile.getContactNumber());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);

    }
}
