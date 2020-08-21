package datasetbuilder.datasources.vcs.git;

import datasetbuilder.datasources.vcs.VersionControlSystem;
import entities.Commit;
import utilis.external.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Git implements VersionControlSystem {

    private static final String ISO_DATE = "--date=iso-strict";
    private final ExternalApplication gitApplication;


    public Git(String rootDirectoryPath, String workingDirectoryPath, String repositoryURL) {

        java.io.File workingDirectory = new java.io.File(workingDirectoryPath);

        if (!workingDirectory.isDirectory()) {

            ExternalApplication git = new ExternalApplication("git", rootDirectoryPath);
            git.execute("clone", "--quiet", repositoryURL, workingDirectoryPath);
        }

        this.gitApplication = new ExternalApplication("git", workingDirectoryPath);
    }

    @Override
    public Commit getCommitByTag(String tag) {

        ExternalApplicationOutputListMaker gitOutputReader = new ExternalApplicationOutputListMaker();

        this.gitApplication.execute(gitOutputReader, "show-ref", "-s", tag);

        List<String> gitOutput = gitOutputReader.output;

        if (gitOutput.size() > 1)
            return null;
        else if (gitOutput.size() == 1)
            return getCommitByHash(gitOutput.get(0));
        else {

            ExternalApplicationOutputListMaker reader = new ExternalApplicationOutputListMaker();

            this.gitApplication.execute(reader, "tag");

            for (String outputTag : reader.output)
                if (outputTag.contains(tag))
                    return getCommitByTag(outputTag);

            return null;
        }
    }

    @Override
    public Commit getCommitByDate(LocalDateTime date) {

        GitCommitGetter gitOutputReader = new GitCommitGetter();

        this.gitApplication.execute(gitOutputReader, "log", ISO_DATE, "--before=" + date.toString(), "--max-count=1", "--pretty=format:\"%H<->%cd\"");

        return gitOutputReader.output;
    }

    @Override
    public Commit getCommitByLogMessagePattern(String pattern) {

        GitCommitGetter gitOutputReader = new GitCommitGetter();

        this.gitApplication.execute(gitOutputReader, "log", ISO_DATE, "--grep=\"" + pattern + "\"", "--max-count=1", "--pretty=format:\"%H<->%cd\"");

        return gitOutputReader.output;
    }

    private Commit getCommitByHash(String commitHash) {

        ExternalApplicationOutputListMaker gitOutputReader = new ExternalApplicationOutputListMaker();

        this.gitApplication.execute(gitOutputReader, "show", ISO_DATE, "--format=\"%cd\"", "-s", commitHash);

        List<String> gitOutput = gitOutputReader.output;
        LocalDateTime commitLocalDateTime = LocalDateTime.parse(gitOutput.get(gitOutput.size() - 1), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return new Commit(commitHash, commitLocalDateTime);
    }
}