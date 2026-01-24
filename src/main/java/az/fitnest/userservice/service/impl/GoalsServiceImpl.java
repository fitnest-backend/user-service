package az.fitnest.userservice.service.impl;

import az.fitnest.userservice.dto.request.UpdateBodyRequest;
import az.fitnest.userservice.dto.request.UpdateGoalsRequest;
import az.fitnest.userservice.repository.UserProfileRepository;
import az.fitnest.userservice.service.GoalsService;
import az.fitnest.userservice.service.UserProfileService;
//import az.fitnest.userservice.util.UserContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalsServiceImpl implements GoalsService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileService userProfileService;
//    private final UserContextUtil userContextUtil;

    @Override
    public void updateMeGoals(UpdateGoalsRequest request) {
        log.info("ActionLog.updateMeGoals.start {}", request.getGoal());
//        var me = userContextUtil.getCurrentUserId();

        var existingGoal = userProfileRepository.findByUserId(1L);
        if (existingGoal.isPresent()) {
            var goal = existingGoal.get();
            goal.setGoal(request.getGoal());
            userProfileRepository.save(goal);
        }
        log.info("ActionLog.updateMeGoals.end");
    }

    @Override
    public void updateMeBody(UpdateBodyRequest request) {
        // TODO Auto-generated method stub

    }

}
