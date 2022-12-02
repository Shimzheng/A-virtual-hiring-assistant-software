/**
 * COMP90041, Sem2, 2022: Final Project
 * @author
 * Name: Shiming ZHENG
 * Email: shimzheng@student.unimelb.edu.au
 * Student number: 1149897
 */

/**
 * A defined class for InvalidCharacteristicException
 */
public class InvalidCharacteristicException extends Exception{

    /**
     * the default constructor of the InvalidCharacteristicException class
     */
    public InvalidCharacteristicException(){
    }

    /**
     * Capture the exception when encountering the invalid characteristic.
     *
     * @param filename the name of the file
     * @param count the index of the line
     * @param manOrNot is mandatory or not
     */
    public InvalidCharacteristicException(String filename, int count, String manOrNot){

        if (filename.equals("jobs.csv")){
            System.out.println("WARNING: invalid characteristic in jobs file in line " + count);

        } else if (filename.equals("applications.csv")){
            System.out.println("WARNING: invalid characteristic in applications file in line "
                    + count);

        }

    }

}
