package sample;

public class NetworkSetup {

    private String ip1 = "192";
    private String ip2 = "168";
    private String ip3 = "0";
    private String ip4 = "100";
    private byte [] ipAddress = {(byte)192,(byte)168,(byte)0,(byte)100};
    private int port = 502;
    private int unitId = 1;
    private int timeout = 3000;
    private int timeCycle = 1000;  // Время цикла опроса

    public NetworkSetup(){
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public byte[] ipToByteArry (String ip1, String ip2, String ip3, String ip4){
        this.ip1 = (ip1.equals(""))?"0":ip1;
        this.ip2 = (ip2.equals(""))?"0":ip2;
        this.ip3 = (ip3.equals(""))?"0":ip3;
        this.ip4 = (ip4.equals(""))?"0":ip4;

       return new  byte[]{(byte)Integer.parseInt(this.ip1),
                            (byte)Integer.parseInt(this.ip2),
                            (byte)Integer.parseInt(this.ip3),
                            (byte)Integer.parseInt(this.ip4)};
    }


    public void setIpAddress(byte[] ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeCycle() {
        return timeCycle;
    }

    public void setTimeCycle(int timeCycle) {
        this.timeCycle = timeCycle;
    }

    public String getIp1() {
        return ip1;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    public String getIp2() {
        return ip2;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public String getIp3() {
        return ip3;
    }

    public void setIp3(String ip3) {
        this.ip3 = ip3;
    }

    public String getIp4() {
        return ip4;
    }

    public void setIp4(String ip4) {
        this.ip4 = ip4;
    }
}
