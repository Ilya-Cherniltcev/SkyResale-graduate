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
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.LoginReq;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.AuthService;

import java.util.Collection;

import static ru.skypro.homework.dto.Role.USER;

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
     * Use method of service {@link AdsService#createAds(AdsDto)}
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
    public ResponseEntity<AdsDto> addAds(@RequestBody AdsDto adsDto) {
        return new ResponseEntity<>(adsService.createAds(adsDto), HttpStatus.CREATED);
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
     * get comment from ads
     * Use method of service {@link AdsService#getAdsComments(String)}
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
    @GetMapping("{ad_pk}/comment")
    public ResponseEntity<String> getAdsComments(@PathVariable String ad_pk) {
        return new ResponseEntity<>(adsService.getAdsComments(ad_pk), HttpStatus.OK);
    }

    /**
     * Create comment to ads
     * Use method of service {@link AdsService#createAdsComments(long, String)}
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
    public ResponseEntity<AdsDto> addAdsComment(@PathVariable long id, @RequestBody String adsComment) {
        return new ResponseEntity<>(adsService.createAdsComments(id, adsComment), HttpStatus.CREATED);
    }

    /**
     * Delete comment from ads by adsId
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
    @DeleteMapping("{ad_pk}/comment{id}")
    public ResponseEntity<AdsDto> deleteAdsComment(@PathVariable String ad_pk, @PathVariable long id) {
        return new ResponseEntity<>(adsService.deleteAdsComments(ad_pk, id), HttpStatus.OK);
    }

    /**
     * Update ads comment
     * Use method of service {@link AdsService#updateAdsComments(long, String)}
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
    @PatchMapping("{id}/comment")
    public ResponseEntity<AdsDto> updateAdsComment(@PathVariable long id, @RequestBody String adsComment) {
        return new ResponseEntity<>(adsService.updateAdsComments(id, adsComment), HttpStatus.OK);
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
    public ResponseEntity<AdsDto> removeAds(@PathVariable long id) {
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
    public ResponseEntity<AdsDto> getAds(@PathVariable long id) {
        return new ResponseEntity<>(adsService.getAds(id), HttpStatus.OK);
    }

    /**
     * Update ads
     * Use method of service {@link AdsService#updateAds(AdsDto)}
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
    public ResponseEntity<AdsDto> updateAdsComment(@RequestBody AdsDto adsDto) {
        return new ResponseEntity<>(adsService.updateAds(adsDto), HttpStatus.OK);
    }

}

