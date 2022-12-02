/**
 * COMP90041, Sem2, 2022: Final Project
 * @author
 * Name: Shiming ZHENG
 * Email: shimzheng@student.unimelb.edu.au
 * Student number: 1149897
 */

/**
 * A defined class for InvalidDataFormatException
 */
public class InvalidDataFormatException extends Exception{

    /**
     * the default constructor of the InvalidDataFormatException class
     */
    public InvalidDataFormatException(){
    }

    /**
     * Capture the exception when encountering the invalid data format.
     *
     * @param filename the name of the file
     * @param count the index of the line
     */
    public InvalidDataFormatException(String filename, int count){

        if (filename.equals("jobs.csv")){
            System.out.println("WARNING: invalid data format in jobs file in line " + count);

        } else if (filename.equals("applications.csv")){
            System.out.println("WARNING: invalid data format in applications file in line "
                    + count);

        }

    }

}
