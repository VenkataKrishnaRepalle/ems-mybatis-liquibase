package com.learning.emsmybatisliquibase.controller;

import com.learning.emsmybatisliquibase.entity.OtpAuth;
import com.learning.emsmybatisliquibase.entity.enums.OtpAuthType;
import com.learning.emsmybatisliquibase.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/sendOtp")
    public ResponseEntity<OtpAuth> sendOtp(@RequestParam("email") String email,
                                           @RequestParam("type") OtpAuthType type) {
        return ResponseEntity.ok(otpService.sendOtp(email, type));
    }

    @PostMapping("/verifyOtp/{employeeUuid}")
    public ResponseEntity<OtpAuth> verifyOtp(@PathVariable UUID employeeUuid,
                                             @RequestParam("otp") String otp,
                                             @RequestParam("type") OtpAuthType type) {
        return ResponseEntity.ok(otpService.verifyOtp(employeeUuid, otp, type));
    }
}