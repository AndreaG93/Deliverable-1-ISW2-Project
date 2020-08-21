package datasetbuilder.datasources.vcs;

import entities.Commit;


import java.time.LocalDateTime;


public interface VersionControlSystem {

    Commit getCommitByTag(String tag);

    Commit getCommitByDate(LocalDateTime date);

    Commit getCommitByLogMessagePattern(String pattern);
}