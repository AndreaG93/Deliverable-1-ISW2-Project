package datasetbuilder.datasources.its;

import entities.Issue;
import entities.release.Release;

import java.util.List;
import java.util.Map;

public interface IssueTrackingSystem {

    List<Release> getReleases(String projectName);

    List<Issue> getIssues(String projectName, Map<Integer, Release> releasesByVersionID);
}