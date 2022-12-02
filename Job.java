/**
 * COMP90041, Sem2, 2022: Final Project
 * @author
 * Name: Shiming ZHENG
 * Email: shimzheng@student.unimelb.edu.au
 * Student number: 1149897
 */

/**
 * The class for current job
 */
public class Job {

    /**
     * job's position title
     */
    private String positionTitle;

    /**
     * job's position description
     */
    private String positionDescription;

    /**
     * job's minimum degree requirement
     */
    private String minDegreeRequirement;

    /**
     * job's salary
     */
    private int salary;

    /**
     * job's start date
     */
    private String startDate;

    /**
     * the default constructor of the Job class
     */
    public Job() {
    }

    //Getter

    /**
     * get the job's position title
     *
     * @return the job's position title
     */
    public String getPositionTitle() {
        return positionTitle;
    }

    /**
     * get the job's position description
     *
     * @return the job's position description
     */
    public String getPositionDescription() {
        return positionDescription;
    }

    /**
     * get the job's minimum degree requirement
     *
     * @return the job's minimum degree requirement
     */
    public String getMinDegreeRequirement() {
        return minDegreeRequirement;
    }

    /**
     * get the job's salary
     *
     * @return the job's salary
     */
    public int getSalary() {
        return salary;
    }

    /**
     * get the job's start date
     *
     * @return the job's start date
     */
    public String getStartDate() {
        return startDate;
    }

    //Setter
    /**
     * set the job's position title
     *
     * @param positionTitle the job's position title
     */
    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    /**
     * set the job's position description
     *
     * @param positionDescription the job's position description
     */
    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    /**
     * set the job's minimum degree requirement
     *
     * @param minDegreeRequirement the job's minimum degree requirement
     */
    public void setMinDegreeRequirement(String minDegreeRequirement) {
        this.minDegreeRequirement = minDegreeRequirement;
    }

    /**
     * set the job's salary
     *
     * @param salary the job's salary
     */
    public void setSalary(int salary) {
        this.salary = salary;
    }

    /**
     * set the job's start date
     *
     * @param startDate the job's start date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

}
