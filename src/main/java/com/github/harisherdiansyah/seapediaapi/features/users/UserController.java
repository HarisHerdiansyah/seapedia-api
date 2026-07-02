package com.github.harisherdiansyah.seapediaapi.features.users;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile-summary")
    public ResponseEntity<?> getProfileSummary() {
        ProfileSummaryResponseDTO summaryResponseDTO = userService.getProfileSummary();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Profile retrieved successfully.", summaryResponseDTO));
    }
}
