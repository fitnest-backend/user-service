package az.fitnest.user.plan.workout.adapter.api;

import az.fitnest.user.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workout-plans")
@RequiredArgsConstructor
public class WorkoutPlanController {
    
    // Stub functionality to match existing empty controller or potential needs
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Object>>> getWorkoutPlans() {
        return ResponseEntity.ok(ApiResponse.success(Collections.emptyList()));
    }
}
