package com.packrobot;

import com.beust.jcommander.Parameter;

public class PackRobotRunnerArgs {

    @Parameter(names = "--config", description = "Task Runner Application Context Configuration XML File , must in spring folder")
    public String config;


}
