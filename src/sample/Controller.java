package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.*;

import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.Register;

import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Controller {

    private static final String AUTO = "АВТОМАТ";
    private static final String OFF = "ВЫКЛЮЧЕН";
    private static final String MANUAL = "РУЧНОЙ";
    private static final String IMAGE_ADDR = "/sample/image/";

    private static final int LVL_OVF_REG = 4513;
    private static final int LVL_ON_PUMP_REG = 4540;
    private static final int LVL_OFF_PUMP_REG = 4539;
    private static  final int LVL_DRY_MO_REG = 4514;

    private final int DU_COIL_MONITOR = 2048;
    private final int DU_COIL = 2587;

    private final int OF_PUMP_ON = 1054;


    private final int MAX_Y_COORD_BAR = 565;
    private final int MIN_Y_COORD_BAR = 350;

    @FXML
    private AnchorPane backgroundPane;

    @FXML
    private Label statusPump1Label;

    @FXML
    private Label statusPump2Label;

    @FXML
    private Label levelLabel;

    @FXML
    private Label currentAlarmLabel;

    @FXML
    private TextField overflwLvlTF;

    @FXML
    private Button connectOkButton;

    @FXML
    private ToggleButton duButton;

    @FXML
    private Button overflwLvlButton;

    @FXML
    private Label overflwLvlLable;

    @FXML
    private TextField onPumpLvlTF;

    @FXML
    private Button onPumpLvlTFButton;

    @FXML
    private Label onPumpLvlTFLable;

    @FXML
    private TextField offPumpLvlTF;

    @FXML
    private Button offPumpLvlTFButton;

    @FXML
    private Label offPumpLvlTFLable;

    @FXML
    private TextField dryMoLvlTF;

    @FXML
    private Button dryMoLvlTFButton;

    @FXML
    private Label dryMoLvlTFLable;

    @FXML
    private ImageView pump1OnShape;

    @FXML
    private ImageView pump2OnShape;

    @FXML
    private ImageView pumpOFOnShape;

    @FXML
    private ProgressBar levelProgressBar;

    @FXML
    private MenuItem networkSetupMenu;

    @FXML
    private SplitPane splitPane;

    @FXML
    private AnchorPane leftAnchorPane;

    @FXML
    private AnchorPane rightAnchorPane;

    @FXML
    private VBox buttonBox;

    @FXML
    private ImageView leftTube;

    @FXML
    private ImageView rightTube;

    @FXML
    private Polygon maxLvlMarkPolyg;

    @FXML
    private Polygon minLvlMarkPolyg;

    @FXML
    private Polygon onPumpLvlMarkPolyg;

    @FXML
    private Polygon offPumpLvlMarkPolyg;


    private InetAddress ipAddress;
    private int port = 502;
    private int unitID = 1;

    private Image pumpLeftOffImage;
    private Image pumpRightOffImage;
    private Image pumpLeftOnImage;
    private Image pumpRightOnImage;
    private Image pumpLeftAlarmImage;
    private Image pumpRightAlarmImage;

    private volatile float floatPoint = (float) 0.1;
    private volatile int lvlSensorMaxLimit = 25;

    private volatile int setNumRetryConnect = 4;
    private volatile int numRetryConnect = 0;
    private volatile boolean connectOkClickedFirst = false;

    private ModbusTCPTransaction mbTransaction;
    volatile private TCPMasterConnection connection;

    private Main main;
    private Stage stage;
    private NetworkSetup netSetup = new NetworkSetup();

    private ObservableList<AlarmType> alarmList = FXCollections.observableArrayList();
    private ObservableList<AlarmType> alarmListCurrent = FXCollections.observableArrayList();
    private int alarmWordPrevious = 0;
    private ArrayList<Integer> typeAlarmListPrevious = new ArrayList<Integer>();

    private LvlBarUtils maxLvlMark;
    private LvlBarUtils minLvlMark;
    private LvlBarUtils onPbmpLvlMark;
    private LvlBarUtils offPumpLvlMark;

    private String alarmCSVFile = "files\\csv\\";
    private String alarmListFile = "files\\worck\\";
    private Path alarmListPath = Paths.get(alarmListFile,"observalarm.ser");


    //private String alarmCSVFile = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();


    private boolean isSplitPaneChange = false;

    private TimerTask taskTimer;
    private Timer requestTimer;

    public Controller() throws URISyntaxException {
    }


    @FXML
    private void initialize()  {


        maxLvlMark = new LvlBarUtils(MIN_Y_COORD_BAR,MAX_Y_COORD_BAR);
        minLvlMark = new LvlBarUtils(MIN_Y_COORD_BAR,MAX_Y_COORD_BAR);
        onPbmpLvlMark = new LvlBarUtils(MIN_Y_COORD_BAR,MAX_Y_COORD_BAR);
        offPumpLvlMark = new LvlBarUtils(MIN_Y_COORD_BAR,MAX_Y_COORD_BAR);

        pumpLeftOffImage = new Image(String.valueOf(getClass().getResource(IMAGE_ADDR + "pumpLeftOff.png")));
        pumpRightOffImage = new Image(String.valueOf(getClass().getResource(IMAGE_ADDR + "pumpRightOff.png")));
        pumpLeftOnImage = new Image(String.valueOf(getClass().getResource(IMAGE_ADDR + "pumpLeftOn.png")));
        pumpRightOnImage = new Image(String.valueOf(getClass().getResource(IMAGE_ADDR + "pumpRightOn.png")));
        pumpLeftAlarmImage = new Image(String.valueOf(getClass().getResource(IMAGE_ADDR + "pumpLeftAlarm.png")));
        pumpRightAlarmImage = new Image(String.valueOf(getClass().getResource(IMAGE_ADDR + "pumpRightAlarm.png")));

        focusList(overflwLvlTF,overflwLvlLable,overflwLvlButton);
        focusList(onPumpLvlTF,onPumpLvlTFLable,onPumpLvlTFButton);
        focusList(offPumpLvlTF,offPumpLvlTFLable,offPumpLvlTFButton);
        focusList(dryMoLvlTF,dryMoLvlTFLable,dryMoLvlTFButton);


        // проверка наличия связи
        Timer timer = new Timer(true);
        TimerTask checkConnectTimer = new CheckConnectTimer();
        timer.schedule(checkConnectTimer,3000,3000);

        //Проверка наличия файла журнала авариии Создание каталога если его нет.
        checkCatalogExist(new File(alarmCSVFile));
        checkCatalogExist(new File(alarmListFile));

        alarmList = read(alarmListPath);

        alarmList.addListener((ListChangeListener<AlarmType>)(c -> {
            print("Событие по изменению списка, рамер составляет " + alarmList.size());
            write(alarmList, alarmListPath);
        } ));

        duButton.setTooltip(new Tooltip("Дистанционная блокировка станции"));

        Platform.runLater(() -> leftAnchorPane.requestFocus()); //Сброс фокуса //TODO Попробовать реализовать при загрузке экрана

    }

    private void checkCatalogExist(File file){
        if (!file.exists()){
            file.mkdirs();
        }
    }

    public void setMain (Main main){
        this.main = main;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ObservableList<AlarmType> getAlarmList(){
        return alarmList;
    }

    public ObservableList<AlarmType> getAlarmListCurrent() {
        return alarmListCurrent;
    }

    /**
     * Слушатель фокус текст филд
     * @param field TextField Который прослушивается на наличие фокуса
     * @param label Привязана к полю ввода
     * @param button кнопка ок
     */
    private void focusList(TextField field, Label label, Button button){

        field.focusedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) {
                button.setVisible(true);
                label.setLayoutX(90);
            }
        });
    }



    @FXML
    private void leftAnchorPaneClick(){

        //splitPane.setDividerPosition(0,0.9);

        startMoutionPanel(splitPane, splitPane.getDividerPositions()[0], 0.9, 1);

        Platform.runLater(()-> leftAnchorPane.requestFocus());
    }

    @FXML
    private void rightAnchorPaneClick(){
        //splitPane.setDividerPosition(0,0.7);

        startMoutionPanel(splitPane,  splitPane.getDividerPositions()[0], 0.7, 1);

        Platform.runLater(()-> rightAnchorPane.requestFocus());

    }

    @FXML
    private void connectOkClicked() {
        netSetup = main.getNetworkSetup();
            if (connection != null && connection.isConnected()) {
                return;
            }
            try {
                ipAddress = InetAddress.getByAddress(netSetup.getIpAddress());
                connection = new TCPMasterConnection(ipAddress);
                connection.setTimeout(netSetup.getTimeout());
                connection.setPort(netSetup.getPort());
                println("Попытка соединения к адресу "+ipAddress.getCanonicalHostName()+" порт "+port);
                connection.connect();

                mbTransaction = new ModbusTCPTransaction(connection);

            } catch (Exception e) {
                System.out.println("Ошибка соединения");
                e.printStackTrace();

            }
        // запуск таймера с переодичностью опросов
        requestTimer = new Timer(true);
        taskTimer = new RequestTimerTask();

        requestTimer.schedule(taskTimer, netSetup.getTimeCycle(), netSetup.getTimeCycle());

        Platform.runLater(() -> leftAnchorPane.requestFocus()); //Сброс фокуса

        connectOkClickedFirst = true; // Флаг по нажатию кнопки "Соединения" первый раз
    }



    @FXML
    private void networkSetupClick (){
        try {
            main.startSetupStage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int getTextFieldValue(TextField textField, float floatPoint){
            String tfValue = textField.getText().trim().replace(",",".");
            float fl = Float.parseFloat(tfValue)/floatPoint;
            return (int) fl;
        }

    private int getTextFieldValue(String value, float  floatPoint){
            value = value.replace(",",".").trim();
            float fl = Float.parseFloat(value)/floatPoint;
            return Math.round(fl);

        }


    /**
     * Отображение состояния переключателя ручной-автомат-выключен в Lable
     * @param label целевой Label.
     * @param status статус считаный с контроллера
     *
     */
    private void printStatusSwitchPump (Label label, int status){
        String statusPump;
        switch (status){
            case 0: statusPump = OFF;
                    break;
            case 1: statusPump = MANUAL;
                    break;
            case 2: statusPump = AUTO;
                    break;
            default: statusPump = "ERROR";
                    break;
        }
        Platform.runLater(() -> label.setText(statusPump));
    }

    /**
     * Смена цвета иконки насоса в зависимости от состояния (включен-выключен-авария)
     * @param pumpShape целевая фигура
     * @param statusP состояние насоса считаное с контроллера
     */
    private void changeColorPump (ImageView pumpShape, int statusP, int numberPump){
        Image pumpColor;
        switch (numberPump) {
            case 1:
                switch (statusP) {
                    case 0:
                        pumpColor = pumpLeftOffImage;
                        break;
                    case 1:
                        pumpColor = pumpLeftOnImage;
                        break;
                    case 2:
                        pumpColor = pumpLeftAlarmImage;
                        break;
                    default:
                        pumpColor = pumpLeftOffImage;
                        break;
                }
                break;
            case 2:
                switch (statusP) {
                    case 0:
                        pumpColor = pumpRightOffImage;
                        break;
                    case 1:
                        pumpColor = pumpRightOnImage;
                        break;
                    case 2:
                        pumpColor = pumpRightAlarmImage;
                        break;
                    default:
                        pumpColor = pumpRightOffImage;
                        break;
                }
                break;
            default:
                 pumpColor = pumpRightOffImage;
                 break;
        }
        Platform.runLater(() -> pumpShape.setImage(pumpColor));
    }

    private void changeColorPump(ImageView pumpShape, boolean pumpStatus){
        Image pumpColor;
        if(pumpStatus){
            pumpColor = pumpRightOnImage;
        }else {
            pumpColor = pumpRightOffImage;
        }
        Platform.runLater(() -> pumpShape.setImage(pumpColor));
    }

    private void visibleTube(ImageView tube, int statusP){

         if (statusP == 1){
             Platform.runLater(() -> tube.setVisible(true));
         } else {
             Platform.runLater(() ->tube.setVisible(false));
         }
    }

    private void printValueInTextField(TextField targetField, Button button, int value, float floatPoint){
        float resultValue = value * floatPoint;
        // Форматирование вывода
        String decimFormat =  "#" + (new StringBuilder (Float.toString(floatPoint))).toString().replace("1","0");


        if (!button.isVisible()) {
            Platform.runLater(() -> targetField.setText(new DecimalFormat(decimFormat).format(resultValue)));
        }
    }

    private void printLevel(int level,float floatPoint){
        float resultLevel = level * floatPoint;
        String decimFormat = "#" + (new StringBuilder (Float.toString(floatPoint))).toString().replace("1","0");

        Platform.runLater(() -> {
            levelLabel.setText(new DecimalFormat(decimFormat).format(resultLevel));
            levelProgressBar.setProgress((double) level/lvlSensorMaxLimit);
        });
    }

    /**
     * Задание предела для датчика уровня
     * @param typeSensor выбор типа датчика
     * @param customtypeSensor Пользовательское значение предела датчика уровня
     * @return предел датчика уровня
     */
    private int setLvlSensorLimit (int typeSensor, int customtypeSensor){
        int maxLvl = 25;
        if (typeSensor == 3){
            maxLvl = customtypeSensor;
        } else {
            switch (typeSensor) {
                case 0: maxLvl = 100;break;
                case 1: maxLvl = 25; break;
                case 2: maxLvl = 60; break;
            }
        }
        floatPoint = setFloatPoint(typeSensor);

        return maxLvl;
    }

    private float setFloatPoint (int typeSensor){
        return (typeSensor == 0)? (float) 0.01: (float) 0.1;
    }


    /**
     * Нажатие на кнопку и сброс фокуса. Возврат меток в исходное положение
     * @param actionEvent
     */
    @FXML
    private void tfButtonOnAction(ActionEvent actionEvent) {
        String value = "";
        int numRegistr = 0;

        Register r = ModbusCoupler.getReference().getProcessImageFactory().createRegister();
        ModbusRequest req;

        Button but = ((Button)actionEvent.getSource());
        but.setVisible(false);

        switch (but.getId()){
            case "overflwLvlButton":
                overflwLvlLable.setLayoutX(65);
                value = overflwLvlTF.getText().trim();
                numRegistr = LVL_OVF_REG;
                break;
            case "onPumpLvlTFButton":
                onPumpLvlTFLable.setLayoutX(65);
                value = onPumpLvlTF.getText().trim();
                numRegistr = LVL_ON_PUMP_REG;
                break;
            case "offPumpLvlTFButton":
                offPumpLvlTFLable.setLayoutX(65);
                value = offPumpLvlTF.getText().trim();
                numRegistr = LVL_OFF_PUMP_REG;
                break;
            case "dryMoLvlTFButton":
                dryMoLvlTFLable.setLayoutX(65);
                value = dryMoLvlTF.getText().trim();
                numRegistr = LVL_DRY_MO_REG;
                break;
            default: break;

        }
        if (!value.equals("")&& numRegistr != 0){

            try {
                r.setValue(getTextFieldValue(value,floatPoint));

                println(Integer.toString(getTextFieldValue(value,floatPoint)));

                req = new WriteSingleRegisterRequest(numRegistr, r);
                req.setUnitID(netSetup.getUnitId());
                mbTransaction.setRequest(req);
                mbTransaction.execute();
                println("Отправка сообщения в Контроллер - REG = "+numRegistr+" || Значение = "+ r.getValue() + " || Значение UnitId = "+ req.getUnitID());

            } catch (NumberFormatException e){
                e.printStackTrace();
            } catch (ModbusIOException e) {
                e.printStackTrace();
            } catch (ModbusSlaveException e) {
                e.printStackTrace();
            } catch (ModbusException e) {
                e.printStackTrace();
            }
        }

        leftAnchorPane.requestFocus(); //Сброс фокуса
    }

    /**
     * Обработчик нажатия кнопки дистанционной блокировки
     * @param actionEvent
     */

    public void duButtonOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаленная блокировка станции");
        alert.setHeaderText("Заблокировать работу насосов?");

        ButtonType blockButton = new ButtonType("Блок");
        ButtonType unBlockButton = new ButtonType("Работа");
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(blockButton,unBlockButton,cancelButton);

        alert.showAndWait().ifPresent(response -> {
            ModbusRequest req;
            try {
                if (response == blockButton) {
                    // println(Integer.toString(getTextFieldValue(value,floatPoint)));
                    req = new WriteCoilRequest(DU_COIL, true);
                    req.setUnitID(netSetup.getUnitId());
                    mbTransaction.setRequest(req);
                    mbTransaction.execute();
                    duButton.setSelected(true);

                } else if (response == unBlockButton) {
                    req = new WriteCoilRequest(DU_COIL, false);
                    req.setUnitID(netSetup.getUnitId());
                    mbTransaction.setRequest(req);
                    mbTransaction.execute();
                    duButton.setSelected(false);

                } else {
                    alert.close();

                }
            } catch (ModbusException e){
                e.printStackTrace();
            }
        });

    }

    /**
     * Обработчик клика мышки по SplitPane (сбрасывает видимость всех кнопок)
     * @param mouseEvent событие
     */
    @FXML
    private void splitPaneMouseClicked(MouseEvent mouseEvent) {
        println("Событие по клику на SplitPane");
        overflwLvlButton.setVisible(false);
        onPumpLvlTFButton.setVisible(false);
        offPumpLvlTFButton.setVisible(false);
        dryMoLvlTFButton.setVisible(false);
        overflwLvlLable.setLayoutX(65);
        onPumpLvlTFLable.setLayoutX(65);
        offPumpLvlTFLable.setLayoutX(65);
        dryMoLvlTFLable.setLayoutX(65);
    }



    private synchronized void alarmHending(int alarmW1,int alarmW2) {

        int alarmWordCurrent = AlarmParsing.getAlarmWord(alarmW1,alarmW2);
        ArrayList<Integer> typeAlarmList = AlarmParsing.getAlarmIndex(alarmW1, alarmW2);

        alarmListCurrent.clear();

        // текущая авария
        for (int i:typeAlarmList) {

            AlarmType alarmType = new AlarmType(i,true);
            alarmListCurrent.add(alarmType);
        }

        if (alarmListCurrent.size() != 0){
            // отображение последней аварии!!
         Platform.runLater(() -> currentAlarmLabel.setText(alarmListCurrent.get(alarmListCurrent.size()-1).getAlarmMassage() + "(*"+alarmListCurrent.size()+")"));
         Platform.runLater(() ->currentAlarmLabel.setVisible(true));
        } else {
         Platform.runLater(() -> currentAlarmLabel.setVisible(false));
        }

        //alarmListCurrent.forEach(type -> println("Текущая авария - "+ type.getDateAlarm() +"    "+type.getAlarmMassage() +"   "+  type.isIsAlarm() + "   "+" Размер   "+ alarmListCurrent.size()));

        //Журнал аварий
        for (int i:typeAlarmList){

            if (AlarmParsing.getChangeBitAlarm(alarmWordPrevious, alarmWordCurrent, i) > 0) {

                AlarmType alarmType = new AlarmType(i,true);

                writeCSV(alarmCSVFile,alarmType);

                if (alarmList.size() > 200){
                    alarmList.remove(0);
                    alarmList.add(alarmType);
                } else {
                    alarmList.add(alarmType);
                }

                //alarmList.forEach(type ->  println("Журнал аварий - "+ type.getDateAlarm() +"    "+type.getAlarmMassage() +"   "+  type.isIsAlarm()));

  //          } else if (AlarmParsing.getChangeBitAlarm(alarmWordPrevious, alarmWordCurrent, i) == 0) {
                //          alarmList.add(new AlarmType(i, false));
//
//                for (AlarmType type:alarmList) {
//                    println("Журнал аварий - "+ type.getDateAlarm() +"    "+type.getAlarmMassage() +"   " +  type.isIsAlarm());
//                }
            }
        }

        for (int  i: typeAlarmListPrevious){
            if (typeAlarmList.indexOf(i)<0){
                alarmList.add(new AlarmType(i, false));
            }
        }

        typeAlarmListPrevious = typeAlarmList;
        alarmWordPrevious = alarmWordCurrent;
    }

    private void writeCSV(String file,AlarmType alarmType) {
        String nameCSVFile;
        String separat  = ";";
        nameCSVFile = new SimpleDateFormat("MM_yyyy").format(new Date())+"_Alam.csv";

        File resultFile = new File(file + nameCSVFile);
        FileWriter fr = null;
        try {
            fr = new FileWriter(resultFile,true);
            fr.write(alarmType.getDateAlarm() + separat + alarmType.getAlarmMassage() + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                println("СSV файл закрыт");
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       // if (file.)

    }

    // серилизация журнала арарии в файл
    private void write(ObservableList<AlarmType> alarmTypes, Path file) {

        try {

            println(file.getFileName().toString());
            // write object to file
            OutputStream fos = Files.newOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(new ArrayList<AlarmType>(alarmTypes));
            oos.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ObservableList<AlarmType> read(Path file) {
        try {
            if (Files.isReadable(file)) {

                print("файл "+ file.getFileName() +" готов к чтению - " + Files.isReadable(file));

                InputStream in = Files.newInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(in);
                List<AlarmType> list = (List<AlarmType>) ois.readObject();
                return FXCollections.observableList(list);

            }

        } catch (ClassNotFoundException|IOException e) {
            e.printStackTrace();
        }

        return FXCollections.observableArrayList();
    }

    public void curentAlarmLabelMousClick(MouseEvent mouseEvent) {
        try {
            main.startAlarmStage();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void alarmJournalCall(ActionEvent actionEvent) {
        try {
            main.startAlarmStage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void duStatusHendler(Boolean statusDu){
        if (statusDu){
            Platform.runLater(()-> duButton.setSelected(true));
        } else {
            Platform.runLater(()-> duButton.setSelected(false));
        }
    }
    /**
     *получение координаты для метки уровня
    */
    public void gangeMarkYCoord (LvlBarUtils lvlMark,Polygon mark,int curentLvl, int typeSensor, int customLvlSensor){
       Platform.runLater(() -> mark.setLayoutY(lvlMark.getResultYCoord(curentLvl,typeSensor,customLvlSensor)));
    }



    public class RequestTimerTask extends TimerTask{

        ModbusRequest request;
        ReadMultipleRegistersResponse registersResponse;

        int level;
        int statusSwitchPump1;
        int statusSwitchPump2;
        int statusSwitchPump3;
        int statusSwitchPump4;
        int statusPump1;
        int statusPump2;
        int statusPump3;
        int statusPump4;
        int setOwerflLvl;
        int setOnPumpLvl;
        int setOffPumpLvl;
        int setDrymoLvl;
        int firstAlarmWord;
        int secondAlarmWord;
        int typeLvlSens;
        int customLvlSens;

        @Override
        public void run() {
            try {
                long time = System.currentTimeMillis();
                //чтение состояния Насосов.
                sendRequest(8096, 8);
                registersResponse = (ReadMultipleRegistersResponse) mbTransaction.getResponse();

                // Опрос сотояния переключателя ручной - выкл - автомат
                statusSwitchPump1 = registersResponse.getRegister(0).getValue();
                statusSwitchPump2 = registersResponse.getRegister(1).getValue();
                //statusSwitchPump3 = registersResponse.getRegister(2).getValue();
                //statusSwitchPump4 = registersResponse.getRegister(3).getValue();

                // Опрос состояния насосов выкл - включ - авария
                statusPump1 = registersResponse.getRegister(4).getValue();
                statusPump2 = registersResponse.getRegister(5).getValue();
                //statusPump3 = registersResponse.getRegister(6).getValue();
                //statusPump4 = registersResponse.getRegister(7).getValue();

                // Вывод на экрана сотояния переключателя ручн-выкл-автомат в завис от состояния
                printStatusSwitchPump(statusPump1Label, statusSwitchPump1);
                printStatusSwitchPump(statusPump2Label, statusSwitchPump2);
                //printStatusSwitchPump(statusPump3Label,statusSwitchPump1);
                //printStatusSwitchPump(statusPump4Label,statusSwitchPump2);

                //Вывод на экран изменения сотояния насоса
                changeColorPump(pump1OnShape, statusPump1, 1);
                changeColorPump(pump2OnShape, statusPump2, 2);
                visibleTube(leftTube, statusPump1);
                visibleTube(rightTube, statusPump2);
                //changeColorPump(pump3OnShape,statusPump3);
                //changeColorPump(pump4OnShape,statusPump4);

                //Опрос датчиков
                sendRequest(4098, 7);
                registersResponse = (ReadMultipleRegistersResponse) mbTransaction.getResponse();

                level = registersResponse.getRegister(3).getValue();
                printLevel(level, floatPoint);

                //Настройки
                sendRequest(4504, 52);
                registersResponse = (ReadMultipleRegistersResponse) mbTransaction.getResponse();

                //считывание значений уровня
                setOwerflLvl = registersResponse.getRegisterValue(9);
                setOnPumpLvl = registersResponse.getRegisterValue(36);
                setOffPumpLvl = registersResponse.getRegisterValue(35);
                setDrymoLvl = registersResponse.getRegisterValue(10);

                typeLvlSens = registersResponse.getRegisterValue(2);
                customLvlSens = registersResponse.getRegisterValue(3);

                //Тип датчика уровня
                lvlSensorMaxLimit = setLvlSensorLimit(typeLvlSens, customLvlSens);
                //Отрисовка меток заданых уровней
                gangeMarkYCoord(maxLvlMark, maxLvlMarkPolyg, setOwerflLvl, typeLvlSens, customLvlSens);
                gangeMarkYCoord(minLvlMark, minLvlMarkPolyg, setDrymoLvl, typeLvlSens, customLvlSens);
                gangeMarkYCoord(onPbmpLvlMark, onPumpLvlMarkPolyg, setOnPumpLvl, typeLvlSens, customLvlSens);
                gangeMarkYCoord(offPumpLvlMark, offPumpLvlMarkPolyg, setOffPumpLvl, typeLvlSens, customLvlSens);


                //Вывод в поля значений
                printValueInTextField(overflwLvlTF, overflwLvlButton, setOwerflLvl, floatPoint);
                printValueInTextField(onPumpLvlTF, onPumpLvlTFButton, setOnPumpLvl, floatPoint);
                printValueInTextField(offPumpLvlTF, offPumpLvlTFButton, setOffPumpLvl, floatPoint);
                printValueInTextField(dryMoLvlTF, dryMoLvlTFButton, setDrymoLvl, floatPoint);

                //Параметры частотника\плавного пуска
                sendRequest(4726, 17);
                registersResponse = (ReadMultipleRegistersResponse) mbTransaction.getResponse();

                //Аварийное слово
                sendRequest(36868, 2);
                registersResponse = (ReadMultipleRegistersResponse) mbTransaction.getResponse();

                firstAlarmWord = registersResponse.getRegisterValue(0);
                secondAlarmWord = registersResponse.getRegisterValue(1);

                alarmHending(firstAlarmWord, secondAlarmWord);

                //Обработчик ДУ
                duStatusHendler(readSingleCoilRequest(DU_COIL_MONITOR));

                // Насос откачки жидкости с приямка
                changeColorPump(pumpOFOnShape, readSingleInputRequest(OF_PUMP_ON));

                System.out.println("Время опроса = " + (System.currentTimeMillis() - time));
            }catch (Exception e){
                println("Ошибка считывания данныйх");
                requestTimer.cancel();

                println(Integer.toString(requestTimer.purge()));
                connection.close();
            }
        }


        private void sendRequest (int ref,int count){
            request = new ReadMultipleRegistersRequest(ref,count);
            request.setUnitID(unitID);
            mbTransaction.setRequest(request);
            try {
                mbTransaction.execute();
            } catch (ModbusException e) {
                e.printStackTrace();
                connection.close();
            }
        }

        /**
         *  Чтение одного битового Coil регистра
         * @param ref адресс регистра в 10-тичн системе с 0.
         * @return значение регисра (True or false)
         */
        private boolean readSingleCoilRequest (int ref){
            ReadCoilsResponse coilsResponse;
            ModbusRequest coilRequest = new ReadCoilsRequest(ref,1);
            coilRequest.setUnitID(unitID);
            mbTransaction.setRequest(coilRequest);
            try {
                mbTransaction.execute();
                //coilRequest = (ReadCoilsResponse) coilsResponse.getCoilStatus(1);
            } catch (ModbusException e) {
                e.printStackTrace();
                connection.close();
            }
            coilsResponse = (ReadCoilsResponse) mbTransaction.getResponse();
            //println("coilsResponse.getCoilStatus(0) = " + coilsResponse.getCoilStatus(0));
            return coilsResponse.getCoilStatus(0);
        }

        /**
         * Чтение одного битового Input регистра
         * @param ref адресс регистра в 10-тичн системе с 0.
         * @return значение регисра (True or false)
         */
        private boolean readSingleInputRequest(int ref){
            ReadInputDiscretesResponse inputResponse;
            ModbusRequest inputRequest = new ReadInputDiscretesRequest(ref,1);
            inputRequest.setUnitID(unitID);
            mbTransaction.setRequest(inputRequest);
            try {
                mbTransaction.execute();
            } catch (ModbusException e){
                e.printStackTrace();
                connection.close();
            }
            inputResponse = (ReadInputDiscretesResponse) mbTransaction.getResponse();
            return inputResponse.getDiscreteStatus(0);
        }

    }


    public class CheckConnectTimer extends TimerTask{
        @Override
        public void run() {
            if (connection != null && connection.isConnected()){
               Platform.runLater(() -> splitPane.setOpacity(1));
               Platform.runLater(() -> connectOkButton.setVisible(false));
            }else {
                Platform.runLater(() -> splitPane.setOpacity(0.2));
                Platform.runLater(() -> connectOkButton.setVisible(true));
                if (connectOkClickedFirst) {
                    connectOkClicked();
                }
            }
        }
    }

    private void startMoutionPanel (SplitPane pane,double starPos, double endPos, int sleep){
        final double[] currentPos = {starPos};
        final boolean[] result = {false};
        Thread thread = new Thread(()-> {

            if (starPos > endPos) {
                while (currentPos[0] > endPos) {
                    currentPos[0] = currentPos[0] - 0.001;
                    Platform.runLater(() -> pane.setDividerPosition(0, currentPos[0]));
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            } else if (starPos < endPos){
                while (currentPos[0] < endPos) {
                    currentPos[0] = currentPos[0] + 0.001;
                    Platform.runLater(() -> pane.setDividerPosition(0, currentPos[0]));
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
        thread.setDaemon(true);
        thread.start();
    }



    public void println (String print){
        System.out.println(print);
    }
    public void print (String print){
        System.out.print(print);
    }


}
