package org.hrd.finalprojectmuseum.controller.museum_owner;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.ApiResponse;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.service.GuideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/guide")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

//    @GetMapping()
//    public ResponseEntity<ApiResponse<ListResponse<>>> getGuide() {}
}
