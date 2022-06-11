import java.util.*;

public class BerkeleyMaster {
    static ArrayList<Long> slave_clockList = new ArrayList<>();


    /*# subroutine function used to fetch average clock difference
    def getAverageClockDiff():
 
    current_client_data = client_data.copy()
 
    time_difference_list = list(client['time_difference']
                                for client_addr, client
                                    in client_data.items())
                                    
 
    sum_of_clock_difference = sum(time_difference_list, \
                                   datetime.timedelta(0, 0))
 
    average_clock_difference = sum_of_clock_difference \
                                         / len(client_data)
 
    return  average_clock_difference*/
    public static long average_diff(long master_time) {
        long total_diff = 0;

        for(int i=0; i<slave_clockList.size(); i++) {
            total_diff += slave_clockList.get(i) - master_time;
        }

        return total_diff/slave_clockList.size();
    }



}