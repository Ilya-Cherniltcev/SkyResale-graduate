package ru.skypro.homework.controller;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.UserAvatar;
import ru.skypro.homework.service.UserService;

import java.util.UUID;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get users
     * Use method of service {@link UserService#getUserMe()}
     *
     * @return Founded user
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was gotten ",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User Not Found"
            )
    })
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<UserDto> getUserMe() {
        return new ResponseEntity<>(userService.getUserMe(), HttpStatus.OK);
    }

    /**
     * Update user Avatar<br>
     * Use method of service {@link UserService#updateUserImage(MultipartFile)}
     *
     * @return Updated user
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User Avatar was updated ",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden Action"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User Not Found"
            )
    })
    @PatchMapping(value = "me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<UserDto> updateUserImage(@RequestParam(value = "image") MultipartFile file) {
        return new ResponseEntity<>(userService.updateUserImage(file), HttpStatus.OK);
    }

    /**
     * Update user<br>
     * Use method of service {@link UserService#updateUser(UserDto)}
     *
     * @return Updated user
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User was updated ",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden Action"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User Not Found"
            )
    })
    @PatchMapping("/me")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.updateUser(userDto), HttpStatus.OK);
    }

    /**
     * Change password for user<br>
     * Use method of service {@link UserService#setPassword(NewPasswordDto)}
     *
     * @return New Password
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User's password was changed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NewPasswordDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden Action"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User Not Found"
            )
    })
    @PostMapping("/set_password")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<NewPasswordDto> setPassword(@RequestBody NewPasswordDto newPassword) {
        return new ResponseEntity<>(userService.setPassword(newPassword), HttpStatus.OK);
    }
    /**
     * Download {@link UserAvatar} to frontend<br>
     * Use method of service {@link UserService#getAvatar(UUID)}
     *
     * @return Needed image
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User's Avatar was gotten",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Image Not Found"
            )
    })
    @GetMapping("/image/{avatarUuid}")
    public ResponseEntity<byte[]> getImage(@PathVariable UUID avatarUuid) {
        return new ResponseEntity<>(userService.getAvatar(avatarUuid), HttpStatus.OK);
    }

    /**
     * Delete user by id (for ADMIN only)<br>
     * Use method of service {@link UserService#removeUser(long)}
     *
     * @return Deleted user
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User was deleted",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
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
                    description = "User Not Found"
            )
    })
    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('admin_full_access')")
    public ResponseEntity<UserDto> deleteUser(@PathVariable long id) {
        return new ResponseEntity<>(userService.removeUser(id), HttpStatus.NO_CONTENT);
    }

}

