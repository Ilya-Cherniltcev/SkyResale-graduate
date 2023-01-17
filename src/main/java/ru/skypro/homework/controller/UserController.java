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
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * get users
     * Use method of service {@link UserService#getUserMe()}
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
    public ResponseEntity<UserDto> getUserMe() {
        return new ResponseEntity<>(userService.getUserMe(), HttpStatus.OK);
    }


    @PatchMapping(value = "me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateUserImage(@RequestParam(value = "image") MultipartFile file) {
        return new ResponseEntity<>(userService.updateUserImage(file), HttpStatus.OK);
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

    @GetMapping("/image/{avatarId}")
    public ResponseEntity<byte[]> getImage(@PathVariable long avatarId) {
        return new ResponseEntity<>(userService.getAvatar(avatarId), HttpStatus.OK);
    }


}

