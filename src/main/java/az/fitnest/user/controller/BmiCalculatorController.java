package az.fitnest.user.controller;

import az.fitnest.user.dto.ApiResponse;
import az.fitnest.user.dto.request.BmiCalculatorRequest;
import az.fitnest.user.dto.response.BmiCalculatorResponse;
import az.fitnest.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bmi")
@Tag(name = "BMI Calculator", description = "Publik BMI hesablayıcı endpointi")
public class BmiCalculatorController {
    private final UserProfileService userProfileService;
    public BmiCalculatorController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    @Operation(summary = "BMI hesablayın", description = "Boy, çəki, doğum tarixi və cinsə görə BMI hesablayır.")
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<BmiCalculatorResponse>> calculateBmi(@RequestBody @Valid BmiCalculatorRequest request) {
        BmiCalculatorResponse response = userProfileService.calculateBmi(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
