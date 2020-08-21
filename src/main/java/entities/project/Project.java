package entities.project;

public class Project {

    public final String name;
    public final String gitRepositoryURL;

    public Project(String name, String gitRepositoryURL) {

        this.name = name;
        this.gitRepositoryURL = gitRepositoryURL;
    }
}
