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
import java.util.Stack;

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
        
       /* for (Miasto miasto : listaMiast) {
            System.out.println(miasto.x + " | " + miasto.y);
        }*/
        //System.out.println(magazyn.x + "mag" + magazyn.y);
        System.out.println("Ile jest miast: " + ileMiast);
        
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
        
        for (Ciezaroweczka pojazd : listaCiezarowek) {
            wyliczDlugoscTrasy(pojazd);
        }      
        wypiszCiezarowki();
        for (Ciezaroweczka pojazd : listaCiezarowek) {
            komiwojazerCiezarowka(pojazd);
        }
 
        wypiszCiezarowki();
    }
    
    public void startObliczen(){
        System.out.println(sumaTras());
    }
    
    public void komiwojazerCiezarowka(Ciezaroweczka aktualnyPojazd){
        int iloscMiastWTrasie = 0;
        
        for(int i = 0; i <=k; i++){
            if(aktualnyPojazd.listaMiastDoOdwiedzenia[i]!=-2)
                iloscMiastWTrasie++;
        }
        aktualnyPojazd.iloscMiastWTrasie = iloscMiastWTrasie;
        
        float macierzSasiedztwa[][] = new float[iloscMiastWTrasie + 1][iloscMiastWTrasie + 1];

        for (int i = 1; i <= iloscMiastWTrasie; i++) {
            for (int j = 1; j <= iloscMiastWTrasie; j++) {
                Miasto miastoA = new Miasto();
                Miasto miastoB = new Miasto();
                if(aktualnyPojazd.listaMiastDoOdwiedzenia[i-1] == -1){
                    miastoA = magazyn;
                } else {
                    miastoA = listaMiast.get(aktualnyPojazd.listaMiastDoOdwiedzenia[i-1]);
                }
                
                if(aktualnyPojazd.listaMiastDoOdwiedzenia[j-1] == -1){
                    miastoB = magazyn;
                } else {
                    miastoB = listaMiast.get(aktualnyPojazd.listaMiastDoOdwiedzenia[j-1]);
                }

                macierzSasiedztwa[i][j] = odlegloscMiast(miastoA, miastoB);
            }
        }
        
        /*for (int i = 1; i <= iloscMiastWTrasie; i++) {
            for (int j = 1; j <= iloscMiastWTrasie; j++) {
                System.out.print(macierzSasiedztwa[i][j] + " ");
            }
            System.out.println();
        }*/
        
        wyliczenieKomiwojazera(macierzSasiedztwa, aktualnyPojazd);
    }
    
    private void wyliczenieKomiwojazera(float adjacencyMatrix[][], Ciezaroweczka obliczanyPojazd){
        Stack<Integer> stos = new Stack<>();
        
        int liczbaWezlow = adjacencyMatrix[1].length - 1;
        int[] odwiedzone = new int[liczbaWezlow + 1];
        
        odwiedzone[1] = 1;
        stos.push(1);
        
        int element, i;
        int dst = 0;
        float min = Float.MAX_VALUE;
        boolean minFlag = false;
        
        int[] nowaKolejnosc = new int[k + 1];
        int pozycja = 1;
        for(int j=1;j<=k;j++){
            nowaKolejnosc[j]= -2;
        }
        nowaKolejnosc[0] = 0;
       
        //System.out.print(1 + "\t");

        while (!stos.isEmpty()) {
            element = stos.peek();
            i = 1;
            min = Float.MAX_VALUE;

            while (i <= liczbaWezlow) {
                if (adjacencyMatrix[element][i] > 1 && odwiedzone[i] == 0) {
                    if (min > adjacencyMatrix[element][i]) {
                        min = adjacencyMatrix[element][i];
                        dst = i;
                        minFlag = true;
                    }
                }
                i++;
            }

            if (minFlag) {
                odwiedzone[dst] = 1;
                stos.push(dst);
                /*System.out.print(dst + "\t");*/ nowaKolejnosc[pozycja]= dst-1; ++pozycja;
                minFlag = false;
                continue;
            }
            stos.pop();
        }
        
        System.out.println();
        
        /*for(int j=0;j<=k;j++){
            System.out.print(nowaKolejnosc[j] + "\t");
        }
        System.out.println();*/
        
        nowaKolejnosc[0] = -1;
        for (int j = 1; j <= k; j++) {
            if (nowaKolejnosc[j] != -2) {
                nowaKolejnosc[j] = obliczanyPojazd.listaMiastDoOdwiedzenia[nowaKolejnosc[j]];
            }
        }

        for (int j = 0; j <= k; j++) {
            obliczanyPojazd.listaMiastDoOdwiedzenia[j] = nowaKolejnosc[j];
        }
        
        wyliczDlugoscTrasy(obliczanyPojazd);
    }
    
    public void wypiszCiezarowki() {
        int numerCiezarowki = 1;
        
        for (Ciezaroweczka pojazd : listaCiezarowek) {
            System.out.println("[" + pojazd.iloscMiastWTrasie + "]" + "(" + pojazd.dlugoscTrasy + ")" + "Ciężarówka numer " + numerCiezarowki + ":");
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

    private void wyliczDlugoscTrasy(Ciezaroweczka pojazd) {
        pojazd.dlugoscTrasy = 0;
        pojazd.dlugoscTrasy = odlegloscMiast(magazyn, listaMiast.get(pojazd.listaMiastDoOdwiedzenia[1]));
        
        for(int i=1; i<(k-1); ++i){
            if(pojazd.listaMiastDoOdwiedzenia[i+1] != -2){
                pojazd.dlugoscTrasy += odlegloscMiast(listaMiast.get(pojazd.listaMiastDoOdwiedzenia[i]), listaMiast.get(pojazd.listaMiastDoOdwiedzenia[i+1]));
            } else{
                System.out.println("ddddddd");
                pojazd.dlugoscTrasy += odlegloscMiast(listaMiast.get(pojazd.listaMiastDoOdwiedzenia[i]), magazyn);
                break;
            }
        }
    }

    private float sumaTras() {
        float suma = 0;
        int ilePojazdow=0;
        
        for (Ciezaroweczka pojazd : listaCiezarowek){
            suma += pojazd.dlugoscTrasy;
            ilePojazdow++;
        }
        
        System.out.println("Ile pojazdow na trasach: " + ilePojazdow);
        
        return suma;
    }
}