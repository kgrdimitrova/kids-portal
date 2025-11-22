package com.portal.kids.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/trainings")
public class TrainingController {

//    private final TrainingService trainingService;
//
//    public TrainingController(TrainingService trainingService) {
//        this.trainingService = trainingService;
//    }
//
//    @GetMapping("/{clubId}")
//    public ModelAndView getClubTrainings(@PathVariable UUID clubId){
//
//        List<Event> trainingEvents = trainingService.getTrainingsByClubId(clubId);
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("training-schedule");
//        return modelAndView;
//    }
}
