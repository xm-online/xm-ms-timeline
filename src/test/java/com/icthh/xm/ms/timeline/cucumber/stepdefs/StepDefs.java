package com.icthh.xm.ms.timeline.cucumber.stepdefs;

import com.icthh.xm.ms.timeline.TimelineApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = TimelineApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
