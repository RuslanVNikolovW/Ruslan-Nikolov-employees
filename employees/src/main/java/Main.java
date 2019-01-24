import entities.Friendship;
import entities.ProjectExperience;
import javafx.util.Pair;
import services.EmployeeFriendshipCalculator;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final String SPLITTER = ",";
    private static final String TODAY_DATE = "null";
    private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_INPUT_FILE_NAME = "employees.txt";

    public static void main(String[] args) {


        Scanner scan= new Scanner(System.in);

        System.out.println("Please enter date format pattern or press enter for default \"yyyy-MM-dd\":");
        String dateFormatPattern = scan.nextLine();
        System.out.println("Please enter input file name or press enter to use the default one \"employees.txt\":");
        String fileName = scan.nextLine();

        printBestBuddies(dateFormatPattern, fileName);
    }

    private static void printBestBuddies(String dateFormatPattern, String fileName) {
        if(dateFormatPattern == null || dateFormatPattern.trim().isEmpty()) {
            dateFormatPattern = DEFAULT_DATE_FORMAT_PATTERN;
        }

        if(fileName == null || fileName.trim().isEmpty()) {
            fileName = DEFAULT_INPUT_FILE_NAME;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        ClassLoader classLoader = Main.class.getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        List<ProjectExperience> allProjectExperiences = new ArrayList<>();

        readDataFromFile(dateFormat, file, allProjectExperiences);

        List<Friendship> friendships = EmployeeFriendshipCalculator.convertProjectExperiencesToFriendships(allProjectExperiences); // first find all employees who have worked together
        Map<String, Friendship> friendshipsPerProjects = EmployeeFriendshipCalculator.findFriendshipsDurationsPerProject(friendships); // used for UI
        List<Pair<String, Long>> bestFriendships = EmployeeFriendshipCalculator.findBestFriendships(new ArrayList<>(friendshipsPerProjects.values())); // find the pairs of employees who have worked together the most

        System.out.println("Pair(s) of employees that have spent most time together on projects:\n");

        for (Pair<String, Long> friends : bestFriendships) {
            System.out.println("Employees with ids " + friends.getKey() + " : " + friends.getValue() + " days;");
        }
    }

    private static void readDataFromFile(SimpleDateFormat dateFormat, File file, List<ProjectExperience> allProjectExperiences) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] params = line.split(SPLITTER);

                if (params.length < 4) {
                    continue;
                }

                String employeeId = params[0].trim();
                String projectId = params[1].trim();
                String dateFromString = params[2].trim();
                String dateToString = params[3].trim();
                Date dateFrom = dateFormat.parse(dateFromString);
                Date dateTo;

                if (dateToString == null || dateToString.equalsIgnoreCase(TODAY_DATE)) {
                    dateTo = new Date();
                } else {
                    dateTo = dateFormat.parse(dateToString);
                }

                ProjectExperience projectExperience = new ProjectExperience();

                projectExperience.setEmployeeId(employeeId);
                projectExperience.setDateFrom(dateFrom);
                projectExperience.setDateTo(dateTo);
                projectExperience.setProjectId(projectId);

                allProjectExperiences.add(projectExperience);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
