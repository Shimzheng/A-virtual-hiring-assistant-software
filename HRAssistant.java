import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * COMP90041, Sem2, 2022: Final Project
 * @author
 * Name: Shiming ZHENG
 * Email: shimzheng@student.unimelb.edu.au
 * Student number: 1149897
 */

/**
 * This class contains the logic for the HRAssistant engine
 */
public class HRAssistant {

    /**
     * the default constructor of the HRAssistant class
     */
    public HRAssistant() {
    }

    /**
     * engine for HRAssistant
     *
     * @param args an array contains the arguments in command-line
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        HRAssistant hrAssistant = new HRAssistant();

        String role = null;
        String applicationsFilename = null;
        String jobsFilename = null;

        // help flag
        if (args.length == 1) {

            //help command
            if (args[0].equals("--help") || args[0].equals("--h")) {

                displayWelcomeMessage("help.ascii");

            } else if (args[0].equals("--role") || args[0].equals("-r")) {

                System.out.println("ERROR:no role defined.");

            } else {

                displayWelcomeMessage("help.ascii");

            }
        }
        // 1 flag + 1 argument or 2 flags + 2 arguments or 3 flags + 3 arguments
        else if (args.length % 2 == 0){

            for (int i = 0; i < args.length; i++){

                if(args[i].equals("--role") || args[i].equals("-r")){
                    role = args[i+1];

                }else if (args[i].equals("--applications") || args[i].equals("-a")){
                    applicationsFilename = args[i+1];

                }else if (args[i].equals("--jobs") || args[i].equals("-j")){
                    jobsFilename = args[i+1];
                }
            }

            // not specify role
            if (role == null){

                displayWelcomeMessage("help.ascii");

            }

        }else{

            if (args[args.length-1].equals("--role") || args[args.length-1].equals("-r")) {

                System.out.println("ERROR:no role defined.");

            } else {

                displayWelcomeMessage("help.ascii");

            }

        }

        // new applicant
        Applicant applicant = new Applicant();

        // new job
        Job job = new Job();

        // num of applicants has selected the job
        int applicantNum = fileRowNum("applicantWithJobSelect.csv");

        // num of applications that each applicant has selected
        int applicationNum_N = hrAssistant.countTotalNumApplication(
                "applicantWithJobSelect.csv", applicantNum);

        //Role
        if (role != null) {

            if (role.equals("applicant")) {

                // application portal(AP) welcome text
                displayWelcomeMessage("welcome_applicant.ascii");

                //num of jobs
                int jobNum_N = 0;

                if (jobsFilename == null){
                    jobsFilename = "jobs.csv"; //default
                }

                jobNum_N = fileRowNum(jobsFilename);

                //num of applicants
                int appNum = 0;
                if (applicationsFilename != null){
                    appNum = fileRowNum(applicationsFilename);

                }

                // AP's main menu
                //num of applications
                int applicationNum_M = 0;

                //display the main menu with 'create' command
                hrAssistant.displayMainMenuWithCreate(jobNum_N, applicationNum_M);

                // boolean used to record: applicant has created the profile / not
                boolean profileCreateDone = false;

                // Used to record all the job choices for this applicant
                ArrayList<String> userSelectRecord = new ArrayList<>();

                // All User Commands in AP
                while(true) {
                    String userCommand = scanner.nextLine();

                    // 1) create a new applicant
                    if (userCommand.equals("create") || userCommand.equals("c") ){

                        hrAssistant.createApplication(scanner, applicant, job);

                        //append new applicant's data into application.csv
                        hrAssistant.addNewRecordToCSV("application",
                                "applications.csv", applicant, job, userSelectRecord);
                        profileCreateDone = true;

                        //return main menu without 'create' command
                        hrAssistant.displayMainMenuWithCreateNot(jobNum_N, applicationNum_M);

                        // 2) list available jobs
                    }else if (userCommand.equals("jobs") || userCommand.equals("j") ){

                        if (jobNum_N == 0){ // no jobs
                            System.out.println("No jobs available.");

                            // judge applicant has created the profile / not
                            if (!profileCreateDone) {
                                //return main menu with 'create' command
                                hrAssistant.displayMainMenuWithCreate(jobNum_N,
                                        applicationNum_M);

                            } else {
                                //return main menu without 'create' command
                                hrAssistant.displayMainMenuWithCreateNot(jobNum_N,
                                        applicationNum_M);

                            }

                        } else {
                            // list the jobs
                            hrAssistant.listJob("jobs.csv", jobNum_N, userSelectRecord);

                            // judge applicant has created the profile / not
                            if (!profileCreateDone) {

                                //return main menu with 'create' command
                                hrAssistant.displayMainMenuWithCreate(jobNum_N, applicationNum_M);

                            } else {

                                // Apply the jobs
                                // save the job selection in the array list: userSelectRecord
                                hrAssistant.applyJob(scanner, jobNum_N, userSelectRecord);

                            }
                        }

                        // 3) quit
                    }else if (userCommand.equals("quit") || userCommand.equals("q") ){

                        if (userSelectRecord.size() != 0) {

                            // Append new record (applicant's info + job selection string)
                            // into applicantWithJobSelect.csv
                            hrAssistant.addNewRecordToCSV("applicant's job selection",
                                    "applicantWithJobSelect.csv",
                                    applicant, job, userSelectRecord);

                        }

                        break;

                        // 4) error warning
                    }else {
                        System.out.println("Invalid input! Please enter a valid command to continue:");
                        System.out.print("> ");

                    }

                }


            } else if (role.equals("hr")) {

                // hiring assistant(HA) welcome text
                displayWelcomeMessage("welcome_hr.ascii");

                //num of jobs
                int jobNum_N = 0;

                if (jobsFilename == null){
                    jobsFilename = "jobs.csv"; //default

                }

                jobNum_N = fileRowNum(jobsFilename);

                //num of applicants
                int appNum = 0;

                if (applicationsFilename != null){
                    appNum = fileRowNum(applicationsFilename);

                }

                // HA's main menu
                //display the HA's main menu with 'create' command
                hrAssistant.displayHRMainMenuWithCreate(applicationNum_N);

                // All User Commands in HA
                while(true) {
                    String userCommand = scanner.nextLine();

                    // 1) create a new job
                    if (userCommand.equals("create") || userCommand.equals("c")){

                        hrAssistant.createJob(scanner, job, applicant);

                        // append new job's data into job.csv
                        ArrayList<String> arrList = new ArrayList<>();
                        hrAssistant.addNewRecordToCSV("job","jobs.csv",
                                applicant, job, arrList);

                        // return HA's main menu with 'create' command
                        hrAssistant.displayHRMainMenuWithCreate(applicationNum_N);


                        // 2) list available jobs
                    }else if (userCommand.equals("jobs") || userCommand.equals("j")){

                        if (jobNum_N == 0){ // no jobs
                            System.out.println("No jobs available.");

                        } else {
                            // list available jobs with its applicant
                            hrAssistant.listJobWithApplicant(jobsFilename,
                                    "applicantWithJobSelect.csv",
                                    jobNum_N, applicantNum);

                        }

                        // return HA's main menu with 'create' command
                        hrAssistant.displayHRMainMenuWithCreate(applicationNum_N);


                        // 3) list applicants
                    } else if (userCommand.equals("applicants") || userCommand.equals("a")){

                        if (appNum == 0) { // no applicants
                            System.out.println("No applicants available.");

                        } else {
                            // list sorted and formatted applicants
                            hrAssistant.listSortedFormattedApplicant("applications.csv");

                        }

                        // return HA's main menu with 'create' command
                        hrAssistant.displayHRMainMenuWithCreate(applicationNum_N);


                        // 4) filter applications
                    } else if (userCommand.equals("filter") || userCommand.equals("f")){

                        if (appNum == 0) { // no applicants
                            System.out.println("No applicants available.");

                        } else {
                            // print search list
                            System.out.print("Filter by: [lastname], [degree] or [wam]: ");

                            // filter
                            String userSelection = scanner.nextLine();

                            if (userSelection.equals("lastname")) {
                                hrAssistant.filterFunction("applications.csv",
                                        "lastname");

                            } else if (userSelection.equals("degree")) {
                                hrAssistant.filterFunction("applications.csv",
                                        "degree");

                            } else if (userSelection.equals("wam")) {
                                hrAssistant.filterFunction("applications.csv",
                                        "wam");

                            } else {
                                System.out.print("Invalid selection!");
                            }

                        }

                        // return HA's main menu with 'create' command
                        hrAssistant.displayHRMainMenuWithCreate(applicationNum_N);


                        // 5) matchmaking
                    } else if (userCommand.equals("match") || userCommand.equals("m")){

                        if (jobNum_N == 0){ // no jobs
                            System.out.println("No jobs available.");

                        } else if (appNum == 0) { // no applicants
                            System.out.println("No applicants available.");

                        } else {
                            hrAssistant.matchFunction(jobsFilename,
                                    "applicantWithJobSelect.csv",
                                    jobNum_N, applicantNum, "hr");

                        }

                        // return HA's main menu with 'create' command
                        hrAssistant.displayHRMainMenuWithCreate(applicationNum_N);


                        // 6) quit
                    }else if (userCommand.equals("quit") || userCommand.equals("q") ) {

                        System.out.print("\n");

                        break;


                        // 7) error warning
                    } else {

                        System.out.println("Invalid input! Please enter a valid command to continue: ");
                        System.out.print("> ");

                    }


                }


            } else if (role.equals("audit")) {

                // Matchmaking Audit(MA) welcome text
                displayWelcomeMessage("welcome_audit.ascii");

                //num of jobs
                int jobNum_N = 0;

                if (jobsFilename == null){
                    jobsFilename = "jobs.csv"; //default

                }

                jobNum_N = fileRowNum(jobsFilename);

                //num of applicants
                int appNum = 0;
                if (applicationsFilename != null){
                    appNum = fileRowNum(applicationsFilename);
                }

                // check job & application file
                if (jobNum_N == 0){
                    System.out.println("No jobs available for interrogation.");

                } else { // jobNum_N != 0

                    if (appNum == 0){
                        System.out.println("No applicants available for interrogation.");

                        // 'system available'
                    } else { // appNum != 0
                        System.out.println("Available jobs: " + jobNum_N);
                        System.out.println("Total number of applicants: " + appNum);

                        // get all matched applicants list
                        ArrayList<ArrayList<String>> allMatchAppList = new ArrayList<>(
                                hrAssistant.matchFunction(jobsFilename,
                                        "applicantWithJobSelect.csv",
                                        jobNum_N, applicantNum, "auditMatch"));

                        // get all unmatched applicants list
                        ArrayList<ArrayList<String>> allUnMatchAppList = new ArrayList<>(
                                hrAssistant.matchFunction(jobsFilename,
                                        "applicantWithJobSelect.csv",
                                        jobNum_N, applicantNum, "auditUnMatch"));

                        // initialize the statistics
                        int sucMatchNum = 0;
                        String averMatchAge = "0.00";
                        String averTotalAge = "0.00";
                        String averMatchWam = "0.00";
                        String averTotalWam = "0.00";

                        String maleProportion = "0.00"; // male matched / total male
                        String femaleProportion = "0.00"; // female matched / total female
                        String otherGenderProportion = "0.00";// other gender matched / total female
                        String phdProportion = "0.00"; // PhD matched / total PhD
                        String masterProportion = "0.00"; // master matched / total master
                        String bachelorProportion = "0.00"; // bachelor matched / total bachelor


                        // calculate the statistics
                        // parameters: allMatchAppList & allUnMatchAppList

                        // 1) Calculate the sucMatchNum
                        sucMatchNum = allMatchAppList.size();

                        // 2) Calculate the averMatchAge
                        averMatchAge = hrAssistant.calculateAverMatchAge(allMatchAppList);

                        // 3) Calculate the averTotalAge
                        averTotalAge = hrAssistant.calculateAverTotalAge(allMatchAppList,
                                allUnMatchAppList);

                        // 4) Calculate the averMatchWam
                        averMatchWam = hrAssistant.calculateAverMatchWam(allMatchAppList);

                        // 5) Calculate the averTotalWam
                        averTotalWam = hrAssistant.calculateAverTotalWam(allMatchAppList,
                                allUnMatchAppList);

                        // 6) Calculate the maleProportion
                        maleProportion = hrAssistant.calculateGenderProportion(allMatchAppList,
                                allUnMatchAppList, "male");

                        // 7) Calculate the femaleProportion
                        femaleProportion = hrAssistant.calculateGenderProportion(allMatchAppList,
                                allUnMatchAppList, "female");

                        // 8) Calculate the otherGenderProportion
                        otherGenderProportion = hrAssistant.calculateGenderProportion(allMatchAppList,
                                allUnMatchAppList, "other");

                        // 9) Calculate the 3 degree Proportion
                        phdProportion = hrAssistant.calculateDegreeProportion(allMatchAppList,
                                allUnMatchAppList, "PHD");
                        masterProportion = hrAssistant.calculateDegreeProportion(allMatchAppList,
                                allUnMatchAppList, "Master");
                        bachelorProportion = hrAssistant.calculateDegreeProportion(allMatchAppList,
                                allUnMatchAppList, "Bachelor");


                        // print the statistics of the matching result
                        System.out.println("Number of successful matches: " + sucMatchNum);

                        System.out.println("Average age: " + averMatchAge
                                + " (average age of all applicants: " + averTotalAge + ")");
                        System.out.println("Average WAM: " + averMatchWam
                                + " (average WAM of all applicants: " + averTotalWam + ")");

                        System.out.println("male: " + maleProportion);
                        System.out.println("female: " + femaleProportion);
                        System.out.println("other gender: " + otherGenderProportion);

                        System.out.println("PHD: " + phdProportion);
                        System.out.println("Master: " + masterProportion);
                        System.out.println("Bachelor: " + bachelorProportion);

                    }

                }



            } else {

                System.out.println("ERROR: " + role + " is not a valid role.");

                displayWelcomeMessage("help.ascii");

            }

        }

    }

    /**
     * display welcome text
     *
     * @param filename the name of the file contained welcome message
     */
    private static void displayWelcomeMessage(String filename) {

        Scanner inputStream = null;

        try{
            inputStream = new Scanner(new FileInputStream(filename));
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Welcome File not found.");
        }

        while(inputStream.hasNextLine())
        {
            System.out.println(inputStream.nextLine());
        }
    }


    /**
     * count the row num in csv file and 3 kinds of error handling when reading the file
     *
     * @param filename the name of the file
     * @return the number of row in the file
     */
    private static int fileRowNum(String filename) {

        int count = -1;
        int lineIndex = -1;

        String eachLine = null;

        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));

            while((eachLine = br.readLine()) != null) {
                count++;
                lineIndex++;
                String[] eachNullLineStr = eachLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                try{

                    // 1) ------------- Test the invalid data format -------------

                    boolean isValidDataFormat = false;

                    isValidDataFormat = true;
                    // for jobs file
                    if (filename.equals("jobs.csv") && eachNullLineStr.length != 6){

                        if (eachNullLineStr.length == 5 && eachLine.endsWith(",")) {
                            isValidDataFormat = true;

                        } else{
                            count--;
                            throw new InvalidDataFormatException(filename, lineIndex);
                        }

                    }

                    // for applications file
                    if (filename.equals("applications.csv") && eachNullLineStr.length != 13){

                        // if the row has 13 column but the last column(availability) is n/a
                        int eachRowCommasNum = countCommasNum(eachLine);

                        if(eachRowCommasNum == 12){
                            eachLine = eachLine + "n/a";
                            eachNullLineStr = eachLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                            if (eachNullLineStr.length == 13){
                                break;
                            }

                        }

                        if (eachNullLineStr.length == 12 && eachLine.endsWith(",")) {
                            isValidDataFormat = true;

                        } else{
                            count--;
                            throw new InvalidDataFormatException(filename, lineIndex);
                        }

                    }

                    // 2) ------------- Test the invalid number format --------------------
                    // for jobs file
                    if(count >= 1) {
                        if (filename.equals("jobs.csv")) {

                            // check salary (non-mandatory)
                            if (eachNullLineStr[4].length() != 0) {
                                try {
                                    float salary = Float.parseFloat(eachNullLineStr[4]);

                                } catch (Exception e) {
                                    throw new NumberFormatException(filename, count, "no");

                                }
                            }


                        }
                    }

                    // for applications file
                    if(count >= 1) {

                        if (filename.equals("applications.csv") && isValidDataFormat) {

                            // check age (mandatory)
                            if (eachNullLineStr[4].length() == 0) {
                                count--;
                                throw new NumberFormatException(filename, lineIndex, "yes");

                            } else {

                                try {
                                    int age = Integer.parseInt(eachNullLineStr[4]);

                                } catch (Exception e) {
                                    count--; // don consider this line
                                    throw new NumberFormatException(filename, lineIndex, "yes");

                                }

                            }

                            // check 4 comp grade (non-mandatory)
                            for (int i = 7; i < 11; i++) {
                                if (eachNullLineStr[i].length() != 0) {
                                    try {
                                        float grade = Float.parseFloat(eachNullLineStr[i]);

                                    } catch (Exception e) {
                                        throw new NumberFormatException(filename, count, "no");

                                    }
                                }
                            }

                            // check salary expectation (non-mandatory)
                            if (eachNullLineStr[11].length() != 0) {
                                try {
                                    float salary = Float.parseFloat(eachNullLineStr[11]);

                                } catch (Exception e) {
                                    throw new NumberFormatException(filename, count, "no");

                                }
                            }

                        }
                    }

                    //3) ------------- Test the invalid Field Values -------------

                    // for jobs file
                    if (count >= 1){

                        if (filename.equals("jobs.csv")) {

                            // check degree (non-mandatory)
                            if (eachNullLineStr[3].length() != 0) {

                                if (!eachNullLineStr[3].equals("Bachelor")
                                        && !eachNullLineStr[3].equals("Master")
                                        && !eachNullLineStr[3].equals("PHD")) {

                                    throw new InvalidCharacteristicException(filename, count, "no");

                                }

                            }

                            // check salary (non-mandatory)
                            if (eachNullLineStr[4].length() != 0) {
                                try {
                                    float salary = Float.parseFloat(eachNullLineStr[4]);

                                    // Test the invalid Field Values
                                    if (salary < 0){
                                        throw new InvalidCharacteristicException(filename,
                                                count, "no");
                                    }

                                } catch (Exception ignored) {

                                }
                            }

                            //check start date (mandatory)
                            if (eachNullLineStr.length == 5 && eachLine.endsWith(",")) {
                                count--;
                                throw new InvalidCharacteristicException(filename,
                                        lineIndex, "yes");

                            }

                            if (eachNullLineStr.length == 6) {
                                if (eachNullLineStr[5].length() == 0) {
                                    count--;
                                    throw new InvalidCharacteristicException(filename,
                                            lineIndex, "yes");

                                }else{
                                    try {
                                        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(
                                                "dd/MM/yy", Locale.ENGLISH);
                                        LocalDate localDate = LocalDate.parse(eachNullLineStr[5],
                                                dtFormatter);
                                        String formatDate = localDate.format(dtFormatter);

                                        if (!formatDate.equals(eachNullLineStr[5])) {
                                            count--;
                                            throw new InvalidCharacteristicException(filename,
                                                    lineIndex, "yes");
                                        }

                                    } catch (Exception e) {
                                        count--;
                                        throw new InvalidCharacteristicException(filename,
                                                lineIndex, "yes");
                                    }

                                }
                            }

                        }

                    }


                    // for applications file
                    if (count >= 1){

                        if (filename.equals("applications.csv") && isValidDataFormat) {

                                // check age (mandatory)
                                if (eachNullLineStr[4].length() == 0) {
                                    count--;
                                    throw new NumberFormatException(filename, lineIndex, "yes");

                                } else {

                                    try {
                                        int age = Integer.parseInt(eachNullLineStr[4]);

                                        if (age < 18 || age > 100){
                                            count--; // don consider this line
                                            throw new InvalidCharacteristicException(filename,
                                                    lineIndex, "yes");
                                        }

                                    } catch (Exception ignored) {

                                    }

                                }

                            // check gender (non-mandatory)
                            if (eachNullLineStr[5].length() != 0) {

                                if (!eachNullLineStr[5].equals("female")
                                        && !eachNullLineStr[5].equals("male")
                                        && !eachNullLineStr[5].equals("other")) {

                                    throw new InvalidCharacteristicException(filename, count, "no");

                                }
                            }

                            // check degree (non-mandatory)
                            if (eachNullLineStr[6].length() != 0) {

                                if (!eachNullLineStr[6].equals("Bachelor")
                                        && !eachNullLineStr[6].equals("Master")
                                        && !eachNullLineStr[6].equals("PHD")) {

                                    throw new InvalidCharacteristicException(filename, count, "no");

                                }

                            }

                            // check coursework (non-mandatory)
                            for (int i = 7; i < 11; i++) {
                                if (eachNullLineStr[i].length() != 0) {
                                    try {
                                        float grade = Float.parseFloat(eachNullLineStr[i]);

                                        if (grade < 49 || grade > 100){
                                            throw new InvalidCharacteristicException(filename,
                                                    count, "no");
                                        }

                                    } catch (Exception ignored) {

                                    }
                                }
                            }

                            // check salary (non-mandatory)
                            if (eachNullLineStr[11].length() != 0) {
                                try {
                                    float salary = Float.parseFloat(eachNullLineStr[11]);

                                    if (salary < 0){
                                        throw new InvalidCharacteristicException(filename,
                                                count, "no");
                                    }

                                } catch (Exception ignored) {

                                }
                            }

                            // check availability (non-mandatory)
                            if (eachNullLineStr.length == 13) {

                                if (eachNullLineStr[12].length() != 0) {

                                    if(eachNullLineStr[12].equals("n/a")){
                                        break;
                                    }

                                    try {
                                        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(
                                                "dd/MM/yy", Locale.ENGLISH);
                                        LocalDate localDate = LocalDate.parse(eachNullLineStr[12],
                                                dtFormatter);
                                        String formatDate = localDate.format(dtFormatter);

                                        if (!formatDate.equals(eachNullLineStr[12])) {
                                            throw new InvalidCharacteristicException(filename,
                                                    count, "no");
                                        }

                                    } catch (Exception e) {
                                        throw new InvalidCharacteristicException(filename,
                                                count, "no");
                                    }

                                }
                            }

                        }

                    }

                } catch (InvalidDataFormatException
                         | InvalidCharacteristicException
                         | NumberFormatException e) {

                }

            }

        } catch (FileNotFoundException e){
            count = 0;

        } catch (IOException e) {
            System.out.println("There is an error.");

        }

        return count;

    }

    /**
     * count the total num of commas for each row in the file
     *
     * @param eachRow each line in the file
     * @return the total num of commas
     */
    private static int countCommasNum(String eachRow) {
        int count = 0;

        for(int i = 0; i < eachRow.length(); i++) {

            if(eachRow.charAt(i) == ',')
                count++;

        }

        return count;
    }

    /**
     * display the main menu with 'create' command for application portal
     *
     * @param jobNum_N the total number of jobs
     * @param applicationNum_M the number of applications this user has submitted
     */
    private void displayMainMenuWithCreate(int jobNum_N, int applicationNum_M){
//        System.out.print("\n");
        System.out.println(jobNum_N + " jobs available. " +
                applicationNum_M + " applications submitted.");
        System.out.println("Please enter one of the following commands to continue:");
        System.out.println("- create new application: [create] or [c]");
        System.out.println("- list available jobs: [jobs] or [j]");
        System.out.println("- quit the program: [quit] or [q]");
        System.out.print("> ");
    }

    /**
     * display the main menu without 'create' command for application portal
     *
     * @param jobNum_N the total number of jobs
     * @param applicationNum_M the number of applications this user has submitted
     */
    private void displayMainMenuWithCreateNot(int jobNum_N, int applicationNum_M){
//        System.out.print("\n");
        System.out.println(jobNum_N + " jobs available. " +
                applicationNum_M + " applications submitted.");
        System.out.println("Please enter one of the following commands to continue:");
        System.out.println("- list available jobs: [jobs] or [j]");
        System.out.println("- quit the program: [quit] or [q]");
        System.out.println("> ");
    }


    /**
     * create a new application
     *
     * @param scanner the command inputted by the user
     * @param applicant the current applicant
     * @param job the current job
     */
    private void createApplication(Scanner scanner, Applicant applicant, Job job){

        System.out.println("# Create new Application");

        //1) Lastname
        System.out.print("Lastname: ");
        String userLastname = scanner.nextLine();
        //check empty input for lastname
        checkLastname(userLastname, scanner, applicant);

        //2) Firstname
        System.out.print("Firstname: ");
        String userFirstname = scanner.nextLine();
        //check empty input for firstname
        checkFirstname(userFirstname, scanner, applicant);

        //3) Career Summary
        System.out.print("Career Summary: ");
        String careerSummary = scanner.nextLine();
        //check n/a
        if (careerSummary.length() == 0){
            applicant.setCareerSummary("n/a");
        }else{
            applicant.setCareerSummary(careerSummary);
        }

        //4) Age
        System.out.print("Age: ");
        String ageStr = scanner.nextLine();
        int age = 0;
        //check age
        checkAge(ageStr, age, applicant, scanner);

        //5) Gender
        System.out.print("Gender: ");
        String gender = scanner.nextLine();
        // check gender: female,male,and other.
        checkGender(gender, applicant, scanner);

        //6) Highest Degree
        System.out.print("Highest Degree: ");
        String highestDegree = scanner.nextLine();
        // check  Highest Degree: Bachelor,Master,and PHD.
        checkDegree("Highest Degree", highestDegree, scanner, applicant, job);

        //7) Coursework
        System.out.println("Coursework: ");

        System.out.print("- COMP90041: ");
        String coursework90041Str = scanner.nextLine();
        //check Coursework 90041
        checkCourseWork(coursework90041Str, scanner, applicant, "90041");

        System.out.print("- COMP90038: ");
        String coursework90038Str = scanner.nextLine();
        //check Coursework 90038
        checkCourseWork(coursework90038Str, scanner, applicant, "90038");

        System.out.print("- COMP90007: ");
        String coursework90007Str = scanner.nextLine();
        //check Coursework 90007
        checkCourseWork(coursework90007Str, scanner, applicant, "90007");

        System.out.print("- INFO90002: ");
        String coursework90002Str = scanner.nextLine();
        //check Coursework 90002
        checkCourseWork(coursework90002Str, scanner, applicant, "90002");

        //8) Salary Expectation
        System.out.print("Salary Expectations ($ per annum): ");
        String salaryExpectationStr = scanner.nextLine();
        //check Salary Expectations
        checkSalary("Salary Expectation", salaryExpectationStr, scanner, applicant, job);

        //9) Date for Availability
        System.out.print("Availability: ");
        String availabilityDate = scanner.nextLine();
        // check Availability
        checkDateFormat("Availability", availabilityDate, scanner, applicant, job);

    }

    /**
     * check empty input for lastname
     *
     * @param userLastname the lastname inputted by user
     * @param scanner the user's command
     * @param applicant the current applicant
     */
    private void checkLastname(String userLastname, Scanner scanner, Applicant applicant){

        while(true){
            if (userLastname.length() == 0){
                System.out.print("Ooops! Lastname must be provided: ");
                userLastname = scanner.nextLine();
            }else{
                applicant.setLastname(userLastname);
                break;
            }
        }
    }

    /**
     * check empty input for firstname
     *
     * @param userFirstname the firstname inputted by user
     * @param scanner the user's command
     * @param applicant the current applicant
     */
    private void checkFirstname(String userFirstname, Scanner scanner, Applicant applicant){

        while(true){
            if (userFirstname.length() == 0){
                System.out.print("Ooops! Firstname must be provided: ");
                userFirstname = scanner.nextLine();
            }else{
                applicant.setFirstname(userFirstname);
                break;
            }
        }

    }

    /**
     * check empty input and inputted range for age
     *
     * @param ageStr the age(string) inputted by user
     * @param age the age(int) used to test the format
     * @param applicant the current applicant
     * @param scanner the user's command
     */
    private void checkAge(String ageStr, int age, Applicant applicant, Scanner scanner){

        //check age
        while(true) {

            //check age (empty)
            if (ageStr.length() == 0){
                System.out.print("Ooops! A valid age between 18 and 100 must be provided: ");

            }else{

                try{
                    age = Integer.parseInt(ageStr);

                    //check age (>18 & < 100)
                    if (age > 18 && age < 100) {
                        applicant.setAge(age);
                        break;
                    }else{
                        System.out.print("Ooops! A valid age between 18 and 100 must be provided: ");
                    }

                }
                catch (Exception e){
                    System.out.print("Ooops! A valid age between 18 and 100 must be provided: ");
                }
            }

            ageStr = scanner.nextLine();

        }
    }

    /**
     * check gender inputted by user: female,male,and other.
     *
     * @param gender the gender(string) inputted by user
     * @param applicant the current applicant
     * @param scanner the user's command
     */
    private void checkGender(String gender, Applicant applicant, Scanner scanner){

        while(true) {
            if (gender.equals("female")
                    || gender.equals("male")
                    || gender.equals("other")){

                applicant.setGender(gender);
                break;

            }else if (gender.length() == 0){
                applicant.setGender("n/a");
                break;

            }else{
                System.out.print("Invalid input! Please specify Gender: ");
                gender = scanner.nextLine();

            }
        }
    }

    /**
     * check the inputted range for the grade inputted by user
     *
     * @param courseworkStr  the grade inputted by user
     * @param scanner the user's command
     * @param applicant the current applicant
     * @param courseType the type of the coursework: 90041/90038/90002/90007
     */
    private void checkCourseWork(String courseworkStr,
                                 Scanner scanner,
                                 Applicant applicant,
                                 String courseType){

        //check Coursework
        while(true) {

            //check Coursework (empty) but allow
            int coursework;

            if (courseworkStr.length() == 0){
                coursework = 0; // means missing value
                setAfterCheckCoursework(courseType, applicant, coursework);
                break;

            }else{

                try{
                    coursework = Integer.parseInt(courseworkStr);

                    //check Coursework (>=49 & <=100)
                    if (coursework >= 49 && coursework <= 100) {

                        setAfterCheckCoursework(courseType, applicant, coursework);
                        break;

                    }else{
                        System.out.print("Invalid input! Please specify Course Work: ");
                    }

                }
                catch (Exception e){
                    System.out.print("Invalid input! Please specify Course Work: ");
                }
            }

            courseworkStr = scanner.nextLine();

        }
    }

    /**
     * set the value of coursework for this applicant after checking the restrictions
     *
     * @param courseType the type of the coursework: 90041/90038/90002/90007
     * @param applicant the current applicant
     * @param coursework the grade(int) of the coursework
     */
    private void setAfterCheckCoursework(String courseType, Applicant applicant, int coursework){

        if (courseType.equals("90041")){
            applicant.setCoursework90041(coursework);

        }else if (courseType.equals("90038")){
            applicant.setCoursework90038(coursework);

        }else if (courseType.equals("90007")){
            applicant.setCoursework90007(coursework);

        }else if (courseType.equals("90002")){
            applicant.setCoursework90002(coursework);

        }

    }

    /**
     * append new data into the file.
     * (new applicant's data into application.csv)
     * (new job's data into jobs.csv)
     * (new data of applicant with selected job into applicantWithJobSelect.csv)
     *
     * @param action the type of the action: application/job/applicant's job selection
     * @param filename the name of the file needed to append
     * @param applicant the current applicant
     * @param job the current job
     * @param userSelectRecord all the job selections of the current applicant
     */
    private void addNewRecordToCSV(String action, String filename, Applicant applicant,
                                   Job job, ArrayList<String> userSelectRecord){

        String newRecord = null;

        if (action.equals("application")){
            newRecord = combineNewApplicationRecord(applicant);

        } else if (action.equals("job")){
            newRecord = combineNewJobRecord(job);

        } else if (action.equals("applicant's job selection")){
            newRecord = combineNewApplicantWithJobSelectRecord(applicant, userSelectRecord);

        }

        try{
            FileWriter fileWriter = new FileWriter(filename, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            //append
            printWriter.println(newRecord);

            //close
            printWriter.flush();
            printWriter.close();

        } catch (Exception e) {
            System.out.println("File not found.");
        }
    }

    /**
     * combine the application's elements into a new record row
     *
     * @param applicant the current applicant
     * @return a string of the combined record row for the current applicant
     */
    private String combineNewApplicationRecord(Applicant applicant){

        long unixTime = System.currentTimeMillis() / 1000L;

        String newRecord = unixTime + ","
                + applicant.getLastname() + ","
                + applicant.getFirstname() + ",";

        if (!applicant.getCareerSummary().equals("n/a")){
            newRecord += applicant.getCareerSummary() + ",";
        }else{
            newRecord += ",";
        }

        newRecord += applicant.getAge() +  ",";

        if (!applicant.getGender().equals("n/a")){
            newRecord += applicant.getGender() + ",";
        }else{
            newRecord += ",";
        }

        if (!applicant.getHighestDegree().equals("n/a")){
            newRecord += applicant.getHighestDegree() + ",";
        }else{
            newRecord += ",";
        }

        if (applicant.getCoursework90041() != 0){
            newRecord += applicant.getCoursework90041() + ",";
        }else{
            newRecord += ",";
        }

        if (applicant.getCoursework90038() != 0){
            newRecord += applicant.getCoursework90038() + ",";
        }else{
            newRecord += ",";
        }

        if (applicant.getCoursework90007() != 0){
            newRecord += applicant.getCoursework90007() + ",";
        }else{
            newRecord += ",";
        }

        if (applicant.getCoursework90002() != 0){
            newRecord += applicant.getCoursework90002() + ",";
        }else{
            newRecord += ",";
        }

        if (applicant.getSalaryExpectation() != 0){
            newRecord += applicant.getSalaryExpectation() + ",";
        }else{
            newRecord += ",";
        }

        if (!applicant.getAvailabilityDate().equals("n/a")){

            //need to check
            String[] dateStr = applicant.getAvailabilityDate().split("/");
            String dateRecord = dateStr[0] + "/" + dateStr[1] + "/"
                    + dateStr[2].charAt(0) + dateStr[2].charAt(1);
            newRecord += dateRecord;

        }

        return newRecord;
    }

    /**
     * combine the job's elements into a new record row
     *
     * @param job the current job
     * @return a string of the combined record row for the current job
     */
    private String combineNewJobRecord(Job job){

        long unixTime = System.currentTimeMillis() / 1000L;

        String newRecord = unixTime + ","
                + job.getPositionTitle() + ",";

        if (!job.getPositionDescription().equals("n/a")){
            newRecord += job.getPositionDescription() + ",";
        }else{
            newRecord += ",";
        }

        if (!job.getMinDegreeRequirement().equals("n/a")){
            newRecord += job.getMinDegreeRequirement() + ",";
        }else{
            newRecord += ",";
        }

        if (job.getSalary() != 0){
            newRecord += job.getSalary() + ",";
        }else{
            newRecord += ",";
        }

        //need to check
        String[] dateStr = job.getStartDate().split("/");
        String dateRecord = dateStr[0] + "/" + dateStr[1] + "/"
                + dateStr[2].charAt(0) + dateStr[2].charAt(1);
        newRecord += dateRecord;

        return newRecord;

    }

    /**
     * combine new applicant with job selection record
     *
     * @param applicant the current applicant
     * @param userSelectRecord all the job selections of the current applicant
     * @return a string of the combined record row for the current applicant with their job selection
     */
    private String combineNewApplicantWithJobSelectRecord(Applicant applicant,
                                                          ArrayList<String> userSelectRecord){

        String newRecord = combineNewApplicationRecord(applicant) + ",";
        String[] jobSelectStr = userSelectRecord.toArray(new String[0]);
        String jobSelectRecord;

        if (jobSelectStr.length == 1){
            jobSelectRecord = jobSelectStr[0];

        }else {
            jobSelectRecord = jobSelectStr[0] + "/";

            for (int i = 1; i < jobSelectStr.length-1; i++){
                jobSelectRecord += jobSelectStr[i] + "/";
            }

            jobSelectRecord += jobSelectStr[jobSelectStr.length-1];

        }

        newRecord +=  jobSelectRecord;

        return newRecord;
    }

    /**
     * list available job from jobs.csv file
     *
     * @param filename the name of the file
     * @param jobNum the number of the job
     * @param userSelectRecord all the job selections of the current applicant
     */
    private void listJob(String filename, int jobNum, ArrayList<String> userSelectRecord){

        ArrayList<String> jobList = new ArrayList<String>();

        try {
            File file = new File(filename);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //read entire line as string
            String eachLine = bufferedReader.readLine();
            while (eachLine != null) {
                jobList.add(eachLine);
                eachLine = bufferedReader.readLine();
            }

            // store the arraylist to array
            String[] jobArray = jobList.toArray(new String[0]);

            // spilt and print the array
            int colNum = 6;
            for (int i = 1; i <= jobNum; i++){

                // for this applicant: only print the jobs hasn't been applied
                if (userSelectRecord.size() == 0){
                    printFormatJobList(jobArray, i, colNum);

                } else {
                    int M = countApplicationNum(userSelectRecord);
                    String[] jobSelectArr = userSelectRecord.toArray(new String[0]);

                    boolean hasBeenApplied = false;

                    for (int k = 0 ; k < M; k++){
                        if (i == Integer.parseInt(jobSelectArr[k])){
                            hasBeenApplied = true;

                        }

                    }

                    if (!hasBeenApplied){
                        printFormatJobList(jobArray, i, colNum);

                    }

                }

            }

            bufferedReader.close();

        } catch(FileNotFoundException exception) {
            System.out.println("File not found!");

        } catch (IOException exception) {
            System.out.println("There is an error.");
        }

    }


    /**
     * print the job list after formatting
     *
     * @param jobArray an array contains the jobs
     * @param i index of each job
     * @param colNum the number of columns in the job file
     */
    private void printFormatJobList(String[] jobArray, int i, int colNum){

        String[] Str = jobArray[i].split(",", colNum);

        System.out.print("[" + i + "] ");

        for (int j = 1; j < colNum; j++){

            // Replace the null string with n/a
            if (Str[j].length() == 0){
                Str[j] = "n/a";
            }

            // print all column
            if (j == 1){
                System.out.print(Str[j]);

            } else if (j == 2){
                System.out.print(" (" + Str[j] + "). ");

            } else if (j == 3){
                System.out.print(Str[j] + ". ");

            } else if (j == 4){
                System.out.print("Salary: " + Str[j] + ". ");

            }else if (j == 5){
                String[] dateStr = Str[j].split("/");
                String dateRecord = dateStr[0] + "/" + dateStr[1] + "/"
                        + "20" + dateStr[2].charAt(0) + dateStr[2].charAt(1);
                System.out.print("Start Date: " + dateRecord + ".");

            }

        }

        System.out.print("\n");

    }

    /**
     * the method for the current applicant to apply the job
     *
     * @param scanner the user's command
     * @param jobNum_N the number of the jobs
     * @param userSelectRecord the job selections of the current applicant
     */
    private void applyJob(Scanner scanner,
                          int jobNum_N,
                          ArrayList<String> userSelectRecord){

        System.out.print("Please enter the jobs you would like to apply for (multiple options are possible): ");

        while(true) {
            String userCommand = scanner.nextLine();

            // test the userCommand is valid or not (a single number / list of numbers)
            boolean selectIsValid = isValidSelection(userCommand);

            if (selectIsValid) {

                if (userCommand.length() == 1) {

                    // Record the applicant's job selection
                    userSelectRecord.add(userCommand);

                } else if (userCommand.length() > 1) {

                    // Spilt the applicant's job selection
                    int jobNumThisTime = userCommand.split(",").length;
                    String[] userCommandArray = userCommand.split(",");

                    for (int i = 0; i < jobNumThisTime; i++) {
                        userSelectRecord.add(userCommandArray[i]);
                    }

                }
                //Return the main menu without 'create' command
                displayMainMenuWithCreateNot(jobNum_N, userSelectRecord.size());
                break;

            }
        }

    }

    /**
     * test the userCommand is valid or not (a single number / list of numbers)
     *
     * @param userCommand the user's command
     * @return a boolean result presents the userCommand is valid or not
     */
    private boolean isValidSelection(String userCommand){
        boolean selectIsValid = false;

        if (userCommand.length() == 0){
            selectIsValid = true;

        } else if (userCommand.length() == 1){

            try{
                int value = Integer.parseInt(userCommand);
                selectIsValid = true;

            } catch (Exception e) {
                System.out.print("Invalid input! Please enter a valid number to continue: ");
            }

        } else {

            // Spilt the applicant's job selection
            int jobNumThisTime = userCommand.split(",").length;
            String[] userCommandArray = userCommand.split(",");

            for (int i = 0; i < jobNumThisTime; i++){

                try{
                    int value = Integer.parseInt(userCommandArray[i]);
                    selectIsValid = true;

                } catch (Exception e) {
                    System.out.print("Invalid input! Please enter a valid number to continue: ");
                    break;

                }
            }

        }

        return selectIsValid;

    }


    /**
     * count the num of applications
     *
     * @param userSelectRecord all the job selections of the current applicant
     * @return the number of applications
     */
    private int countApplicationNum(ArrayList<String> userSelectRecord){

        int applicationNum_M = 0;
        applicationNum_M = userSelectRecord.size();

        return applicationNum_M;

    }

    /**
     * count total number of applications for HR
     *
     * @param filename the name of the file
     * @param applicantNum the number of the applicants
     * @return the total number of applications
     */
    private int countTotalNumApplication(String filename, int applicantNum){

        int totalApplicationNum = 0;

        ArrayList<String> ApplicantJobSelectList = new ArrayList<String>();

        // read the applicantWithJobSelect.csv file
        try{
            File f = new File(filename);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                ApplicantJobSelectList.add(line);
                line = br.readLine();
            }

            String[] applicantJobSelectArray = ApplicantJobSelectList.toArray(new String[0]);

            int jobSelectCol = 13;

            if (applicantJobSelectArray.length > 1){

                for (int k = 1; k <= applicantNum; k++){

                    // spilt the row
                    String[] appJobStr = applicantJobSelectArray[k].split(
                            ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                    // extract job selection column (string)
                    int appJobNum = appJobStr[jobSelectCol].split("/").length;

                    totalApplicationNum += appJobNum;

                }
            }

            br.close();

        } catch(FileNotFoundException exception){
            System.out.println("3 An error occurred while loading the file.");

        } catch (Exception e) {

            System.out.println("4 An error occurred while loading the file.");
        }

        return totalApplicationNum;

    }

    /**
     * display the HA's main menu with 'create' command
     *
     * @param applicationNum the number of applications
     */
    private void displayHRMainMenuWithCreate(int applicationNum){
        System.out.println(applicationNum + " applications received.");
        System.out.println("Please enter one of the following commands to continue:");
        System.out.println("- create new job: [create] or [c]");
        System.out.println("- list available jobs: [jobs] or [j]");
        System.out.println("- list applicants: [applicants] or [a]");
        System.out.println("- filter applications: [filter] or [f]");
        System.out.println("- matchmaking: [match] or [m]");
        System.out.println("- quit the program: [quit] or [q]");
        System.out.print("> "); //here
    }

    /**
     * create a new job in the HA system
     *
     * @param scanner the user's command
     * @param job the current job
     * @param applicant the current applicant
     */
    private void createJob(Scanner scanner, Job job, Applicant applicant){

        System.out.println("# Create new Job");

        //1) Position Title
        System.out.print("Position Title: ");
        String positionTitle = scanner.nextLine();
        // check Position Title
        checkPositionTitle(positionTitle, scanner, job);

        //2) Position Description
        System.out.print("Position Description: ");
        String positionDescription = scanner.nextLine();
        //check n/a
        if (positionDescription.length() == 0){
            job.setPositionDescription("n/a");
        }else{
            job.setPositionDescription(positionDescription);
        }

        //3) Minimum Degree Requirement
        System.out.print("Minimum Degree Requirement: ");
        String minDegreeRequirement = scanner.nextLine();
        // check Minimum Degree Requirement: Bachelor,Master,and PHD.
        checkDegree("Minimum Degree Requirement",
                minDegreeRequirement, scanner, applicant, job);

        //4) Salary
        System.out.print("Salary ($ per annum): ");
        String salary = scanner.nextLine();
        // check Salary
        checkSalary("Salary", salary, scanner, applicant, job);

        //5) Start Date
        System.out.print("Start Date: ");
        String startDate = scanner.nextLine();
        // check date format
        checkDateFormat("Start Date", startDate, scanner, applicant, job);

    }

    /**
     * check restriction for Position Title
     *
     * @param positionTitle the position title user inputted
     * @param scanner the user's command
     * @param job the current job
     */
    private void checkPositionTitle(String positionTitle, Scanner scanner, Job job){

        while(true){
            if (positionTitle.length() == 0){
                System.out.print("Ooops! Position Title must be provided: ");
                positionTitle = scanner.nextLine();
            }else{
                job.setPositionTitle(positionTitle);
                break;
            }
        }

    }

    /**
     * check restriction for Degree: Bachelor,Master,and PHD.
     *
     * @param degreeTitle the degree title for different systems:
     *                    Highest Degree/Minimum Degree Requirement
     * @param degree the degree user inputted
     * @param scanner the user's command
     * @param applicant the current applicant
     * @param job the current job
     */
    private void checkDegree(String degreeTitle, String degree, Scanner scanner,
                             Applicant applicant, Job job){

        while(true) {
            if (degree.equals("Bachelor")
                    || degree.equals("Master")
                    || degree.equals("PHD")){

                if (degreeTitle.equals("Highest Degree")){
                    applicant.setHighestDegree(degree);

                } else if (degreeTitle.equals("Minimum Degree Requirement")){
                    job.setMinDegreeRequirement(degree);
                }

                break;

            }else if (degree.length() == 0) {

                if (degreeTitle.equals("Highest Degree")) {
                    applicant.setHighestDegree("n/a");

                } else if (degreeTitle.equals("Minimum Degree Requirement")){
                    job.setMinDegreeRequirement("n/a");

                }

                break;

            }else{

                System.out.print("Invalid input! Please specify " + degreeTitle + ": ");
                degree = scanner.nextLine();

            }
        }
    }

    /**
     * check restriction for Salary Expectation / Salary
     *
     * @param salaryTitle for different systems: Salary Expectation/Salary
     * @param salaryStr the string of the salary user inputted
     * @param scanner the user's command
     * @param applicant the current applicant
     * @param job the current job
     */
    private void checkSalary(String salaryTitle, String salaryStr,
                             Scanner scanner, Applicant applicant, Job job){

        while(true) {

            //check Salary Expectations/ Salary (empty) but allow
            int salary;

            if (salaryStr.length() == 0) {
                salary = 0; // means missing value

                if (salaryTitle.equals("Salary Expectation")){
                    applicant.setSalaryExpectation(salary);

                } else if (salaryTitle.equals("Salary")){
                    job.setSalary(salary);
                }

                break;

            } else {
                try{
                    salary = Integer.parseInt(salaryStr);

                    //check salaryExpectation/ Salary (>0)
                    if (salary > 0) {

                        if (salaryTitle.equals("Salary Expectation")){
                            applicant.setSalaryExpectation(salary);

                        } else if (salaryTitle.equals("Salary")){
                            job.setSalary(salary);
                        }

                        break;

                    }else{
                        System.out.print("Invalid input! Please specify " + salaryTitle + ": ");
                    }

                }
                catch (Exception e){
                    System.out.print("Invalid input! Please specify " + salaryTitle + ": ");
                }
            }

            salaryStr = scanner.nextLine();

        }
    }

    /**
     * check Date format for Availability/Start Date
     *
     * @param dateTitle for different systems: Availability/Start Date
     * @param date the string of the date user inputted
     * @param scanner the user's command
     * @param applicant the current applicant
     * @param job the current job
     */
    private void checkDateFormat(String dateTitle, String date,
                                 Scanner scanner, Applicant applicant, Job job){

        while(true) {

            //check empty
            if (date.length() == 0) {

                //check Availability (empty) but allow
                if (dateTitle.equals("Availability")){
                    applicant.setAvailabilityDate("n/a");
                    break;

                    //check Start Date (empty) but not allow
                } else if (dateTitle.equals("Start Date")){
                    System.out.print("Ooops! Start Date must be provided: ");
                    date = scanner.nextLine();

                }

                // check format
            }else{

                try {
//                        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(
//                                "dd/MM/yy", Locale.ENGLISH);
//                        LocalDate localDate = LocalDate.parse(date, dtFormatter);
//                        String formatDate = localDate.format(dtFormatter);

                    DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(
                            "dd/MM/yy", Locale.ENGLISH);

                    String formatDate = null;
                    String formatCurrentDate = null;

                    if (date.length() == 10){

                        SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
                        Date inputDate = input.parse(date);
                        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yy");
                        formatDate = output.format(inputDate);
                        // now
                        Date currentDate = new Date(System.currentTimeMillis());
                        formatCurrentDate = output.format(currentDate);

                    }else {
                        LocalDate localDate = LocalDate.parse(date, dtFormatter);
                        formatDate = localDate.format(dtFormatter);
                        // now
                        LocalDate currentDate = LocalDate.now();
                        formatCurrentDate = currentDate.format(dtFormatter);
                    }

                        boolean isFutureDate;

//                        if (formatDate.equals(date)) {

                            // check the date is after today
                            isFutureDate = checkDateFuture(formatDate, formatCurrentDate);

                            if (isFutureDate) {

                                if (dateTitle.equals("Availability")) {
                                    applicant.setAvailabilityDate(date);

                                } else if (dateTitle.equals("Start Date")) {
                                    job.setStartDate(date);

                                }

                                break;

//                            } else {
//
//                                System.out.print("Invalid input! Please specify " +
//                                        dateTitle + ": ");
//
//                            }

                        } else {
                            System.out.print("Invalid input! Please specify " + dateTitle + ": ");

                        }

                } catch (DateTimeParseException | ParseException e) { //here
                    System.out.print("Invalid input! Please specify " + dateTitle + ": ");

                }

                date = scanner.nextLine();

            }

        }
    }

    /**
     * check the date inputted is after today
     *
     * @param formatDate the formatted date user inputted
     * @param formatCurrentDate the formatted date of today
     * @return the boolean represents the date inputted is after today or not
     * @throws ParseException the string can not parse to the date format
     */
    private boolean checkDateFuture(String formatDate, String formatCurrentDate)
            throws ParseException {

        boolean isFutureDate = false;

        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        Calendar cal = Calendar.getInstance();

        cal.setTime(df.parse(formatDate));
        Date inputDate = cal.getTime();

        cal.setTime(df.parse(formatCurrentDate));
        Date currentDate = cal.getTime();

        if (inputDate.compareTo(currentDate) > 0){
            isFutureDate = true;

        }
        return isFutureDate;

    }

    /**
     * list available jobs with its applicant
     *
     * @param jobsFilename the name of the job file
     * @param applicantWithJobSelectFilename the name of the applicantWithJobSelect file
     * @param jobNum the num of the jobs
     * @param applicantNum the num of the applicants
     */
    private void listJobWithApplicant(String jobsFilename, String applicantWithJobSelectFilename,
                                      int jobNum, int applicantNum){

        ArrayList<String> jobList = new ArrayList<String>();
        ArrayList<String> ApplicantJobSelectList = new ArrayList<String>();

        try {
            // 1) read the jobs.csv file
            File file = new File(jobsFilename);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //read entire line as string
            String eachLine = bufferedReader.readLine();
            while (eachLine != null) {
                jobList.add(eachLine);
                eachLine = bufferedReader.readLine();
            }

            // store the arraylist to array
            String[] jobArray = jobList.toArray(new String[0]);

            // 2) read the applicantWithJobSelect.csv file
            try{
                File f = new File(applicantWithJobSelectFilename);
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);

                String line = br.readLine();
                while (line != null) {
                    ApplicantJobSelectList.add(line);
                    line = br.readLine();
                }

                String[] applicantJobSelectArray = ApplicantJobSelectList.toArray(new String[0]);

                //print jobs with their applicant
                int colNum = 6;
                int jobSelectCol = 13;

                for (int i = 1; i <= jobNum; i++){

                    // print formatted job list
                    printFormatJobList(jobArray, i, colNum);

                    // if exists applicants
                    if (applicantJobSelectArray.length > 1){

                        char letterItem = 'a';
                        int numItem = 1;

                        for (int k = 1; k <= applicantNum; k++){

                            // spilt the row
                            String[] appJobStr = applicantJobSelectArray[k].split(
                                    ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                            // extract job selection column (string)
                            String[] appJobList = appJobStr[jobSelectCol].split("/");

                            for (int h = 0; h < appJobList.length; h++){

                                // if the job was selected
                                if (i == Integer.parseInt(appJobList[h])){

                                    // print the item
                                    printItemCounter(letterItem, numItem);

                                    letterItem++;

                                    if (letterItem > 'z'){
                                        letterItem = 'a';
                                        numItem++;

                                    }

                                    // print the applicants
                                    printApplicantListForEachJob(appJobStr);

                                }
                            }
                        }
                    }
                }

                br.close();

            } catch(FileNotFoundException exception){
                System.out.println("File not found.");

            } catch (Exception e) {

                System.out.println("There is an error.");
            }

            bufferedReader.close();

        } catch(FileNotFoundException exception) {
            System.out.println("File not found.");

        } catch (IOException e) {
            System.out.println("There is an error.");
        }

    }

    /**
     * print the item counter
     *
     * @param letterItem the letter item
     * @param numItem the number item
     */
    private void printItemCounter(char letterItem, int numItem){

        System.out.print("    ");
        if (numItem == 1) {
            System.out.print("[" + letterItem + "] ");

        } else{
            System.out.print("[" + letterItem);
            System.out.print(numItem + "] ");

        }

    }

    /**
     * print applicants for each job
     *
     * @param appJobStr the string of the job selections for each applicant
     */
    private void printApplicantListForEachJob(String[] appJobStr){

        for (int m = 1; m < appJobStr.length ; m++){

            // address 'n/a' value
            if (appJobStr[m].length() == 0){
                appJobStr[m] = "n/a";

            }

        }

        // print formatted applicants
        printFormatApplicant(appJobStr);

    }

    /**
     * print formatted applicants
     *
     * @param appJobStr the string of the job selections for each applicant
     */
    private void printFormatApplicant(String[] appJobStr){

        System.out.print(appJobStr[1] + ", "); //lastname
        System.out.print(appJobStr[2] + " ");  //firstname
        System.out.print("(" + appJobStr[6] + ")" + ": "); //degree
        System.out.print(appJobStr[3] + ". "); //career summary
        System.out.print("Salary Expectations : " + appJobStr[11] + ". "); //salary expectations

        //need to check
        if (appJobStr[12].equals("n/a")){
            System.out.println("Available: " + appJobStr[12] + "."); //availability

        } else {
            String[] dateStr = appJobStr[12].split("/");

            String dateRecord = null;

            if (dateStr[2].length() == 2){
                dateRecord = dateStr[0] + "/" + dateStr[1] + "/"
                        + "20" + dateStr[2].charAt(0) + dateStr[2].charAt(1);

            } else if (dateStr[2].length() == 4){
                dateRecord = appJobStr[12];
            }

            System.out.println("Available: " + dateRecord + "."); //availability

        }

    }

    /**
     * list sorted and formatted applicants
     *
     * @param applicantFilename the name of the applicant file
     */
    private void listSortedFormattedApplicant(String applicantFilename){

        // 1) create an Arraylist to store all the applicants
        ArrayList<String> applicantList = new ArrayList<>();

        // 2) read the applicantWithJobSelect.csv file
        applicantList = readAppJobFile(applicantFilename);

        // 3) format the date in the list of applicants
        ArrayList<ArrayList<String>> formatDateAppList = formatDate(applicantList);

        // 4) sort the list of applicants
        ArrayList<ArrayList<String>> sortFormatDateAppList = sortApplicantList(formatDateAppList);

        // 5) print the formatted and sorted applicant list
        printFormattedSortedAppList(sortFormatDateAppList);

    }

    /**
     * read the applicant with job file
     *
     * @param applicantFilename the name of the applicant file
     * @return an arraylist of list of the applicants
     */
    private ArrayList<String> readAppJobFile(String applicantFilename){

        ArrayList<String> applicantList = new ArrayList<>();

        try{
            File f = new File(applicantFilename);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                applicantList.add(line);
                line = br.readLine();
            }

            br.close();

        } catch(FileNotFoundException exception){
            System.out.println("File not found.");

        } catch (Exception e) {
            System.out.println("There is an error");

        }

        return applicantList;
    }

    /**
     * format the date in the list of applicants
     *
     * @param applicantList an array list of the applicants
     * @return a 2D arraylist of applicant List with formatted dates
     */
    private ArrayList<ArrayList<String>> formatDate(ArrayList<String> applicantList){

        ArrayList<ArrayList<String>> formattedApplicantDateList = new ArrayList<>();

        int appNum = fileRowNum("applications.csv");

        String[] appArr = applicantList.toArray(new String[0]);

        for (int i = 1; i <= appNum; i++){

            ArrayList<String> eachRow = new ArrayList<>();

            // replace ", " in career summary
            appArr[i] = appArr[i].replaceAll(", ", "&");

            if (appArr[i].split(",").length == 12
                    || appArr[i].split(",").length == 13){

                String[] appArrElement = appArr[i].split(",", 13);

                for (int k = 1; k < appArrElement.length; k++){

                    if (appArrElement[k].length() == 0){
                        appArrElement[k] = "n/a";
                    }

                    eachRow.add(appArrElement[k]);

                }

                String dateRecord = "n/a";

                if (!appArrElement[12].equals("n/a")) {

                    String[] dateStr = appArrElement[12].split("/");
                    dateRecord = dateStr[0] + "/" + dateStr[1] + "/"
                            + "20" + dateStr[2].charAt(0) + dateStr[2].charAt(1);

                }

                eachRow.set(11, dateRecord);

            } else {
                System.out.println("less than 13 column");

            }

            formattedApplicantDateList.add(eachRow);

        }

        return formattedApplicantDateList;

    }

    /**
     * sort the list of applicants
     *
     * @param formatDateAppList a 2D arraylist of applicant List with formatted dates
     * @return a 2D arraylist of sorted applicant List
     */
    private ArrayList<ArrayList<String>> sortApplicantList(
            ArrayList<ArrayList<String>> formatDateAppList){

        // sort by 'available day'
        DateFormat df = new SimpleDateFormat("dd/MM/yy");

        Collections.sort(formatDateAppList, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {

                int index = 0;

                if (!Objects.equals(o1.get(11), o2.get(11))
                        && !Objects.equals(o1.get(11), "n/a")
                        && !Objects.equals(o2.get(11), "n/a")){

                    Calendar cal = Calendar.getInstance();

                    try{
                        cal.setTime(df.parse(o1.get(11)));
                        Date date1 = cal.getTime();

                        cal.setTime(df.parse(o2.get(11)));
                        Date date2 = cal.getTime();

                        index = date1.compareTo(date2);

                    } catch (ParseException e) {
                        System.out.println("An error in parse the date");
                    }

                } else if (!Objects.equals(o1.get(11), o2.get(11)) &&
                        (Objects.equals(o1.get(11), "n/a")
                                || Objects.equals(o2.get(11), "n/a"))) {

                    index = o1.get(11).compareTo(o2.get(11));

                } else if (Objects.equals(o1.get(11), o2.get(11))){

                    // sort by 'lastname'
                    if (!Objects.equals(o1.get(0), o2.get(0))){

                        index = o1.get(0).compareTo(o2.get(0));

                        // sort by 'firstname'
                    } else if (!Objects.equals(o1.get(1), o2.get(1))){

                        index = o1.get(1).compareTo(o2.get(1));

                    }

                }

                return index;
            }
        });

        return formatDateAppList;

    }

    // print the formatted and sorted applicant list

    /**
     * print the formatted and sorted applicant list
     *
     * @param sortFormatDateAppList a 2D arraylist of sorted applicant List
     */
    private void printFormattedSortedAppList(ArrayList<ArrayList<String>> sortFormatDateAppList){

        for (int i = 0; i < sortFormatDateAppList.size(); i++){

            sortFormatDateAppList.get(i).add(0, "0"); //for align

            String[] arr = sortFormatDateAppList.get(i).toArray(new String[0]);

            for (int k = 0; k < arr.length; k++){
                arr[k] = arr[k].replaceAll("&", ", ");

            }

            int item = i + 1;
            System.out.print("[" + item + "] ");

            printFormatApplicant(arr);

        }
    }

    /**
     * sort the list of applicants by 'lastname'/'degree'/'wam'
     *
     * @param filename the name of the file
     * @param filterType the feature used to filter
     */
    private void filterFunction(String filename, String filterType){

        // 1) create an Arraylist to store all the applicants
        ArrayList<String> applicantList = new ArrayList<>();

        // 2) read the applicantWithJobSelect.csv file
        applicantList = readAppJobFile(filename);

        // 3) format the date in the list of applicants
        ArrayList<ArrayList<String>> formatDateAppList = formatDate(applicantList);

        // 4) filter
        // sort the list of applicants by 'lastname'
        if (filterType.equals("lastname")){

            Collections.sort(formatDateAppList, new Comparator<ArrayList<String>>() {
                public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                    return o1.get(0).compareTo(o2.get(0));
                }
            });

            // print the formatted and sorted applicant list
            printFormattedSortedAppList(formatDateAppList);

            // sort the list of applicants by 'degree'
        } else if (filterType.equals("degree")){

            Collections.sort(formatDateAppList, new Comparator<ArrayList<String>>() {
                public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                    return o1.get(5).compareTo(o2.get(5));
                }
            });

            // print the formatted and sorted applicant list
            printFormattedSortedAppList(formatDateAppList);

            // sort the list of applicants by 'wam'
        } else if (filterType.equals("wam")){

            // calculate the wam of applicants
            ArrayList<ArrayList<String>> wamAppList = calculateWam(formatDateAppList);

            // sort by wam
            ArrayList<ArrayList<String>> sortWamAppList = sortByWam(wamAppList);

            // print the formatted and sorted applicant list
            printFormattedSortedAppList(sortWamAppList);

        }

    }

    /**
     * sort the applicant list by wam
     *
     * @param wamAppList a 2D arraylist of applicant list with wam
     * @return a 2D arraylist of applicant list sorted by wam
     */
    private ArrayList<ArrayList<String>> sortByWam(ArrayList<ArrayList<String>> wamAppList){

        ArrayList<ArrayList<String>> sortWamAppList = wamAppList;

        Collections.sort(wamAppList, new Comparator<ArrayList<String>>() {
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {

                int index = 0;

                // normal
                if (!Objects.equals(o1.get(6), o2.get(6))
                        && !Objects.equals(o1.get(6), "n/a")
                        && !Objects.equals(o2.get(6), "n/a") ){

                    //reverse
                    index =  (int) -(Float.parseFloat(o1.get(6))-Float.parseFloat(o2.get(6)));

                } else if (!Objects.equals(o1.get(6), o2.get(6)) &&
                        (Objects.equals(o1.get(6), "n/a")
                                || Objects.equals(o2.get(6), "n/a"))){

                    index =  o1.get(6).compareTo(o2.get(6)); //normal

                } else if (Objects.equals(o1.get(6), o2.get(6))){

                    // sort by 'lastname'
                    if (!Objects.equals(o1.get(0), o2.get(0))){

                        index = o1.get(0).compareTo(o2.get(0));

                        // sort by 'firstname'
                    } else if (!Objects.equals(o1.get(1), o2.get(1))){

                        index = o1.get(1).compareTo(o2.get(1));

                    }

                }

                return index;

            }
        });

        return sortWamAppList;

    }

    /**
     * calculate the wam of applicants
     *
     * @param formatDateAppList a 2D arraylist of applicant List with formatted dates
     * @return a 2D arraylist of  applicant List with wam
     */
    private ArrayList<ArrayList<String>> calculateWam(
            ArrayList<ArrayList<String>> formatDateAppList){

        ArrayList<ArrayList<String>> wamAppList = formatDateAppList;

        int appNum = formatDateAppList.size();

        for (int i = 0; i < appNum; i++){
            String[] eachApp = formatDateAppList.get(i).toArray(new String[0]);

            if (!Objects.equals(eachApp[6], "n/a")
                    && !Objects.equals(eachApp[7], "n/a")
                    && !Objects.equals(eachApp[8], "n/a")
                    && !Objects.equals(eachApp[9], "n/a") ){

                float sum = 0;
                int n = 4;

                sum = Float.parseFloat(eachApp[6])
                        + Float.parseFloat(eachApp[7])
                        + Float.parseFloat(eachApp[8])
                        + Float.parseFloat(eachApp[9]) ;

                float wamGrade = sum/(float)n;

                wamAppList.get(i).set(6, String.valueOf(wamGrade));
                wamAppList.get(i).set(7, String.valueOf(wamGrade));
                wamAppList.get(i).set(8, String.valueOf(wamGrade));
                wamAppList.get(i).set(9, String.valueOf(wamGrade));

            } else {
                wamAppList.get(i).set(6, "n/a");
                wamAppList.get(i).set(7, "n/a");
                wamAppList.get(i).set(8, "n/a");
                wamAppList.get(i).set(9, "n/a");
            }


        }

        return wamAppList;
    }

    /**
     * The function used to call matching algorithm and choose the unique applicant for each job
     *
     * @param jobFilename the name of the job file
     * @param applicantWithJobSelectFilename the name of the applicantWithJobSelect file
     * @param jobNum the number of the jobs
     * @param applicantNum  the number of the applicants
     * @param roleType the role of the user using system
     * @return an 2D arraylist of the results depended on the role type
     */
    private ArrayList<ArrayList<String>> matchFunction(String jobFilename,
                                                       String applicantWithJobSelectFilename,
                                                       int jobNum,
                                                       int applicantNum,
                                                       String roleType){

        ArrayList<String> jobList = new ArrayList<String>();
        ArrayList<String> ApplicantJobSelectList = new ArrayList<String>();

        // create an Arraylist to store the matched applicant for each job
        ArrayList<ArrayList<String>> wholeMatchedList = new ArrayList<>();

        // create an Arraylist to store the Unmatched applicant for each job
        ArrayList<ArrayList<String>> wholeUnMatchedList = new ArrayList<>();

        try {
            // 1) read the jobs.csv file
            File file = new File(jobFilename);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //read entire line as string
            String eachLine = bufferedReader.readLine();
            while (eachLine != null) {
                jobList.add(eachLine);
                eachLine = bufferedReader.readLine();
            }

            // store the arraylist to array
            String[] jobArray = jobList.toArray(new String[0]);


            // 2) read the applicantWithJobSelect.csv file
            try{

                File f = new File(applicantWithJobSelectFilename);
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);

                String line = br.readLine();
                while (line != null) {
                    ApplicantJobSelectList.add(line);
                    line = br.readLine();
                }

                String[] applicantJobSelectArray = ApplicantJobSelectList.toArray(new String[0]);

                //print jobs with their applicant
                int colNum = 6;
                int jobSelectCol = 13;

                for (int i = 1; i <= jobNum; i++){

                    // store all applicants for each job (Update when job changes)
                    ArrayList<ArrayList<String>> eachAppMatchList = new ArrayList<>();

                    // if exists applicants
                    if (applicantJobSelectArray.length > 1) {

                        for (int k = 1; k <= applicantNum; k++) {

                            // spilt the row
                            String[] appJobStr = applicantJobSelectArray[k].split(
                                    ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                            // extract job selection column (string)
                            String[] appJobList = appJobStr[jobSelectCol].split("/");

                            for (int h = 0; h < appJobList.length; h++) {

                                // if the job was selected
                                if (i == Integer.parseInt(appJobList[h])) {

                                    ArrayList<String> eachApp = new ArrayList<>();

                                    for (int s = 0; s < appJobStr.length - 1; s++) {

                                        if (appJobStr[s].length() == 0) {
                                            appJobStr[s] = "n/a";
                                        }

                                        eachApp.add(appJobStr[s]);

                                    }

                                    // will not rank/consider the applicant
                                    // who has been matched last time
                                    if (wholeMatchedList.size() == 0) {
                                        eachAppMatchList.add(eachApp);

                                    } else {
                                        for (ArrayList<String> w : wholeMatchedList) {
                                            if (!w.get(0).equals(eachApp.get(0))) {
                                                eachAppMatchList.add(eachApp);
                                            }

                                        }
                                    }


                                }
                            }
                        }


                        // matching and ranking!
                        Matchmaker matchmaker = new Matchmaker();
                        ArrayList<ArrayList<String>> sortAppTotalCreditList = new ArrayList<>(
                                matchmaker.calculateEachAppTotalCredit(jobList.get(i),
                                        eachAppMatchList));

                        if (sortAppTotalCreditList.size() != 0 && roleType.equals("hr")) {

                            // print formatted job list
                            printFormatJobList(jobArray, i, colNum);

                            // print the applicants (need to change 'get(0)')
                            System.out.print("Applicant match: ");
                            printApplicantListForEachJob(sortAppTotalCreditList.get(0).toArray(new String[0]));

                        }

                        // store the all matched applicants (and remove from the whole list)
                        if (sortAppTotalCreditList.size() != 0) {
                            wholeMatchedList.add(sortAppTotalCreditList.get(0));
                        }

                    }

                }

                // store the all unmatched applicants
                storeAllUnmatchedApp(applicantJobSelectArray, wholeMatchedList,
                        wholeUnMatchedList, applicantNum);

                br.close();

            } catch(FileNotFoundException exception){
                System.out.println("File not found.");

            } catch (Exception e) {

                e.printStackTrace();
                System.out.println("There is an error.");
            }

            bufferedReader.close();

        } catch (FileNotFoundException exception) {
            System.out.println("File not found.");

        } catch (IOException e) {
            System.out.println("There is an error.");
        }

        // return the different list according to different role type
        ArrayList<ArrayList<String>> resultList= new ArrayList<>();

        if (roleType.equals("hr") || roleType.equals("auditMatch")){
            resultList = wholeMatchedList;

        } else if (roleType.equals("auditUnMatch")){
            resultList =  wholeUnMatchedList;
        }

        return resultList;

    }

    /**
     * calculate the average age of the matched list
     *
     * @param allMatchAppList a 2D arraylist of the matched list
     * @return formatted average age of the matched list
     */
    private String calculateAverMatchAge(ArrayList<ArrayList<String>> allMatchAppList){

        float averMatchAge = 0.0F;
        float sum = 0.0F;
        int n = 0;

        for (int i = 0; i < allMatchAppList.size(); i++){

            if (!allMatchAppList.get(i).get(4).equals("n/a")){

                sum += Float.parseFloat(allMatchAppList.get(i).get(4));
                n++;

            }

        }

        averMatchAge = sum/(float)n;

        String formattedAverMatchAge = null;

        if (n == 0){
            formattedAverMatchAge = "0.00";

        } else{
            formattedAverMatchAge = String.format("%.02f", averMatchAge);

        }

        return formattedAverMatchAge;
    }

    /**
     * calculate the average age of the matched list and unmatched list
     *
     * @param allMatchAppList a 2D arraylist of the matched list
     * @param allUnMatchAppList a 2D arraylist of the unmatched list
     * @return formatted average age of the matched list and unmatched list
     */
    private String calculateAverTotalAge(ArrayList<ArrayList<String>> allMatchAppList,
                                         ArrayList<ArrayList<String>>allUnMatchAppList){

        float averTotalAge = 0.0F;
        float sum = 0.0F;
        int n = 0;

        // for matched list
        for (int i = 0; i < allMatchAppList.size(); i++){

            if (!allMatchAppList.get(i).get(4).equals("n/a")){

                sum += Float.parseFloat(allMatchAppList.get(i).get(4));
                n++;

            }

        }

        // for unmatched list
        for (int i = 0; i < allUnMatchAppList.size(); i++){

            if (!allUnMatchAppList.get(i).get(4).equals("n/a")){

                sum += Float.parseFloat(allUnMatchAppList.get(i).get(4));
                n++;

            }

        }

        averTotalAge = sum/(float)n;

        String formattedAverTotalAge = null;

        if (n == 0){
            formattedAverTotalAge = "0.00";

        } else{
            formattedAverTotalAge = String.format("%.02f", averTotalAge);

        }

        return formattedAverTotalAge;

    }

    /**
     * calculate the average wam of the matched list
     *
     * @param allMatchAppList a 2D arraylist of the matched list
     * @return formatted average wam of the matched list
     */
    private String calculateAverMatchWam(ArrayList<ArrayList<String>> allMatchAppList){

        float averMatchWam = 0.0F;
        float sum = 0.0F;
        int n = 0;

        for (int i = 0; i < allMatchAppList.size(); i++){

            float fourGradeSum = 0.0F;

            if (!allMatchAppList.get(i).get(7).equals("n/a")
                    && !allMatchAppList.get(i).get(8).equals("n/a")
                    && !allMatchAppList.get(i).get(9).equals("n/a")
                    && !allMatchAppList.get(i).get(10).equals("n/a") ){

                fourGradeSum += (Float.parseFloat(allMatchAppList.get(i).get(7))
                        + Float.parseFloat(allMatchAppList.get(i).get(8))
                        + Float.parseFloat(allMatchAppList.get(i).get(9))
                        + Float.parseFloat(allMatchAppList.get(i).get(10)))/(float)4;

                sum += fourGradeSum;
                n++;

            }
        }

        averMatchWam = sum/(float)n;

        String formattedAverMatchWam = null;

        if (n == 0){
            formattedAverMatchWam = "0.00";

        } else{
            formattedAverMatchWam = String.format("%.02f", averMatchWam);

        }

        return formattedAverMatchWam;

    }

    /**
     * calculate the average wam of the matched list and unmatched list
     *
     * @param allMatchAppList a 2D arraylist of the matched list
     * @param allUnMatchAppList a 2D arraylist of the unmatched list
     * @return formatted average wam of the matched list and unmatched list
     */
    private String calculateAverTotalWam(ArrayList<ArrayList<String>> allMatchAppList,
                                         ArrayList<ArrayList<String>> allUnMatchAppList){

        float averTotalWam = 0.0F;
        float sum = 0.0F;
        int n = 0;

        // for matched list
        for (int i = 0; i < allMatchAppList.size(); i++){

            float fourGradeSum = 0.0F;

            if (!allMatchAppList.get(i).get(7).equals("n/a")
                    && !allMatchAppList.get(i).get(8).equals("n/a")
                    && !allMatchAppList.get(i).get(9).equals("n/a")
                    && !allMatchAppList.get(i).get(10).equals("n/a") ){

                fourGradeSum += (Float.parseFloat(allMatchAppList.get(i).get(7))
                        + Float.parseFloat(allMatchAppList.get(i).get(8))
                        + Float.parseFloat(allMatchAppList.get(i).get(9))
                        + Float.parseFloat(allMatchAppList.get(i).get(10)))/(float)4;

                sum += fourGradeSum;
                n++;

            }
        }

        // for unmatched list
        for (int i = 0; i < allUnMatchAppList.size(); i++){

            float fourGradeSum = 0.0F;

            if (!allUnMatchAppList.get(i).get(7).equals("n/a")
                    && !allUnMatchAppList.get(i).get(8).equals("n/a")
                    && !allUnMatchAppList.get(i).get(9).equals("n/a")
                    && !allUnMatchAppList.get(i).get(10).equals("n/a") ){

                fourGradeSum += (Float.parseFloat(allUnMatchAppList.get(i).get(7))
                        + Float.parseFloat(allUnMatchAppList.get(i).get(8))
                        + Float.parseFloat(allUnMatchAppList.get(i).get(9))
                        + Float.parseFloat(allUnMatchAppList.get(i).get(10)))/(float)4;

                sum += fourGradeSum;
                n++;

            }
        }

        averTotalWam = sum/(float)n;

        String formattedAverTotalWam = null;

        if (n == 0){
            formattedAverTotalWam = "0.00";

        } else{
            formattedAverTotalWam = String.format("%.02f", averTotalWam);

        }


        return formattedAverTotalWam;
    }

    /**
     * calculate the gender proportion of the matched list and unmatched list
     *
     * @param allMatchAppList a 2D arraylist of the matched list
     * @param allUnMatchAppList a 2D arraylist of the unmatched list
     * @param genderType the type of the gender : male/female/other
     * @return formatted gender proportion of the matched list and unmatched list
     */
    private String calculateGenderProportion(ArrayList<ArrayList<String>> allMatchAppList,
                                             ArrayList<ArrayList<String>> allUnMatchAppList,
                                             String genderType){

        float genderProportion = 0.0F;
        float sumOfMatch = 0.0F;
        int n = 0;

        // for matched list
        for (int i = 0; i < allMatchAppList.size(); i++){

            if (!allMatchAppList.get(i).get(5).equals("n/a")){

                if (genderType.equals("male")){

                    if (allMatchAppList.get(i).get(5).equals("male")){
                        sumOfMatch ++;
                        n++;
                    }

                }else if (genderType.equals("female")){

                    if (allMatchAppList.get(i).get(5).equals("female")){
                        sumOfMatch ++;
                        n++;
                    }

                } else if (genderType.equals("other")){

                    if (allMatchAppList.get(i).get(5).equals("other")){
                        sumOfMatch ++;
                        n++;
                    }

                }
            }
        }

        // add total num from unmatched list
        for (int i = 0; i < allUnMatchAppList.size(); i++){

            if (!allUnMatchAppList.get(i).get(5).equals("n/a")){

                if (genderType.equals("male")){

                    if (allUnMatchAppList.get(i).get(5).equals("male")){
                        n++;
                    }

                }else if (genderType.equals("female")){

                    if (allUnMatchAppList.get(i).get(5).equals("female")){
                        n++;
                    }

                }else if (genderType.equals("other")){

                    if (allUnMatchAppList.get(i).get(5).equals("other")){
                        n++;
                    }

                }
            }
        }

        genderProportion = sumOfMatch/(float)n;

        String formattedGenderProportion = null;

        if (n == 0){
            formattedGenderProportion = "0.00";

        } else{
            formattedGenderProportion = String.format("%.02f", genderProportion);

        }

        return formattedGenderProportion;

    }

    /**
     * calculate the degree proportion of the matched list and unmatched list
     *
     * @param allMatchAppList a 2D arraylist of the matched list
     * @param allUnMatchAppList a 2D arraylist of the unmatched list
     * @param degreeType the type of the degree: PHD/Master/Bechelor
     * @return the formatted degree proportion of the matched list and unmatched list
     */
    private String calculateDegreeProportion(ArrayList<ArrayList<String>> allMatchAppList,
                                             ArrayList<ArrayList<String>> allUnMatchAppList,
                                             String degreeType){

        float degreeProportion = 0.0F;
        float sumOfMatch = 0.0F;
        int n = 0;

        // for matched list
        for (int i = 0; i < allMatchAppList.size(); i++){

            if (!allMatchAppList.get(i).get(6).equals("n/a")){

                if (degreeType.equals("PHD")){

                    if (allMatchAppList.get(i).get(6).equals("PHD")){
                        sumOfMatch ++;
                        n++;
                    }

                }else if (degreeType.equals("Master")){

                    if (allMatchAppList.get(i).get(6).equals("Master")){
                        sumOfMatch ++;
                        n++;
                    }

                } else if (degreeType.equals("Bachelor")){

                    if (allMatchAppList.get(i).get(6).equals("Bachelor")){
                        sumOfMatch ++;
                        n++;
                    }

                }
            }
        }


        // add total num from unmatched list
        // (The total number of people with degree in the unmatched list)
        for (int i = 0; i < allUnMatchAppList.size(); i++){

            if (!allUnMatchAppList.get(i).get(6).equals("n/a")){

                n++;

            }
        }

        degreeProportion = sumOfMatch/(float)n;

        String formattedDegreeProportion = null;

        if (n == 0){
            formattedDegreeProportion = "0.00";

        } else{
            formattedDegreeProportion = String.format("%.02f", degreeProportion);

        }

        return formattedDegreeProportion;

    }

    /**
     * store the all unmatched applicants into an 2D arraylist
     *
     * @param applicantJobSelectArray an array contains the applicant with job selection
     * @param wholeMatchedList an 2D arraylist of all matched applicant
     * @param wholeUnMatchedList  an 2D arraylist of all unmatched applicant
     * @param applicantNum the number of applicants
     * @return an 2D arraylist of all unmatched applicants
     */
    private ArrayList<ArrayList<String>> storeAllUnmatchedApp(String[] applicantJobSelectArray,
                                                              ArrayList<ArrayList<String>> wholeMatchedList,
                                                              ArrayList<ArrayList<String>> wholeUnMatchedList,
                                                              int applicantNum){

        ArrayList<ArrayList<String>> wholeAppList = new ArrayList<>();

        for (int k = 1; k <= applicantNum; k++) {

            // spilt the row
            String[] appJobStr = applicantJobSelectArray[k].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            ArrayList<String> eachApp = new ArrayList<>();

            for (int s = 0; s < appJobStr.length - 1; s++) {

                if (appJobStr[s].length() == 0) {
                    appJobStr[s] = "n/a";
                }

                eachApp.add(appJobStr[s]);

            }

            wholeAppList.add(eachApp);

        }

        if (wholeAppList.size() != 0){
            for (ArrayList<String> w: wholeAppList){
                int count = 0;

                for(ArrayList<String> m: wholeMatchedList){

                    if (w.get(0).equals(m.get(0))){
                        count++;
                    }

                }

                if (count == 0){
                    wholeUnMatchedList.add(w);
                }
            }
        }

        return wholeUnMatchedList;
    }

}


// Command for test in terminal
// javac *.java
// java HRAssistant

// java HRAssistant --help
// java HRAssistant --h

// java HRAssistant -r applicant -j jobs.csv -a applications.csv
// java HRAssistant -a applications.csv -j jobs.csv -r applicant
// java HRAssistant --applications applications.csv --jobs jobs.csv --role applicant

// java HRAssistant -r applicant
// java HRAssistant -r hr

// java HRAssistant -r hr -a applications.csv
// java HRAssistant -r audit -a applications.csv