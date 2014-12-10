/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srpp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tom4hawk
 */
public class SRPP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            SimulatedAnnealing problem = new SimulatedAnnealing();
            String file = args[0];
            try {
                problem.readFile(file);
                problem.pierwszeWybranieMiast();
                problem.wypiszCiezarowki();
                problem.startObliczen();
                problem.wypiszCiezarowki();
            } catch (IOException ex) {
                Logger.getLogger(SRPP.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Nastąpił niespodziewany problem");
            }
        } else{
            System.out.println("Nie pododano pliku do wczytania.");
        }
        

    }
    
    
}
