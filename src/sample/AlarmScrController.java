package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class AlarmScrController {

    private static final String IMAGE_ADDR = "/sample/image/";

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
        //отображение в ячейке ноявления/пропадания аварии картинки.
        offOnAlarm.setCellFactory(cellData -> new TableCell<AlarmType, String>() {

            ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                imageView.setFitHeight(15);
                imageView.setFitWidth(15);

                setStyle("-fx-alignment: CENTER");

                if (item == null || empty){
                    setText(null);
                } else if (item.equals("+")) {

                    imageView.setImage(new Image(IMAGE_ADDR + "alarm_on.png"));

                    setGraphic(imageView);

                } else if (item.equals("-")) {
                    imageView.setImage(new Image(IMAGE_ADDR + "alarm_off.png"));
                    setGraphic(imageView);

                }
            }
        });

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
