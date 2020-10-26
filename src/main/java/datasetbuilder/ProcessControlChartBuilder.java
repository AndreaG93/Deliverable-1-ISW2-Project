package datasetbuilder;


import datasetbuilder.datasources.its.IssueTrackingSystem;
import datasetbuilder.datasources.its.jira.Jira;
import datasetbuilder.datasources.vcs.VersionControlSystem;
import datasetbuilder.datasources.vcs.git.Git;
import entities.Commit;
import entities.FileCSV;
import entities.Issue;
import entities.enums.ReleaseOutputField;
import entities.project.Project;
import entities.release.Release;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ProcessControlChartBuilder {

    private final Project project;
    private Map<Integer, Release> releasesByVersionID;

    private final VersionControlSystem versionControlSystem;
    private final IssueTrackingSystem issueTrackingSystem;
    private final Map<LocalDate, Integer> processControlChartData;

    public ProcessControlChartBuilder(Project project) {

        this.project = project;

        String rootDirectory = "C://";
        String workingDirectory = rootDirectory + this.project.name;

        this.versionControlSystem = new Git(rootDirectory, workingDirectory, this.project.gitRepositoryURL);
        this.issueTrackingSystem = new Jira();
        this.processControlChartData = new HashMap<>();
    }

    public void exportDatasetAsCSV() {

        List<String> header = new ArrayList<>();
        header.add("Date");
        header.add("Number of Fixed Bugs");

        FileCSV output = new FileCSV(this.project.name + "-ProcessControlChart.csv", header);

        for (Map.Entry<LocalDate, Integer> entry : this.processControlChartData.entrySet()) {

            List<String> data = new ArrayList<>();
            data.add(entry.getKey().toString());
            data.add(entry.getValue().toString());

            output.write(data);
        }

        output.close();
    }

    public void collectDataset() {

        collectReleases();
        collectFixedBugs();
    }

    private void collectReleases() {

        this.releasesByVersionID = new TreeMap<>();

        for (Release release : this.issueTrackingSystem.getReleases(this.project.name)) {

            String releaseTag = (String) release.getMetadata(ReleaseOutputField.NAME);
            LocalDateTime releaseDate = (LocalDateTime) release.getMetadata(ReleaseOutputField.RELEASE_DATE);

            Commit releaseCommit = this.versionControlSystem.getCommitByTag(releaseTag);
            if (releaseCommit == null)
                releaseCommit = this.versionControlSystem.getCommitByDate(releaseDate);

            release.setCommit(releaseCommit);

            this.releasesByVersionID.put(release.getReleaseVersionID(), release);
        }
    }

    private void collectFixedBugs() {

        List<Issue> issueList = this.issueTrackingSystem.getIssues(this.project.name, this.releasesByVersionID);

        for (Issue issue : issueList) {

            Commit bugFixCommit = this.versionControlSystem.getCommitByLogMessagePattern(issue.key);
            LocalDate bugFixDate;

            if (bugFixCommit == null)
                bugFixDate = issue.resolutionDate.toLocalDate();
            else
                bugFixDate = bugFixCommit.date.toLocalDate();

            int year = bugFixDate.getYear();
            int month = bugFixDate.getMonthValue();

            LocalDate date = LocalDate.of(year, month, 1);
            this.processControlChartData.merge(date, 1, Integer::sum);
        }
    }
}
