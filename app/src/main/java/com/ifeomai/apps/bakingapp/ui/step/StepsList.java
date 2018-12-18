package com.ifeomai.apps.bakingapp.ui.step;

import com.ifeomai.apps.bakingapp.data.model.Step;

import java.util.List;

public class StepsList {
    private static List<Step> steps;

    public static void setList(List<Step> stepsList) {
        steps = stepsList;
    }

    public static List<Step> getSteps() {
        return steps;
    }
}

