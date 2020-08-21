package launchers;

import datasetbuilder.ProcessControlChartBuilder;
import entities.project.Geode;
import entities.project.Project;

import java.util.ArrayList;
import java.util.List;

public class Launcher {

    public static void main(String[] args) {

        List<Project> projectList = new ArrayList<>();
        projectList.add(new Geode());

        for (Project project : projectList) {

            ProcessControlChartBuilder builder = new ProcessControlChartBuilder(project);

            builder.collectDataset();
            builder.exportDatasetAsCSV();
        }
    }
}