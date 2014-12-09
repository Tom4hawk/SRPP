/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srpp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tom4hawk
 */
public class SimulatedAnnealing {
    public Integer k;//liczba miast maksymalnie do odwiedzenia przez jedną ciężarówkę
    private List<Miasto> listaMiast;//lista miast wraz z ich koordynatami
    private List<Ciezaroweczka> listaCiezarowek;//lista ciezarowek wraz z trasami przypisanymi do kazdej
    private Miasto magazyn;
    private int ileMiast;//liczba miast minus jeden(żeby się indeksy z Arraylist zgadzały)
    
    public void readFile(String pathToFile) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(pathToFile));
        String currentLine;
        String[] currentLineNumbers;
        
        ileMiast = (-1);
        listaMiast = new ArrayList<>();
        k = Integer.parseInt(br.readLine());
        
        currentLineNumbers = br.readLine().trim().split("\\s+");
        magazyn = new Miasto();
        magazyn.x = Integer.parseInt(currentLineNumbers[0]);
        magazyn.y = Integer.parseInt(currentLineNumbers[1]);
        magazyn.index = -1;
        

        while ((currentLine = br.readLine()) != null) {
            Miasto aktualneMiasto = new Miasto();
            ileMiast++;
            currentLineNumbers = currentLine.trim().split("\\s+");
            
            aktualneMiasto.x = Integer.parseInt(currentLineNumbers[0]);
            aktualneMiasto.y = Integer.parseInt(currentLineNumbers[1]);
            aktualneMiasto.index = ileMiast;
            
            listaMiast.add(aktualneMiasto);
        }
        
        for (Miasto miasto : listaMiast) {
            System.out.println(miasto.x + " | " + miasto.y);
        }
        System.out.println(ileMiast);
        System.out.println(magazyn.x + "mag" + magazyn.y);
        br.close();
    }
    
    public void pierwszeWybranieMiast() {
        int indeksyMiast = 0;
        listaCiezarowek = new ArrayList<>();
 
        for (int i = 0; i < ceil(((float)ileMiast + 1) / k); i++) {
            Ciezaroweczka aktualnaCiezarowka = new Ciezaroweczka();
            aktualnaCiezarowka.listaMiastDoOdwiedzenia = new int[k + 1];
            aktualnaCiezarowka.listaMiastDoOdwiedzenia[0] = -1;
            for (int j = 1; j <= k; j++) {
                if (indeksyMiast <= ileMiast) {
                    aktualnaCiezarowka.listaMiastDoOdwiedzenia[j] = indeksyMiast;
                    indeksyMiast++;
                } else {
                    aktualnaCiezarowka.listaMiastDoOdwiedzenia[j] = -2;
                }
            }
            listaCiezarowek.add(aktualnaCiezarowka);
        }
    }
    
    public void wypiszCiezarowki() {
        int numerCiezarowki = 1;
        
        for (Ciezaroweczka pojazd : listaCiezarowek) {
            System.out.println("Ciężarówka numer " + numerCiezarowki + ":");
            for (int i = 0; i <= k; ++i) {
                System.out.print(" " + pojazd.listaMiastDoOdwiedzenia[i]);
            }
            System.out.println();
            ++numerCiezarowki;
        }
    }
    
    private float odlegloscMiast(Miasto pierwsze, Miasto drugie){
        float dx = drugie.x - pierwsze.x;
        float dy = drugie.y - pierwsze.y;
        return (float) sqrt(dx * dx + dy * dy);
    }
}