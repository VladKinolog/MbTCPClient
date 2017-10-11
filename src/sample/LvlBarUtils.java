package sample;

public class LvlBarUtils {

    //public static void
    private int basicMinYCoord;
    private int basicMaxYCoord;
    private int basicDeltaCoord;
    private int previousLvl = -1;
    private int previousTypeSensor = -1;
    private int previousCustLvlSens = -1;

    private int resultCoord;

    public LvlBarUtils(int basicMinYCoord,int basicMaxYCoord){
        this.basicMaxYCoord = basicMaxYCoord;
        this.basicMinYCoord = basicMinYCoord;
        this.basicDeltaCoord = Math.abs(basicMaxYCoord-basicMinYCoord);

    }

    public int getResultYCoord(int curentLvl, int typeSensor, int customLvlSensor){
        int maxLvl = 25;
        if(curentLvl == previousLvl && typeSensor == previousTypeSensor && customLvlSensor == previousCustLvlSens){
            return resultCoord;
        } else {
            if (typeSensor == 3) {
                maxLvl = customLvlSensor;
            } else {
                switch (typeSensor) {
                    case 0:
                        maxLvl = 100;
                        break;
                    case 1:
                        maxLvl = 25;
                        break;
                    case 2:
                        maxLvl = 60;
                        break;
                }
            }

            previousLvl = curentLvl;
            previousTypeSensor = typeSensor;
            previousCustLvlSens = customLvlSensor;

            double y = (double) basicMaxYCoord - (((double) basicDeltaCoord / (double) maxLvl) * (double) curentLvl);
            resultCoord = (int) y;
            System.out.println("resultCoord (int) y =" + resultCoord);
            return resultCoord;
        }
    }
}
