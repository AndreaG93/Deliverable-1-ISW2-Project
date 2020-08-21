package entities.project;

public class Geode extends Project {

    private static final String PROJECT_NAME = "GEODE";
    private static final String PROJECT_REPOSITORY_URL = "https://github.com/apache/geode";

    public Geode() {
        super(PROJECT_NAME, PROJECT_REPOSITORY_URL);
    }
}

