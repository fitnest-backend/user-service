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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/bmi")
@Tag(
    name = "BMI Calculator",
    description = "Publik BMI hesablayıcı endpointi. Bu endpoint istifadəçilərə boy, çəki, doğum tarixi və cinsə əsaslanaraq BMI (Bədən Kütlə İndeksi) hesablamasına imkan verir. Nəticə olaraq BMI dəyəri və kateqoriyası qaytarılır."
)
public class BmiCalculatorController {
    private final UserProfileService userProfileService;
    public BmiCalculatorController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Operation(
        summary = "BMI hesablayın",
        description = """
        Bu endpoint istifadəçinin boyu (sm), çəkisi (kq), doğum tarixi və cinsinə əsaslanaraq BMI (Bədən Kütlə İndeksi) hesablayır.

        Xüsusiyyətlər:
        - Yalnız pozitiv və düzgün formatda dəyərlər qəbul edilir
        - BMI dəyəri və kateqoriyası (Underweight, Normal weight, Overweight, Obesity) qaytarılır
        - Doğum tarixi və cins gələcəkdə əlavə analizlər üçün istifadə oluna bilər

        Qeyd:
        - Boy və çəki sıfırdan böyük olmalıdır, əks halda xəta qaytarılır
        - Nəticə JSON formatında təqdim olunur
        """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "BMI uğurla hesablandı",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BmiCalculatorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Daxil edilən məlumatlar yanlışdır (məsələn, mənfi və ya sıfır boy/çəki)",
            content = @Content
        )
    })
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<BmiCalculatorResponse>> calculateBmi(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "BMI hesablaması üçün tələb olunan məlumatlar",
            required = true,
            content = @Content(
                schema = @Schema(
                    implementation = BmiCalculatorRequest.class,
                    requiredProperties = {"height", "weight", "birthDate", "gender"},
                    description = "\n- height: Boy (sm, >0)\n- weight: Çəki (kq, >0)\n- birthDate: Doğum tarixi (YYYY-MM-DD)\n- gender: Cins (MALE/FEMALE)\n"
                )
            )
        )
        @RequestBody @Valid BmiCalculatorRequest request
    ) {
        BmiCalculatorResponse response = userProfileService.calculateBmi(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
