/**
 * WeatherService provides forecasts based on city and state entered.
 * coorToGrid uses google geocode api, all other calls uses api from National Weather Service
 * 
 * @author Ben Carnes
 * @version Mar 31 2021
 */
import java.net.*;
import java.io.*;
public class WeatherService
{
    //instance variables
    String[] time = new String[10];
    String[] wind = new String[10];
    String[] speed = new String[10];
    String[] forecastDetail = new String[10]; //arrays to hold info 
    
    String[] forecastListRaw = new String[10]; //data from api separated    
    
    private String address,forecast, city, state; //stores address 
    
    private double longitude, latitude;
    private boolean valid; //used for checking for valid input in addressToCoor()
    
    /**
     * Default Constructor (no parameters)
     */
    public WeatherService(){ //default constructor
        
    }
    
    /**
     * Method to call all other methods needed in order to provide forecast
     * @param city city
     * @param state state
     */
    public void call(String city, String state)throws IOException, MalformedURLException{
        this.state = state;
        this.city = city;
        address = removeWhitespace(this.city) + "+" + removeWhitespace(this.state);
        addressToCoor();
        //System.out.println(latitude + "\t" + longitude);  //debug
        if(valid == false){
            System.out.print("Invalid Address");    //if address isn't found
        }
        else{
            coorToGrid();
            forecast();
            //displayRawData(); //debug displays data from api
            parseData();
            displayData();
            reset();    
            
        }
    }
    
    /**
     * Method removes and replaces whitespace with +
     * @param string string to have whitespace replaced
     */
    public String removeWhitespace(String string){
        String address = string;
        address = string.replaceAll("\\s", "+");
        return address;
    }
    
    /**
     * Method sets all instance variables to default of empty
     */
    public void reset(){//set all instance variables to 0 or empty or false
        time = new String[10];
        wind = new String[10];
        speed = new String[10];
        forecastDetail = new String[10];
        
        forecastListRaw = new String[10];
        
        address = "";
        forecast = "";
        city = "";
        state = "";
        
        longitude = 0;
        latitude = 0;
        
        valid = false;
    }
    
    /**
     * Method display final data
     */
    public void displayData(){ // prints elements of time[] wind[] speed[] and forcastDetail[]

        System.out.print("\tForecast for the next five days in: " +city + ", " + state + " \n\n");
        for(int i = 0; i < time.length; i++){   //iterates through time[], arrays should be same length
            System.out.printf("%1s%n", time[i]);
            System.out.printf("%1s   %1s%n","Wind Direction:", wind[i]);
            System.out.printf("%1s   %1s%n","Wind Speed:", speed[i]);
            System.out.printf("%1s   %1s%n%n","Forecast:", forecastDetail[i]);
        }
    }
    
    /**
     * Method gets info from elements of forecastListRaw
     */
    public  void parseData(){ 
        String forecast = "";   //initialize variables
        String name = "";
        String detailedForecast = "";
        String windspeed = "";
        String windDirection = "";
        
        int index = 0;
        
        for(int i = 0; i < forecastListRaw.length; i++){        //iterates through elements of forecastListRaw
            index = forecastListRaw[i].indexOf("name") + 8;
            while(forecastListRaw[i].charAt(index) != '"'){      //creates substring of element      
                name += forecastListRaw[i].charAt(index);
                index++;
            }
            
            index = forecastListRaw[i].indexOf("windSpeed")  + 13;
            while(forecastListRaw[i].charAt(index) != '"'){            
                windspeed +=forecastListRaw[i].charAt(index);
                index++;
            }
            
            index = forecastListRaw[i].indexOf("windDirection")  + 17;
            while(forecastListRaw[i].charAt(index) != '"'){            
                windDirection +=forecastListRaw[i].charAt(index);
                index++;
            }
            
            index = forecastListRaw[i].indexOf("detailedForecast")  + 20;
            while(forecastListRaw[i].charAt(index) != '"'){            
                detailedForecast +=forecastListRaw[i].charAt(index);
                index++;
            }
            
            time[i] = name; //stores info to array
            wind[i] = windDirection;
            speed[i] = windspeed;
            forecastDetail[i] = detailedForecast;
            
            name = "";      //reset variables
            windspeed = "";
            windDirection = "";
            detailedForecast = "";
        }
        
        
        
    }
    
    /**
     * Method displays data from api
     */
    public  void displayRawData(){
        for(int i = 0; i < forecastListRaw.length; i++){
            System.out.println(forecastListRaw[i]);
        }
    }
    
    /**
     * Method reads in forecast from api
     */
    private void forecast()throws IOException, MalformedURLException{
        String URL = coorToGrid(); //call coorToGrid method
        URL url = new URL(URL);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();       //open connection
        con.setRequestMethod("GET"); 
        
        BufferedReader in = new BufferedReader(  new InputStreamReader(con.getInputStream() ) ); //read in using BufferedReader and StringBuffer
        String inputLine;
        StringBuffer content = new StringBuffer();
        

        int counter = 52;
        while (counter != 0 && (inputLine = in.readLine()) != null) {
            counter--;
        }
        
        String temp;
        for(int i = 0; i < forecastListRaw.length; i++){
            for(int k = 0; k < 15; k++){
                inputLine = in.readLine();
                content.append(inputLine);
                content.append("\n");
            }
            temp = content.toString();
            forecastListRaw[i] = temp;
            content.delete(0, content.length()-1 );     //empty content
        }
        in.close();     //close BufferReader in
        
        forecast = content.toString();

    }
    
    /**
     * Method converts address to coordinates
     */
    private void addressToCoor()throws IOException, MalformedURLException{
        valid = true;
        String startUrlStr = "https://maps.googleapis.com/maps/api/geocode/json?address=";  //api url start
        String endUrlStr = "&key=AIzaSyDzZQUgoLqEnu09WYREBd6XlyPP96kA9eQ";      //api url end + key
        URL url = new URL(startUrlStr + address + endUrlStr);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();       //open connection
        con.setRequestMethod("GET");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream() ) ); //read in from api
        String inputLine;
        StringBuffer content = new StringBuffer();

        
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
            content.append("\n");
        }
        in.close();     //close BufferedReader in
        
        
        String coordinatesStr = content.toString();

        if(isValidRequest(coordinatesStr) == true){     //check if address is valid
            getLonLat(coordinatesStr);  //get longitude and latitude
        }
        else{
            valid = false;
        }

    }
    
    /**
     * Method finds longitude and latitude from address
     */
    public void getLonLat(String data){
        
        data = data.substring(data.indexOf("location") );
        data = data.substring(data.indexOf("lat") +7 );

        String lat = "";
        for(int i = 0; i < data.length(); i++){ //goes over string and add numbers to lat, stops if it reaches anything other then a number or . or -
            if(Character.isDigit(data.charAt(i) ) ){
                lat += data.charAt(i);
            }
            else if (data.charAt(i) == '.'){
                lat += data.charAt(i);
            }
            else if (data.charAt(i) == '-'){
                lat += data.charAt(i);
            }
            else{
                break;
            }
        }
        
        
        data = data.substring(data.indexOf("lng")+7 );
        String lon = "";
        for(int i = 0; i < data.length(); i++){
            if(Character.isDigit(data.charAt(i) ) ){
                lon += data.charAt(i);
            }
            else if (data.charAt(i) == '.'){
                lon += data.charAt(i);
            }
            else if (data.charAt(i) == '-'){
                lon += data.charAt(i);
            }
            else{
                break;
            }
        }
        
        
        longitude = Double.parseDouble(lon);        //parse numbers
        latitude = Double.parseDouble(lat);
    }
    
    /**
     * Method checks if api returns OK
     */
    public boolean isValidRequest(String string){
        String status = string.substring(string.indexOf("status") + 11, string.indexOf("status") + 13);
        
        
        if(status.equals("OK") ){
            return true;
        }
        return false;
    }
    
    /**
     * Method converts coordinates to grids used by api and return URL
     */
    private String coorToGrid()throws IOException, MalformedURLException{
        String gridURL = "https://api.weather.gov/points/";
        
        URL url = new URL(gridURL + latitude + "," + longitude);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();  //call api to get url with correct grid     
        con.setRequestMethod("GET");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        
        
        int counter = 60;
        
        while (counter != 0 && (inputLine = in.readLine()) != null) {   //throw away lines
            counter--;
        }
        counter = 1;
        while (counter !=0 && (inputLine = in.readLine()) != null) {
            counter --;
            inputLine = in.readLine();
            content.append(inputLine);
        }

        
        in.close();
        String temp = "";
        String URL = content.toString();
        URL = URL.substring(URL.indexOf("forecast") +12);
        int index = 0;
        while(URL.charAt(index) != '"'){            
                temp += URL.charAt(index);
                index++;
        }
        return temp;
    }
}
