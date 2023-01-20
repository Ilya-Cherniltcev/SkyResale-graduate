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
import ru.skypro.homework.dto.*;
import ru.skypro.homework.model.AdsImage;
import ru.skypro.homework.service.AdsService;

import java.util.UUID;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdsController {

    private final AdsService adsService;


    /**
     * get All ads from DataBase<br>
     * Use method of service {@link AdsService#getAllAds()}
     *
     * @return Collection of ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Got all ads",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapperAdsDto.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ResponseWrapperAdsDto> getAllAds() {
        return new ResponseEntity<>(adsService.getAllAds(), HttpStatus.OK);
    }


    /**
     * Create new ads<br>
     * Use method of service {@link AdsService#createAds(CreateAdsDto, MultipartFile[])}
     *
     * @return Created ads
     */
    @ApiResponses({

            @ApiResponse(
                    responseCode = "201",
                    description = "Created new Ads",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsDto.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Action Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ads Not Found"
            )
    })

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<AdsDto> addAds(@RequestPart(value = "properties") CreateAdsDto adsDto,
                                         @RequestParam(value = "image") MultipartFile[] files) {
        return new ResponseEntity<>(adsService.createAds(adsDto, files), HttpStatus.CREATED);
    }

    /**
     * Delete ads by adsId<br>
     * Use method of service {@link AdsService#removeAds(long)}
     *
     * @return Deleted ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Ads was deleted",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Action Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ads Not Found"
            )
    })
    @DeleteMapping("/{adsId}")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<AdsDto> removeAds(@PathVariable long adsId) {
        return new ResponseEntity<>(adsService.removeAds(adsId), HttpStatus.NO_CONTENT);
    }

    /**
     * Get FullAds by adsId<br>
     * Use method of service {@link AdsService#getFullAds(long)}
     *
     * @return Full data Ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ads was gotten",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FullAdsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Action Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ads Not Found"
            )
    })
    @GetMapping("/{adsId}")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<FullAdsDto> getFullAds(@PathVariable long adsId) {
        return new ResponseEntity<>(adsService.getFullAds(adsId), HttpStatus.OK);
    }

    /**
     * Update ads<br>
     * Use method of service {@link AdsService#updateAds(long, CreateAdsDto)}
     *
     * @return Updated ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ads was updated ",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Action Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ads Not Found"
            )
    })
    @PatchMapping("{adsId}")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<AdsDto> updateAds(@PathVariable long adsId,
                                            @RequestBody CreateAdsDto adsDto) {
        return new ResponseEntity<>(adsService.updateAds(adsId, adsDto), HttpStatus.OK);
    }

    /**
     * Update adsImage<br>
     * Use method of service {@link AdsService#updateAdsImage(long, MultipartFile)}
     *
     * @return Updated ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ads was updated ",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ads Not Found"
            )
    })
    @PatchMapping(value = "{adsId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<AdsDto> updateAdsImage(@PathVariable long adsId,
                                                 @RequestParam(value = "image") MultipartFile file
    ) {
        return new ResponseEntity<>(adsService.updateAdsImage(adsId, file), HttpStatus.OK);
    }


    /**
     * Get my ads from DataBase<br>
     * Use method of service {@link AdsService#getAdsMe()}
     *
     * @return Collection of Ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Got my ads",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapperAdsDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
            ),
    })
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<ResponseWrapperAdsDto> getAdsMe() {
        return new ResponseEntity<>(adsService.getAdsMe(), HttpStatus.OK);
    }
//*****************************ADS_COMMENTS*********************************

    /**
     * Get comment from ads by id<br>
     * Use method of service {@link AdsService#getAdsComments(long)}
     *
     * @return Collection of comments
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Get comment from ads",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapperCommentDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ads Not Found"
            )
    })
    @GetMapping("{adsId}/comments")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<ResponseWrapperCommentDto> getAdsComments(@PathVariable long adsId) {
        return new ResponseEntity<>(adsService.getAdsComments(adsId), HttpStatus.OK);
    }

    /**
     * Create comment to ads<br>
     * Use method of service {@link AdsService#createAdsComments(long, AdsCommentDto)}
     *
     * @return Comment
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Created comment to ads",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsCommentDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Action Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ads Not Found"
            )
    })
    @PostMapping("{adsId}/comments")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<AdsCommentDto> addAdsComment(@PathVariable long adsId,
                                                       @RequestBody AdsCommentDto adsCommentDto) {
        return new ResponseEntity<>(adsService.createAdsComments(adsId, adsCommentDto), HttpStatus.CREATED);
    }

    /**
     * Get comment from ads<br>
     * Use method of service {@link AdsService#getAdsComment(long, long)}
     *
     * @return Comment
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment was gotten",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsCommentDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found"
            )
    })
    @GetMapping("{adsId}/comments/{commentId}")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<AdsCommentDto> getAdsComment(@PathVariable long adsId,
                                                       @PathVariable long commentId) {
        return new ResponseEntity<>(adsService.getAdsComment(adsId, commentId), HttpStatus.OK);
    }


    /**
     * Delete comment from ads by adsId and comment's id<br>
     * Use method of service {@link AdsService#deleteAdsComments(long, long)}
     *
     * @return Comment
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment was deleted",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsCommentDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No content"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
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

    @DeleteMapping("{adsId}/comments/{commentId}")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<AdsCommentDto> deleteAdsComment(@PathVariable long adsId,
                                                          @PathVariable long commentId) {
        return new ResponseEntity<>(adsService.deleteAdsComments(adsId, commentId), HttpStatus.OK);
    }

    /**
     * Update ads comment<br>
     * Use method of service {@link AdsService#updateAdsComments(long, long, AdsCommentDto)}
     *
     * @return Comment
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ads comment was updated ",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsCommentDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized User"
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
    @PatchMapping("{adsId}/comments/{commentId}")
    @PreAuthorize("hasAuthority('user_basic_access')")
    public ResponseEntity<AdsCommentDto> updateAdsComment(@PathVariable long adsId,
                                                          @PathVariable long commentId,
                                                          @RequestBody AdsCommentDto adsCommentDto
    ) {
        return new ResponseEntity<>(adsService.updateAdsComments(adsId, commentId, adsCommentDto), HttpStatus.OK);
    }

    /**
     * Download {@link AdsImage} to frontend<br>
     * Use method of service {@link AdsService#getImage(UUID)}
     *
     * @return Needed image
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Got image from ads",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Image Not Found"
            )
    })
    @GetMapping("/image/{adsImageUUID}")
    public ResponseEntity<byte[]> getImage(@PathVariable UUID adsImageUUID) {
        return new ResponseEntity<>(adsService.getImage(adsImageUUID), HttpStatus.OK);
    }

}

