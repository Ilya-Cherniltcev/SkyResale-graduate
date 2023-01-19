package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.LoginReq;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.model.Role;
import ru.skypro.homework.service.AuthService;

import static ru.skypro.homework.model.Role.USER;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    /**
     * Authentication in this application<br>
     * Use method of service {@link AuthService#login(String, String)}
     *
     * @return OK if user authenticated
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Action Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        if (authService.login(req.getLogin(), req.getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    /**
     * Registration in this application<br>
     * Use method of service {@link AuthService#register(RegisterReq, Role)}
     *
     * @return OK if user registered
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was registered"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Action Forbidden"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        Role role = req.getRole() == null ? USER : req.getRole();
        if (authService.register(req, role)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
