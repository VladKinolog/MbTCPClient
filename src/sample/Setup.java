package sample;

import com.sun.org.apache.bcel.internal.generic.NEW;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class Setup {

    @FXML
    private TextField ip1;
    @FXML
    private TextField ip2;
    @FXML
    private TextField ip3;
    @FXML
    private TextField ip4;
    @FXML
    private TextField portTF;
    @FXML
    private TextField unitIdTF;
    @FXML
    private TextField timeoutTF;
    @FXML
    private TextField timeCycleTF;

    private Main main;
    private Stage stage;
    private NetworkSetup netSetup;

    @FXML
    private void initialize(){

    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public NetworkSetup getNetSetup() {
        return netSetup;
    }

    public void setNetSetup(NetworkSetup netSetup) {
        this.netSetup = netSetup;
        fillTextField();
    }
    private void fillTextField(){
        ip1.setText(netSetup.getIp1());
        ip2.setText(netSetup.getIp2());
        ip3.setText(netSetup.getIp3());
        ip4.setText(netSetup.getIp4());
        portTF.setText(Integer.toString(netSetup.getPort()));
        unitIdTF.setText(Integer.toString(netSetup.getUnitId()));
        timeoutTF.setText(Integer.toString(netSetup.getTimeout()));
        timeCycleTF.setText(Integer.toString(netSetup.getTimeCycle()));
    }

    @FXML
    private void okButtonClick(){

        String ip01 = ip1.getText().trim();
        String ip02 = ip2.getText().trim();
        String ip03 = ip3.getText().trim();
        String ip04 = ip4.getText().trim();

        try {

            int port = Integer.parseInt(portTF.getText().trim());
            int unitId = Integer.parseInt(unitIdTF.getText().trim());
            int timeout = Integer.parseInt(timeoutTF.getText().trim());
            int timeCycle = Integer.parseInt(timeCycleTF.getText().trim());

            netSetup = new NetworkSetup();
            netSetup.setIpAddress(netSetup.ipToByteArry(ip01, ip02, ip03, ip04));
            netSetup.setPort(port);
            netSetup.setUnitId(unitId);
            netSetup.setTimeout(timeout);
            netSetup.setTimeCycle(timeCycle);

            main.setNetworkSetup(netSetup);

            stage.close();

        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }
    @FXML
    private void cancelButtonClick(){
        stage.close();
    }



}
