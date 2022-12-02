import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

/**
 * COMP90041, Sem2, 2022: Final Project
 * @author
 * Name: Shiming ZHENG
 * Email: shimzheng@student.unimelb.edu.au
 * Student number: 1149897
 */

/**
 * This class contains the logic for the matchmaking algorithm
 */
public class Matchmaker {

    /**
     * the default constructor of the Matchmaker class
     */
    public Matchmaker() {
    }

    /**
     * calculate total matching credits for each applicant
     *
     * @param eachJob a string of each job
     * @param eachAppMatchList a 2D arraylist of matched list for each job
     * @return a 2D arraylist of matched list sorted by total matching credits
     */
    ArrayList<ArrayList<String>> calculateEachAppTotalCredit(String eachJob,
                                                             ArrayList<ArrayList<String>> eachAppMatchList) {

        ArrayList<ArrayList<String>> appWithTotalCreditList = new ArrayList<>(eachAppMatchList);

        // Calculate the gender proportion[male, female, other] for the applicant list selected this job
        ArrayList<Float> eachAppListGenderPro = calculateGenderPro(eachAppMatchList);
        float genderProBenchmark = 1/(float)eachAppListGenderPro.size();

        // Calculate the age proportion [18-34, 35-69, 70-100] for the applicant list selected this job
        ArrayList<Float> eachAppListAgePro = calculateAgePro(eachAppMatchList);
        float ageProBenchmark = 1/(float)eachAppListAgePro.size();

        //eachJob String to array
        String[] eachJobStr = eachJob.split(",");

        // 1) Calculate the credit by measuring [job Title, job description] with [career summary]
        //eachJob's Job title String to array
        String[] jobTitle = eachJobStr[1].split(" ");

        //eachJob's Job description String to array
        String[] jobDescription = eachJobStr[2].split("[\\p{Punct}\\s]+");

        for (int i = 0; i < eachAppMatchList.size(); i++){

            int totalCredit = 0;

            // each applicant Arraylist to array
            String[] eachAppMatchArr = eachAppMatchList.get(i).toArray(new String[0]);

            // each applicant's career Summary
            String[] eachAppMatchArrEle = eachAppMatchArr[3].split("[\\p{Punct}\\s]+");

            // calculate credits by matching job title and career summary
            totalCredit = calByCareerSummary(jobTitle, eachAppMatchArrEle, totalCredit);

            // calculate credits by matching job description and career summary
            totalCredit = calByCareerSummary(jobDescription, eachAppMatchArrEle, totalCredit);

            // 2) Calculate the credit by Highest Degree (only consider applicants)
            // each applicant's Highest Degree
            totalCredit = calByMatchDegree(eachAppMatchArr, totalCredit);

            // 3) Calculate the credit by compWAM  (only consider applicants)
            totalCredit = calByWam(eachAppMatchArr, totalCredit);

            // 4) Calculate the credit by matching job salary
            // and application's salary expectation*(100%,110%)
            totalCredit = calByMatchSalary(eachJobStr, eachAppMatchArr, totalCredit);

            // 5) Balance the credit by matching job startDate and application availability
            totalCredit = calByMatchStartDate(eachJobStr, eachAppMatchArr, totalCredit);

            // 6) Balance/Calculate the credit by the gender [male, female, other]
            totalCredit = calByBalanceGenderPro(eachAppListGenderPro, genderProBenchmark,
                    eachAppMatchArr, totalCredit);

            // 7) Balance the credit by age proportion [18-34, 35-69, 70-100]
            totalCredit = calByBalanceAgePro(eachAppListAgePro, ageProBenchmark,
                    eachAppMatchArr, totalCredit);


            // add the total credit into list
            appWithTotalCreditList.get(i).add(13, String.valueOf(totalCredit));

        }

        // only the applicant with >0 totalCredit can be sorted
        ArrayList<ArrayList<String>> appWithTotalCreditLargerZeroList = new ArrayList<>();

        for (ArrayList<String> c: appWithTotalCreditList){

            if (Integer.parseInt(c.get(13)) > 0){
                appWithTotalCreditLargerZeroList.add(c);
            }

        }

        // sort the list by totalCredit (Descending order)
        ArrayList<ArrayList<String>> sortAppTotalCreditList = new ArrayList<>(
                sortByTotalCredit(appWithTotalCreditLargerZeroList));

        return sortAppTotalCreditList;

    }

    /**
     * calculate the gender proportion[male, female, other] for the applicant list selected this job
     *
     * @param eachAppMatchList a 2D arraylist of matched list for each job
     * @return an arraylist contains the gender proportion[male, female, other]
     * for the applicant list selected this job
     */
    private ArrayList<Float> calculateGenderPro(ArrayList<ArrayList<String>> eachAppMatchList){

        ArrayList<Float> eachAppListGenderPro = new ArrayList<>();

        int maleSum = 0;
        int femaleSum = 0;
        int otherSum = 0;

        float malePro = 0.0F;
        float femalePro = 0.0F;
        float otherPro = 0.0F;

        int n = eachAppMatchList.size();

        for (ArrayList<String> a: eachAppMatchList){

            if (!a.get(5).equals("n/a")){

                if (a.get(5).equals("male")){
                    maleSum++;

                } else if (a.get(5).equals("female")){
                    femaleSum++;

                } else if (a.get(5).equals("other")){
                    otherSum++;

                }

            }


        }

        malePro = maleSum/(float)n;
        femalePro = femaleSum/(float)n;
        otherPro = otherSum/(float)n;

        eachAppListGenderPro.add(malePro);
        eachAppListGenderPro.add(femalePro);
        eachAppListGenderPro.add(otherPro);

        return eachAppListGenderPro;
    }

    /**
     * calculate the age proportion[18-34, 35-69, 70-100] for the applicant list selected this job
     *
     * @param eachAppMatchList a 2D arraylist of matched list for each job
     * @return an arraylist of the age proportion[18-34, 35-69, 70-100]
     * for the applicant list selected this job
     */
    private ArrayList<Float> calculateAgePro(ArrayList<ArrayList<String>> eachAppMatchList){

        ArrayList<Float> eachAppListAgePro = new ArrayList<>();

        int ageSum_18_34 = 0;
        int ageSum_35_69 = 0;
        int ageSum_70_100 = 0;

        float ageSum_18_34_pro = 0.0F;
        float ageSum_35_69_pro = 0.0F;
        float ageSum_70_100_pro = 0.0F;

        int n = eachAppMatchList.size();

        for (ArrayList<String> a: eachAppMatchList){

            if (!a.get(4).equals("n/a")){

                int age = Integer.parseInt(a.get(4));

                if (age >= 18 && age < 35){
                    ageSum_18_34++;

                } else if (age >= 35 && age < 70){
                    ageSum_35_69++;

                } else if (age >= 70 && age <= 100){
                    ageSum_70_100++;

                }

            }

        }

        ageSum_18_34_pro = ageSum_18_34/(float)n;
        ageSum_35_69_pro = ageSum_35_69/(float)n;
        ageSum_70_100_pro = ageSum_70_100/(float)n;

        eachAppListAgePro.add(ageSum_18_34_pro);
        eachAppListAgePro.add(ageSum_35_69_pro);
        eachAppListAgePro.add(ageSum_70_100_pro);

        return eachAppListAgePro;
    }

    /**
     * calculate the credit by measuring [job Title, job description] with [career summary]
     *
     * @param jobTitleOrDescription an array contains job title or description
     * @param eachAppMatchArrEle an array contains each matched applicant
     * @param totalCredit the current total credits
     * @return the updated total credits
     */
    private int calByCareerSummary(String[] jobTitleOrDescription,
                                   String[] eachAppMatchArrEle,
                                   int totalCredit){

        for (String t: jobTitleOrDescription){
            t = t.toLowerCase();

            for (String a: eachAppMatchArrEle) {
                a = a.toLowerCase();

                if (!a.equals("n") && !a.equals("a")
                        && !t.equals("n") && !t.equals("a")){

                    if (a.equals(t)) {
                        totalCredit++;

                    }
                }
            }
        }

        return totalCredit;
    }

    /**
     * calculate the credit by Highest Degree (only consider applicants)
     *
     * @param eachAppMatchArr a 2D arraylist of matched list for each job
     * @param totalCredit the current total credits
     * @return the updated total credits
     */
    private int calByMatchDegree(String[] eachAppMatchArr, int totalCredit){

        if (eachAppMatchArr[6].equals("PHD")){
            totalCredit += 3;

        } else if (eachAppMatchArr[6].equals("Master")){
            totalCredit += 2;

        } else if (eachAppMatchArr[6].equals("Bachelor")){
            totalCredit += 1;

        }

        return totalCredit;

    }

    /**
     * calculate the credit by compWAM (only consider applicants)
     *
     * @param eachAppMatchArr an array contains each matched applicant
     * @param totalCredit the current total credits
     * @return the updated total credits
     */
    private int calByWam(String[] eachAppMatchArr, int totalCredit){

        if (!eachAppMatchArr[7].equals("n/a") && !eachAppMatchArr[8].equals("n/a") &&
                !eachAppMatchArr[9].equals("n/a") && !eachAppMatchArr[10].equals("n/a")) {

            float compWam = (Float.parseFloat(eachAppMatchArr[7])
                    + Float.parseFloat(eachAppMatchArr[8])
                    + Float.parseFloat(eachAppMatchArr[9])
                    + Float.parseFloat(eachAppMatchArr[10])) / (float) 4;

            if (compWam >= 80.0) {
                totalCredit += 5;

            } else if (compWam >= 75 && compWam <= 79) {
                totalCredit += 4;

            } else if (compWam >= 70 && compWam <= 74) {
                totalCredit += 3;

            } else if (compWam >= 65 && compWam <= 69) {
                totalCredit += 2;

            } else if (compWam >= 49 && compWam <= 64) {
                totalCredit += 1;

            }
        }

        return totalCredit;

    }

    /**
     * calculate the credit by matching job salary and application's salary expectation*(100%,110%)
     *
     * @param eachJobStr an array contains each job
     * @param eachAppMatchArr an array contains each matched applicant
     * @param totalCredit the current total credits
     * @return the updated total credits
     */
    private int calByMatchSalary(String[] eachJobStr, String[] eachAppMatchArr, int totalCredit){

        if (!eachJobStr[4].equals("n/a") && !eachAppMatchArr[11].equals("n/a")){

            float eachJobSalary = Float.parseFloat(eachJobStr[4]);
            float eachAppSalary = Float.parseFloat(eachAppMatchArr[11]);

            if (eachAppSalary <= eachJobSalary){
                totalCredit += 2;

            } else if (eachAppSalary*1.1 >= eachJobSalary){
                totalCredit += 1;

            }

        }

        return totalCredit;

    }

    /**
     * balance the credit by matching job startDate and application availability
     *
     * @param eachJobStr an array contains each job
     * @param eachAppMatchArr an array contains each matched applicant
     * @param totalCredit the current total credits
     * @return the updated total credits
     */
    private int calByMatchStartDate(String[] eachJobStr,
                                    String[] eachAppMatchArr,
                                    int totalCredit){

        if (!eachJobStr[5].equals("n/a") && !eachAppMatchArr[12].equals("n/a")){

            DateFormat df = new SimpleDateFormat("dd/MM/yy");

            Calendar cal = Calendar.getInstance();

            try{
                //  job startDate
                cal.setTime(df.parse(eachJobStr[5]));
                Date jobStartDate = cal.getTime();

                cal.setTime(df.parse(eachAppMatchArr[12]));
                Date appAvailableDate = cal.getTime();

                //compareTo (Result > 0 The former date is late))
                if(jobStartDate.compareTo(appAvailableDate) > 0) {
                    totalCredit ++;
                }

            } catch (ParseException e) {
                System.out.println("An error in parse the date");
            }

        }

        return totalCredit;
    }

    /**
     * balance/calculate the credit by the gender [male, female, other]
     *
     * @param eachAppListGenderPro the gender proportion in each application list for each job
     * @param genderProBenchmark the benchmark to balance the gender proportion
     * @param eachAppMatchArr an array contains each matched applicant
     * @param totalCredit the current total credits
     * @return the updated total credits
     */
    private int calByBalanceGenderPro(ArrayList<Float> eachAppListGenderPro,
                                      float genderProBenchmark,
                                      String[] eachAppMatchArr,
                                      int totalCredit){

        if (eachAppListGenderPro.get(0) < genderProBenchmark){ //low male proportion
            if (eachAppMatchArr[5].equals("male")){
                totalCredit++;
            }

        }

        if (eachAppListGenderPro.get(1) < genderProBenchmark){ //low female proportion
            if (eachAppMatchArr[5].equals("female")){
                totalCredit++;
            }

        }

        if (eachAppListGenderPro.get(2) < genderProBenchmark) { //low other gender proportion
            if (eachAppMatchArr[5].equals("other")){
                totalCredit++;
            }

        }

        return totalCredit;
    }

    /**
     * balance the credit by age proportion [18-34, 35-69, 70-100]
     *
     * @param eachAppListAgePro the age proportion in each application list for each job
     * @param ageProBenchmark the benchmark to balance the age proportion
     * @param eachAppMatchArr an array contains each matched applicant
     * @param totalCredit the current total credits
     * @return the updated total credits
     */
    private int calByBalanceAgePro(ArrayList<Float> eachAppListAgePro,
                                   float ageProBenchmark,
                                   String[] eachAppMatchArr,
                                   int totalCredit){

        if (!eachAppMatchArr[4].equals("n/a")) {

            int age = Integer.parseInt(eachAppMatchArr[4]);

            if (eachAppListAgePro.get(0) < ageProBenchmark) { //low age 18-34 proportion
                if (age >= 18 && age < 35) {
                    totalCredit++;

                }

            }

            if (eachAppListAgePro.get(1) < ageProBenchmark) { //low age 35-70 proportion
                if (age >= 35 && age < 70) {
                    totalCredit++;

                }

            }

            if (eachAppListAgePro.get(2) < ageProBenchmark) { //low age 70-100 proportion
                if (age >= 70 && age < 100) {
                    totalCredit++;

                }

            }
        }
        return totalCredit;

    }

    /**
     * rank applicants by total credit
     *
     * @param appWithTotalCreditList a 2D arraylist of applicants with total credits
     * @return a 2D arraylist of applicants sorted by total credits
     */
    private ArrayList<ArrayList<String>> sortByTotalCredit(ArrayList<ArrayList<String>> appWithTotalCreditList){

        ArrayList<ArrayList<String>> sortAppTotalCreditList = new ArrayList<>(appWithTotalCreditList);

        Collections.sort(sortAppTotalCreditList, new Comparator<ArrayList<String>>() {
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {

                int index = 0;

                int creditNum1 = Integer.parseInt(o1.get(13));
                int creditNum2 = Integer.parseInt(o2.get(13));

                if (!Objects.equals(creditNum1, creditNum2)){ // compare by total credit
                    index = -(creditNum1-creditNum2);

                } else { // if same credit: compare by total salary

                    if (!o1.get(11).equals("n/a")){

                        int salaryNum1 = Integer.parseInt(o1.get(11));
                        int salaryNum2 = Integer.parseInt(o2.get(11));

                        if (salaryNum1 != salaryNum2){

                            index = salaryNum1-salaryNum2;

                        } else { // if same salary: compare by application time

                            long unixTime1 = Long.parseLong(o1.get(0));
                            Instant instant1 = Instant.ofEpochSecond(unixTime1);

                            long unixTime2= Long.parseLong(o2.get(0));
                            Instant instant2 = Instant.ofEpochSecond(unixTime2);

                            index = instant1.compareTo(instant2);

                        }

                    }

                }

                return index;

            }
        });

        return sortAppTotalCreditList;

    }


}