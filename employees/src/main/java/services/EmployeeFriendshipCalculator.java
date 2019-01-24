package services;

import entities.Friendship;
import entities.ProjectExperience;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class EmployeeFriendshipCalculator {

    /**
     * Converts the list of all employee's experiences into pairs that have worked together.
     */
    public static List<Friendship> convertProjectExperiencesToFriendships(List<ProjectExperience> projectExperiences) {
        List<Friendship> friendships = new ArrayList<>();

        for (ProjectExperience employeeExperience : projectExperiences) {
            for (ProjectExperience buddyProjectExperience : projectExperiences) {
                if (buddyProjectExperience.getEmployeeId().equals(employeeExperience.getEmployeeId())) { // if the employee is itself skip
                    continue;
                }

                if (!employeeExperience.getProjectId().equals(buddyProjectExperience.getProjectId())) { // if the employees have not worked on the same project skip
                    continue;
                }

                Friendship friendship = new Friendship();

                friendship.setEmployeeExperience(employeeExperience);
                friendship.setBuddyExperience(buddyProjectExperience);
                friendship = findIfBuddies(friendship); // checks if the employees have worked together during the same period

                if (friendship != null) {
                    friendships.add(friendship);
                }
            }
        }

        return friendships;
    }

    /**
     * Checks if the employees have actually worked together during the same time period.
     */
    private static Friendship findIfBuddies(Friendship friendship) {
        Date dateEmployeeFrom = friendship.getEmployeeExperience().getDateFrom();
        Date dateEmployeeTo = friendship.getEmployeeExperience().getDateTo();
        Date dateBuddyFrom = friendship.getBuddyExperience().getDateFrom();
        Date dateBuddyTo = friendship.getBuddyExperience().getDateTo();

        if (dateEmployeeTo.getTime() <= dateBuddyFrom.getTime() || dateBuddyTo.getTime() <= dateEmployeeFrom.getTime()) { // condition for overlap
            return null; // not buddies
        }

        // checks which case of overlap it is and sets the time period correctly
        if (dateEmployeeFrom.getTime() <= dateBuddyFrom.getTime()) {
            friendship.setDateFrom(dateBuddyFrom);
        } else {
            friendship.setDateFrom(dateEmployeeFrom);
        }


        if (dateEmployeeTo.getTime() <= dateBuddyTo.getTime()) {
            friendship.setDateTo(dateEmployeeTo);
        } else {
            friendship.setDateTo(dateBuddyTo);
        }

        return friendship;
    }

    /**
     * Converts the given List of Friendships to a Map of Friendships and their "project friendship id'
     */
    public static Map<String, Friendship> findFriendshipsDurationsPerProject(List<Friendship> allFriendships) {
        Map<String, Friendship> friendships = new HashMap<>();

        for (Friendship friendship : allFriendships) {
            Long duration = 0L;

            if(friendships.containsKey(friendship.getFriendshipIdReversedForProject())) { // if the friendship exists in a reverse pair skip
                continue;
            }

            if (friendships.containsKey(friendship.getFriendshipIdForProject())) {
                duration = friendships.get(friendship.getFriendshipIdForProject()).getDuration(); // if there is already data for these employees and project add it to the current one
            }

            friendship.setDuration(duration + getFriendshipInDays(friendship));
            friendships.put(friendship.getFriendshipIdForProject(), friendship);
        }

        return friendships;
    }

    /**
     * Extracts the duration of the Friendship and converts it from milliseconds to days.
     */
    private static Long getFriendshipInDays(Friendship friendship) {
        return (friendship.getDateTo().getTime() - friendship.getDateFrom().getTime()) / (1000 * 60 * 60 * 24); // nothing magic about these numbers
    }

    /**
     * Provides a List of the longest Friendships ids and their duration in days.
     */
    public static List<Pair<String, Long>> findBestFriendships(List<Friendship> friendships) {
        Map<String, Long> friendshipDurations = new HashMap<>();

        // first sum all durations for all pairs of employees
        for (Friendship friendship : friendships) {
            Long duration = 0L;

            if (friendshipDurations.containsKey(friendship.getFriendshipId())) {
                duration = friendshipDurations.get(friendship.getFriendshipId());
            }

            duration += friendship.getDuration();
            friendshipDurations.put(friendship.getFriendshipIdForProject(), duration);
        }

        // sort them
        List<Pair<String, Long>> sortedFriendShips = friendshipDurations.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        List<Pair<String, Long>> bestFriendships = new ArrayList<>();

        bestFriendships.add(sortedFriendShips.get(sortedFriendShips.size() - 1));

        // take the longest one and all equal to its length and return them
        for (int i = sortedFriendShips.size() - 2; i >= 0; i--) {
            if (sortedFriendShips.get(i).getValue().equals(bestFriendships.get(0).getValue())) {
                bestFriendships.add(sortedFriendShips.get(i));
            } else {
                break;
            }
        }

        return bestFriendships;
    }
}


