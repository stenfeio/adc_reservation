import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will be used to provide a template for the booking requests over the network
 * It will consist of values that will be parsed on the coordinator side and accepted on the client side.
 */
public class Request implements Serializable {

    enum RStatus{
        SUCCESS, FAILED, INVALID
    }

    RStatus status;     //Status of the request
    String id;          //ID of the request
    int numberOfDays;   //number of days required for the request
    List<Integer> dates;        //dates array to be checked if available

    public Request(){
        this.status = RStatus.INVALID;
        this.id = "missingno.";
        this.numberOfDays = 0;
        this.dates = new ArrayList<>();
    }

    public Request(String id, int numberOfDays, List<Integer> dates){
        this.status = RStatus.FAILED;
        this.id = id;
        this.numberOfDays = numberOfDays;
        this.dates = (ArrayList<Integer>) ((ArrayList<Integer>)dates).clone();
    }

    @Override
    public String toString(){
        return "Status:" + status + " ID: " + id + " Number of days: "+ numberOfDays + " Dates: " + dates;
    }
}
