package az.fitnest.userservice.service.impl;

import az.fitnest.userservice.dto.request.UpdateBodyRequest;
import az.fitnest.userservice.dto.request.UpdateGoalsRequest;
import az.fitnest.userservice.exception.BaseException;
import az.fitnest.userservice.repository.UserProfileRepository;
import az.fitnest.userservice.service.GoalsService;
import az.fitnest.userservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalsServiceImpl implements GoalsService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileService userProfileService;

    @Override
    public void updateMeGoals(UpdateGoalsRequest request) {
        log.info("ActionLog.updateMeGoals.start {}", request.getGoal());

        //TODO userId current userden gelecek
        var existingGoal = userProfileRepository.findByUserId(1L).orElseThrow(
                () -> new BaseException("User not found", HttpStatus.NOT_FOUND, "404") {
                }
        );

        existingGoal.setGoal(request.getGoal());
        userProfileRepository.save(existingGoal);

        log.info("ActionLog.updateMeGoals.end");
    }

    @Override
    public void updateMeBody(UpdateBodyRequest request) {
        // TODO Auto-generated method stub

    }

}
