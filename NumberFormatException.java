/**
 * COMP90041, Sem2, 2022: Final Project
 * @author
 * Name: Shiming ZHENG
 * Email: shimzheng@student.unimelb.edu.au
 * Student number: 1149897
 */

/**
 * A defined class for NumberFormatException
 */
public class NumberFormatException extends Exception{

    /**
     * the default constructor of the NumberFormatException class
     */
    public NumberFormatException(){
    }

    /**
     * Capture the exception when encountering the invalid number format.
     *
     * @param filename the name of the file
     * @param count the index of the line
     * @param manOrNot is mandatory or not
     */
    public NumberFormatException(String filename, int count, String manOrNot) {

        if (filename.equals("jobs.csv")) {
            System.out.println("WARNING: invalid number format in jobs file in line " + count);

        } else if (filename.equals("applications.csv")) {
            System.out.println("WARNING: invalid number format in applications file in line "
                    + count);

        }

    }

}
