package sample;

import java.util.ArrayList;
import java.util.List;

public class AlarmParsing {

    private int [] alarmIndexArry;
    private int firstWordAlarm;
    private int secondWordAlarm;


    public AlarmParsing(int firstWordAlarm, int secondWordAlarm){
    }

    public static ArrayList<Integer> getAlarmIndex(int firstWordAlarm, int secondWordAlarm){

        secondWordAlarm = secondWordAlarm << 16;
        int foolWord = firstWordAlarm | secondWordAlarm;
        ArrayList<Integer> result = new ArrayList<Integer>();

        for (int i = 0; i<32 ;i++){
            if ((foolWord & 1) == 1) {
                result.add(i);
            }
            foolWord = foolWord >> 1;
        }
        return result;
    }

    public static int getAlarmWord(int firstWordAlarm, int secondWordAlarm){
        secondWordAlarm = secondWordAlarm << 16;
        return firstWordAlarm | secondWordAlarm;

    }

    public static int getChangeBitAlarm (int oldAlarmWord, int currentAlarmWord, int index){
        oldAlarmWord = oldAlarmWord >> index;
        oldAlarmWord = oldAlarmWord & 1;
        currentAlarmWord = currentAlarmWord >> index;
        currentAlarmWord = currentAlarmWord & 1;

        if (oldAlarmWord == currentAlarmWord){
            return -1;
        } else if (currentAlarmWord  == 1){
            return 1;
        } else {
            return 0;
        }
    }
}
