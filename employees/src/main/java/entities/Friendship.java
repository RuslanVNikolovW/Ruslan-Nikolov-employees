package entities;

import java.util.Date;

/**
 * Represents the working relationship of two employees that have worked on the same projects at the same time.
 */
public class Friendship {

    /**
     * The period of work of the first employee.
     */
    private ProjectExperience employeeExperience;

    /**
     * The period of work by the second employee.
     */
    private ProjectExperience buddyExperience;

    /**
     * The start of the overlap between both employees' work on the project.
     */
    private Date dateFrom;

    /**
     * The end of the overlap between both employees' work on the project.
     */
    private Date dateTo;

    /**
     * The duration of all overlapping work between the two employees.
     */
    private Long duration;

    public ProjectExperience getBuddyExperience() {
        return buddyExperience;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public Long getDuration() {
        return duration;
    }

    public ProjectExperience getEmployeeExperience() {
        return employeeExperience;
    }

    public void setBuddyExperience(ProjectExperience buddyExperience) {
        this.buddyExperience = buddyExperience;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setEmployeeExperience(ProjectExperience employeeExperience) {
        this.employeeExperience = employeeExperience;
    }

    /**
     * A concatenation between the two employees' ids i.e. a way to identify a pair of employees.
     */
    public String getFriendshipId() {
        return employeeExperience.getEmployeeId() + " and " + buddyExperience.getEmployeeId();
    }

    /**
     * A concatenation between the project's id which was worked by both employees and their friendship id.
     */
    public String getFriendshipIdForProject() {
        return getProjectId() + ": " + getFriendshipId();
    }

    /**
     * A concatenation between the project's id which was worked by both employees and their reversed friendship id.
     */
    public String getFriendshipIdReversedForProject() {
        return getProjectId() + ": " + getFriendshipIdReversed();
    }

    /**
     * The friendship's id where the first employee is the second and vice versa.
     */
    public String getFriendshipIdReversed() {
        return buddyExperience.getEmployeeId() + " and " + employeeExperience.getEmployeeId();
    }

    public String getProjectId() {
        return employeeExperience.getProjectId();
    }
}
