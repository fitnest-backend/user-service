package az.fitnest.user.workout.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
@Tag(name = "Workout", description = "Endpoints for workout plans")
public class WorkoutPlanController {

}
