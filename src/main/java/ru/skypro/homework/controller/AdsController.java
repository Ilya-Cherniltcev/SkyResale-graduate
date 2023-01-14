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
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsCommentDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.service.AdsService;

import java.util.Collection;
import java.util.List;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdsController {

    private final AdsService adsService;


    /**
     * get All ads from DataBase
     * Use method of service {@link AdsService#getAllAds()}
     *
     * @return collection of ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Get all ads",
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
    @GetMapping
    public ResponseEntity<Collection<AdsDto>> getAllAds() {
        return new ResponseEntity<>(adsService.getAllAds(), HttpStatus.OK);
    }


    /**
     * Create new ads

     * Use method of service {@link AdsService#createAds(CreateAdsDto, MultipartFile[])}

     *
     * @return ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Create a new ads",
                    content = {@Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                            // schema = @Schema(implementation = .class)
                    )}
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdsDto> addAds(@RequestPart(value = "adsDto") CreateAdsDto adsDto,
                                         @RequestParam(value = "files") MultipartFile[] files) {
        return new ResponseEntity<>(adsService.createAds(adsDto, files), HttpStatus.CREATED);
    }

    /**
     * Delete ads by adsId
     * Use method of service {@link AdsService#removeAds(long)}
     *
     * @return ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ads was deleted",
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
    @DeleteMapping("/{adsId}")
    public ResponseEntity<AdsDto> removeAds(@PathVariable long adsId) {
        return new ResponseEntity<>(adsService.removeAds(adsId), HttpStatus.OK);
    }

    /**
     * get ads by id
     * Use method of service {@link AdsService#getAdsById(long)}
     *
     * @return ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ads was gotten",
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
    @GetMapping("/{adsId}")
    public ResponseEntity<AdsDto> getAds(@PathVariable long adsId) {
        return new ResponseEntity<>(adsService.getAdsById(adsId), HttpStatus.OK);
    }

    /**
     * Update ads
     * Use method of service {@link AdsService#updateAds(long, CreateAdsDto)}
     *
     * @return ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ads was Updated ",
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
    @PatchMapping("{adsId}")
    public ResponseEntity<AdsDto> updateAds(
            @PathVariable long adsId,
            @RequestBody CreateAdsDto adsDto) {
        return new ResponseEntity<>(adsService.updateAds(adsId, adsDto), HttpStatus.OK);
    }

    /**
     * get my ads from DataBase
     * Use method of service {@link AdsService#getAdsMe()}
     *
     * @return collection of ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Get my ads",
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
    public ResponseEntity<Collection<AdsDto>> getAdsMe() {
        return new ResponseEntity<>(adsService.getAdsMe(), HttpStatus.OK);
    }
//*****************************ADS_COMMENTS*********************************
    /**
     * get comment from ads by id
     * Use method of service {@link AdsService#getAdsComments(long)}
     *
     * @return String (comment)
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Get comment from ads",
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
    @GetMapping("{adsId}/comment")
    public ResponseEntity<List<AdsCommentDto>> getAdsComments(@PathVariable long adsId) {
        return new ResponseEntity<>(adsService.getAdsComments(adsId), HttpStatus.OK);
    }

    /**
     * Create comment to ads
     * Use method of service {@link AdsService#createAdsComments(long, CreateAdsCommentDto)} (long, AdsCommentDto)}
     *
     * @return ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Create comment to ads",
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
    @PostMapping("{adsId}/comment")
    public ResponseEntity<AdsCommentDto> addAdsComment(@PathVariable long adsId,
                                                       @RequestBody CreateAdsCommentDto adsComment) {
        return new ResponseEntity<>(adsService.createAdsComments(adsId, adsComment), HttpStatus.CREATED);
    }

    /**
     * Delete comment from ads by adsId and comment's id
     * Use method of service {@link AdsService#deleteAdsComments(long, long)}
     *
     * @return ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Сomment was deleted",
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

    @DeleteMapping("{adsId}/comment/{commentId}")
    public ResponseEntity<AdsCommentDto> deleteAdsComment(@PathVariable long adsId,
                                                          @PathVariable long commentId) {
        return new ResponseEntity<>(adsService.deleteAdsComments(adsId, commentId), HttpStatus.OK);
    }

    /**
     * Update ads comment
     * Use method of service {@link AdsService#updateAdsComments(long, long, CreateAdsCommentDto)}
     *
     * @return ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ads comment was Updated ",
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
    @PatchMapping("{adsId}/comment/{commentId}")
    public ResponseEntity<AdsCommentDto> updateAdsComment(@PathVariable long adsId,
                                                          @PathVariable long commentId,
                                                          @RequestBody CreateAdsCommentDto adsComment
    ) {
        return new ResponseEntity<>(adsService.updateAdsComments(adsId, commentId, adsComment), HttpStatus.OK);
    }



    @GetMapping("/image/{adsImageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable long adsImageId) {
        return new ResponseEntity<>(adsService.getImage(adsImageId), HttpStatus.OK);
    }
}

