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
import ru.skypro.homework.dto.AdsCommentDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.AdsImage;
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
     * Use method of service {@link AdsService#getALLAds()}
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
    public ResponseEntity<Collection<AdsDto>> getALLAds() {
        return new ResponseEntity<>(adsService.getALLAds(), HttpStatus.OK);
    }


    /**
     * Create new ads
     * Use method of service {@link AdsService#createAds(CreateAdsDto, AdsImage)}
     *
     * @return ads
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Create a new ads",
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
    public ResponseEntity<AdsDto> addAds(@RequestBody CreateAdsDto adsDto, AdsImage image) {
        return new ResponseEntity<>(adsService.createAds(adsDto, image), HttpStatus.CREATED);
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
     * Use method of service {@link AdsService#createAdsComments(long, AdsCommentDto)} (long, AdsCommentDto)}
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
    @PostMapping("{id}/comment")
    public ResponseEntity<AdsCommentDto> addAdsComment(@PathVariable long id, @RequestBody AdsCommentDto adsComment) {
        return new ResponseEntity<>(adsService.createAdsComments(id, adsComment), HttpStatus.CREATED);
    }

    /**
     * Delete comment from ads by adsId and comment's id
     * Use method of service {@link AdsService#deleteAdsComments(String, long)}
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

    @DeleteMapping("{adsId}/comment{id}")
    public ResponseEntity<AdsDto> deleteAdsComment(@PathVariable String adsId, @PathVariable long id) {
        return new ResponseEntity<>(adsService.deleteAdsComments(adsId, id), HttpStatus.OK);
    }

    /**
     * Update ads comment
     * Use method of service {@link AdsService#updateAdsComments(String, long, AdsCommentDto)}
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
    @PatchMapping("{adsId}/comment{id}/comment")
    public ResponseEntity<AdsCommentDto> updateAdsComment(@PathVariable String adsId,
                                                          @PathVariable long commId,
                                                          @RequestBody AdsCommentDto adsComment
    ) {
        return new ResponseEntity<>(adsService.updateAdsComments(adsId, commId, adsComment), HttpStatus.OK);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Ads> removeAds(@PathVariable long id) {
        return new ResponseEntity<>(adsService.removeAds(id), HttpStatus.OK);
    }

    /**
     * get ads by id
     * Use method of service {@link AdsService#getAds(long)}
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
    @GetMapping("/{id}")
    public ResponseEntity<Ads> getAds(@PathVariable long id) {
        return new ResponseEntity<>(adsService.getAds(id), HttpStatus.OK);
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
    @PatchMapping
    public ResponseEntity<AdsDto> updateAdsComment(
            @PathVariable long id,
            @RequestBody CreateAdsDto adsDto) {
        return new ResponseEntity<>(adsService.updateAds(id, adsDto), HttpStatus.OK);
    }

}

