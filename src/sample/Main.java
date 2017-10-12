package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;
import java.util.prefs.Preferences;

public class Main extends Application {
    private Stage primaryStage;
    private NetworkSetup networkSetup = new NetworkSetup();
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("main_scr.fxml"));
        AnchorPane rootLayout = loader.load();

        Scene scene = new Scene(rootLayout);

        primaryStage.setTitle("");
        primaryStage.setScene(scene);
        primaryStage.setMaxHeight(650);
        primaryStage.setMaxWidth(670);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/sample/image/pumpLeftOn.png")));
        primaryStage.setResizable(false);


        this.primaryStage = primaryStage;


        controller = loader.getController();
        controller.setMain(this);
        controller.setStage(primaryStage);

        getPreferences();

        primaryStage.show();

    }

    public void startSetupStage() throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("setup.fxml"));
        AnchorPane setupLayout = loader.load();

        Stage dialogStage = new Stage();

        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(setupLayout);

        dialogStage.setScene(scene);
        dialogStage.setTitle("Настройка соединения.");
        dialogStage.setResizable(false);

        Setup controller = loader.getController();
        controller.setMain(this);
        controller.setStage(dialogStage);
        controller.setNetSetup(networkSetup);

        dialogStage.show();
    }

    public void startAlarmStage() throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("alarm_scr.fxml"));
        AnchorPane alarmLayout = loader.load();

        Stage dialogStage = new Stage();

        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(alarmLayout);

        dialogStage.setScene(scene);
        dialogStage.setTitle("Аварийные ситуации.");

        AlarmScrController controller = loader.getController();
        controller.setMain(this);
        controller.setStage(dialogStage);
        controller.setMainController(this.controller);

        dialogStage.show();
    }

    public NetworkSetup getNetworkSetup() {
        return networkSetup;
    }

    public void setNetworkSetup(NetworkSetup networkSetup) {
        this.networkSetup = networkSetup;
    }

    private void getPreferences () {
        System.out.println("Получене сохраненных настроек");
        NetworkSetup networkSetup = new NetworkSetup();

        Preferences pref = Preferences.userNodeForPackage(Main.class);

        String ip1 = pref.get("ip1", "192");
        String ip2 = pref.get("ip2", "168");
        String ip3 = pref.get("ip3", "0");
        String ip4 = pref.get("ip4", "113");
        String port = pref.get("port", "502");
        String unitID = pref.get("unitID","1");
        String timeout = pref.get("timeout","3000");
        String timeCycle = pref.get ("timeCycle","1000");
        try {

            networkSetup.setIpAddress(networkSetup.ipToByteArry(ip1,ip2,ip3,ip4));
            networkSetup.setPort(Integer.parseInt(port));
            networkSetup.setUnitId(Integer.parseInt(unitID));
            networkSetup.setTimeout(Integer.parseInt(timeout));
            networkSetup.setTimeCycle(Integer.parseInt(timeCycle));

        } catch (NumberFormatException e){
            e.printStackTrace();
        }

        this.networkSetup = networkSetup;

    }

    /**
     * Сохранение последних введеных параметров
     */
    private void setPreferences() {
        Preferences pref = Preferences.userNodeForPackage(Main.class);

        String ip1 = networkSetup.getIp1();
        String ip2 = networkSetup.getIp2();
        String ip3 = networkSetup.getIp3();
        String ip4 = networkSetup.getIp4();
        String port = Integer.toString(networkSetup.getPort());
        String unitID = Integer.toString(networkSetup.getUnitId());
        String timeout = Integer.toString(networkSetup.getTimeout());
        String timeCycle = Integer.toString(networkSetup.getTimeCycle());

        pref.put("ip1",ip1);
        pref.put("ip2",ip2);
        pref.put("ip3",ip3);
        pref.put("ip4",ip4);
        pref.put("port",port);
        pref.put("unitID",unitID);
        pref.put("timeout",timeout);
        pref.put("timeCycle",timeCycle);

    }

    @Override
    public void stop() {
        System.out.println("Приложение закрыто");
        setPreferences();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
