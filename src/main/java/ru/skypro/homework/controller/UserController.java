package ru.skypro.homework.controller;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;
import ru.skypro.homework.service.UserService;

import java.util.Collection;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Create new user
     * Use method of service {@link UserService#createUser(CreateUserDto)}
     *
     * @return user
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Create a new user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                            // schema = @Schema(implementation = .class)
                    )
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Created"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found"
            )
    })
    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody CreateUserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    /**
     * get users
     * Use method of service {@link UserService#getUsers()}
     *
     * @return collection of users
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Get users",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                            // schema = @Schema(implementation = .class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<Collection<UserDto>> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    /**
     * Update user
     * Use method of service {@link UserService#updateUser(UserDto)}
     *
     * @return user
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was Updated ",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                            // schema = @Schema(implementation = .class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No content"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            )
    })
    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.updateUser(userDto), HttpStatus.OK);
    }

    /**
     * Set password for user
     * Use method of service {@link UserService#setPassword(NewPasswordDto)}
     *
     * @return user
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Create a new user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                            // schema = @Schema(implementation = .class)
                    )
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Created"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found"
            )
    })
    @PostMapping("/set_password")
    public ResponseEntity<NewPasswordDto> setPassword(@RequestBody NewPasswordDto newPassword) {
        return new ResponseEntity<>(userService.setPassword(newPassword), HttpStatus.OK);
    }

    /**
     * Delete user by id
     * Use method of service {@link UserService#removeUser(long)} 
     *
     * @return user
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was deleted",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                            // schema = @Schema(implementation = .class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No content"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            )
    })
    @DeleteMapping("{id}")
    public ResponseEntity<User> deleteUser(@PathVariable long id) {
        return new ResponseEntity<>(userService.removeUser(id), HttpStatus.OK);
    }

    /**
     * get user by id
     * Use method of service {@link UserService#getUserById(long)} 
     *
     * @return user
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was gotten",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                            // schema = @Schema(implementation = .class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

}

