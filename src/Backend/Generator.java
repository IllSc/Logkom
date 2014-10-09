package Backend;

import Exception.NoOtherSolutionException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ivan
 */
public class Generator {

    //Number of Squares used
    int numOfVariables;

    //List of clauses
    ArrayList<String> clauses;

    public void generateAlternatives() {
        String resSAT = null;
        try {

            File file = new File("output.txt");
            Scanner scan = new Scanner(file);
            while (scan.hasNext()) {
                resSAT = scan.nextLine();
            }
            String[] resInt = resSAT.split(" ");

            if (resInt[0].equals("UNSAT")) {

            } else {
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new FileWriter("kotak.cnf", true)));
                String newLiterals = "";

                for (int i = 0; i < resInt.length - 1; i++) {
                    newLiterals += (0 - Integer.parseInt(resInt[i])) + " ";
                }
                newLiterals += "0";
                out.println(newLiterals);
                out.close();
                String command = "minisat " + "kotak.cnf " + "output.txt";
                executeCommand(command);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "You have not generate any solution");
            
        }

    }

    public void generateSolutions() throws FileNotFoundException, UnsupportedEncodingException {
        generateRowExistenceConstraint();
        generateRowUniqueConstraint();
        generateColumnUniqueConstraint();
        generateRightDiagonalConstraint();
        generateLeftDiagonalConstraint();

        printLiterals();
    }

    public void executeCommand(String command) {

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();

        } catch (IOException | InterruptedException e) {
        }

    }

    public Generator(int numOfVariables) {
        this.numOfVariables = numOfVariables;
        this.clauses = new ArrayList<String>();
    }

    public void printLiterals() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("kotak.cnf", "UTF-8");
        String header = "p cnf " + (numOfVariables * numOfVariables) + " " + clauses.size();
        writer.println(header);
        for (int i = 0; i < clauses.size(); i++) {
            writer.println(clauses.get(i));

        }
        writer.close();
        String command = "minisat " + "kotak.cnf " + "output.txt";
        executeCommand(command);

    }
    /*
     Method used to generate constraint that each columns must have no more than one
     queen
     */

    public void generateColumnUniqueConstraint() {
        String clause = "";

        for (int i = 1; i <= (numOfVariables * numOfVariables); i++) {

            for (int j = i + numOfVariables; j <= (numOfVariables * numOfVariables); j += numOfVariables) {
                clause = (0 - i) + " " + (0 - j) + " 0";
                clauses.add(clause);
            }
        }

        return;
    }
    /*
     Method used to generate constraint that each row must have no more than one
     queen
     */

    public void generateRowUniqueConstraint() {
        String clause = "";

        for (int i = 1; i <= (numOfVariables * numOfVariables); i++) {
            int constraint = (((i - 1) / numOfVariables) + 1) * numOfVariables;
            for (int j = i + 1; j <= constraint; j++) {
                clause = (0 - i) + " " + (0 - j) + " 0";
                clauses.add(clause);
            }
        }

        return;
    }
    /*
     Method used to generate constraint that each row must have one queen
     */

    public void generateRowExistenceConstraint() {
        String clause = "";

        for (int i = 1; i <= (numOfVariables * numOfVariables); i++) {
            clause = clause + i + " ";
            if (i % numOfVariables == 0) {
                clause += "0";
                clauses.add(clause);
                clause = "";
            }
        }

        return;
    }
    /*
     Method used to generate constraint that on a right diagonal line.
     There must be no more than one queen
     */

    public void generateRightDiagonalConstraint() {
        String clause = "";
        for (int i = 1; i < (numOfVariables * numOfVariables); i++) {
            int base1 = ((i - 1) / numOfVariables) + 1;
            int base2 = ((i - 1) % numOfVariables) + 1;
            while (base1 < numOfVariables && base2 < numOfVariables) {

                int j = (base1) * numOfVariables + base2 + 1;
                clause = (0 - i) + " " + (0 - j) + " 0";
                clauses.add(clause);
                base1 += 1;
                base2 += 1;

            }
        }
    }
    /*
     Method used to generate constraint that on a left diagonal line.
     There must be no more than one queen
     */

    public void generateLeftDiagonalConstraint() {
        for (int i = 2, j = 1; i <= numOfVariables; i++, j++) {
            int[] list = new int[j + 1];
            for (int k = 0, l = i; k < list.length; k++, l += (numOfVariables - 1)) {
                list[k] = l;
                System.out.println("");
            }
            for (int k = 0; k < list.length; k++) {
                for (int l = k + 1; l < list.length; l++) {
                    String clause = (0 - list[k]) + " " + (0 - list[l]) + " 0";
                    clauses.add(clause);
                }
            }
        }
        for (int i = (numOfVariables * (numOfVariables - 1)), j = 1; i > numOfVariables; i -= numOfVariables, j++) {
            int[] list = new int[j + 1];
            for (int k = 0, l = i; k < list.length; k++, l += (numOfVariables - 1)) {
                list[k] = l;
                System.out.println("");
            }
            for (int k = 0; k < list.length; k++) {
                for (int l = k + 1; l < list.length; l++) {
                    String clause = (0 - list[k]) + " " + (0 - list[l]) + " 0";
                    clauses.add(clause);
                }
            }
        }
    }

    /*
     public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

     for (int i = 5; i <= 8; i++) {
     Generator generator = new Generator(i);
     long start = System.currentTimeMillis();
     generator.generateRowExistenceConstraint();
     generator.generateRowUniqueConstraint();
     generator.generateColumnUniqueConstraint();
     generator.generateRightDiagonalConstraint();
     generator.generateLeftDiagonalConstraint();
     long end = System.currentTimeMillis();
            
     generator.printLiterals();
     }
     }
     */
}
