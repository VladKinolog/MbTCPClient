package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class AlarmScrController {

    @FXML
    private TableView<AlarmType> alarmTable;
    @FXML
    private TableColumn<AlarmType, String> dateAlarm;
    @FXML
    private TableColumn<AlarmType, String> offOnAlarm;
    @FXML
    private TableColumn<AlarmType, String> massageAlarm;
    @FXML
    private TableView<AlarmType> alarmCurrentTable;
    @FXML
    private TableColumn<AlarmType, String> dateCurrentAlarm;
    @FXML
    private TableColumn<AlarmType, String> massageCurrentAlarm;


    private Main main;
    private Stage stage;
    private Controller mainController;

    @FXML
    private void initialize() {
        // Инициализация таблицы адресатов с двумя столбцами.
        dateAlarm.setCellValueFactory(cellData -> cellData.getValue().dateAlarmProperty());
        massageAlarm.setCellValueFactory(cellData -> cellData.getValue().alarmMassageProperty());
        offOnAlarm.setCellValueFactory(cellData -> cellData.getValue().alarmStatusProperty());

        dateCurrentAlarm.setCellValueFactory(cellData -> cellData.getValue().dateAlarmProperty());
        massageCurrentAlarm.setCellValueFactory(cellData -> cellData.getValue().alarmMassageProperty());
    }



    public void setMain(Main main) {
        this.main = main;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setMainController(Controller mainController) {
        this.mainController = mainController;
        alarmTable.setItems(mainController.getAlarmList());
        alarmCurrentTable.setItems(mainController.getAlarmListCurrent());
    }
}
