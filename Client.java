        
/**
 * Client class for Weather Service
 *
 * @author Ben Carnes
 * @version Mar 31 2021
 */
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client
{
    
    public static void main(String[]args) throws IOException, MalformedURLException{ //main
        
        boolean isQuit = false;
        String quit = "";
        Scanner inKey = new Scanner(System.in);
        
        while(isQuit != true){
            forecast(); 
            
            System.out.println("Quit?(Y/N)");
            quit = inKey.next();
            
            clrscr();   //clear screen
            
            if(quit.equals("Y") || quit.equals("y") ){ //quits loops when input is Y or y
                isQuit = true;
            }
            
        }  

    }  
            
    /**
     * Method clears screen
     */
    public static void clrscr(){    //found this online  Amit Rawat, Sept 19 2019, Java: Clear the console, https://intellipaat.com/community/294/java-clear-the-console
        try{
            if(System.getProperty("os.name").contains("Windows")){    
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
    
            else{
                Runtime.getRuntime().exec("clear");
            }
    
        } catch (IOException | InterruptedException ex) {}

    }
    
    /**
     * Method calls creates obj and forecasts
     */
    public static void forecast()throws IOException, MalformedURLException{ // in a method so it can be called in a loop easier
        String address1 = "";
        String address2 = "";
        
        WeatherService weather = new WeatherService();    //initialize object
        Scanner inKey = new Scanner(System.in);
        

        System.out.println("Please enter the City"); //read input
        address1 = inKey.nextLine();
    
        System.out.println("Please enter the State or Province");
        address2 = inKey.nextLine();

        weather.call(address1, address2);


    }



}
