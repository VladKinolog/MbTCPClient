package sample;

import javafx.beans.property.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmType implements Serializable{

    public transient static final String DRY_MO_ALARM = "Сухой ход";   // 0
    public transient static final String SS1_ALARM = "Авария силового агрегата 1";  //1
    public transient static final String SS2_ALARM = "Авария силового агрегата 2";  //2
    public transient static final String SS3_ALARM = "Авария силового агрегата 3";  //3
    public transient static final String SS4_ALARM = "Авария силового агрегата 4";  //4
    public transient static final String PUMP1_ALARM = "Авария насоса 1";  //5
    public transient static final String PUMP2_ALARM = "Авария насоса 2";  //6
    public transient static final String PUMP3_ALARM = "Авария насоса 3";  //7
    public transient static final String PUMP4_ALARM = "Авария насоса 4";  //8
    public transient static final String OVERFLOW_ALARM = "Перелив";  //9
    public transient static final String BREAK_LVL_SENS = "Обрыв датчика уровня";  //10
    public transient static final String CLOSURE_LVL_SENS = "КЗ датчика уровня";  //11
    public transient static final String BREAK_PRESS_SENS = "Обрыв датчика давления";  //12
    public transient static final String CLOSURE_PRESS_SENS = "КЗ датчика давления";  //13
    public transient static final String BREAK_CONSUM_SENS = "Обрыв датчика расхода";  //14
    public transient static final String CLOSURE_CONSUM_SENS = "КЗ датчика расхода";  //15
    public transient static final String LOW_PRESS = "Низкое давление";  //16
    public transient static final String HIGH_PRESS = "Превышение давления";  //17
    public transient static final String LOW_CONSUM = "Низкий расход";  //18
    public transient static final String HIGH_CONSUM = "Превышение расхода";  //19
    public transient static final String ACCESS_ALARM = "Несанкционированный доступ";  //20
    public transient static final String MAN_AUTO_ALARM = "Ошибка включения ручной/автомат";  //21
    public transient static final String OVF_DRY_SENS_ALARM = "Неверная логика срабатывания датчиков \"Сух. ход - Перелив\"";  //22
    public transient static final String OVF_SENS_ALARM = "Неверная логика срабатывания датчиков \"Перелив\"";  //23
    public transient static final String RKF1 = "Авария ввод 1";  //24
    public transient static final String RKF2 = "Авария ввод 2";  //25

    private transient String [] alarmTypeArry= {AlarmType.DRY_MO_ALARM,
            AlarmType.SS1_ALARM,
            AlarmType.SS2_ALARM,
            AlarmType.SS3_ALARM,
            AlarmType.SS4_ALARM,
            AlarmType.PUMP1_ALARM,
            AlarmType.PUMP2_ALARM,
            AlarmType.PUMP3_ALARM,
            AlarmType.PUMP4_ALARM,
            AlarmType.OVERFLOW_ALARM,
            AlarmType.BREAK_LVL_SENS,
            AlarmType.CLOSURE_LVL_SENS,
            AlarmType.BREAK_PRESS_SENS,
            AlarmType.CLOSURE_PRESS_SENS,
            AlarmType.BREAK_CONSUM_SENS,
            AlarmType.CLOSURE_CONSUM_SENS,
            AlarmType.LOW_PRESS,
            AlarmType.HIGH_PRESS,
            AlarmType.LOW_CONSUM,
            AlarmType.HIGH_CONSUM,
            AlarmType.ACCESS_ALARM,
            AlarmType.MAN_AUTO_ALARM,
            AlarmType.OVF_DRY_SENS_ALARM,
            AlarmType.OVF_SENS_ALARM,
            AlarmType.RKF1,
            AlarmType.RKF2
    };

    private transient IntegerProperty typeAlarm;
    private transient StringProperty alarmMassage;
    private transient StringProperty dateAlarm;
    private transient StringProperty dayAlarm;
    private transient StringProperty timeAlarm;
    private transient StringProperty alarmStatus;
    private transient BooleanProperty isAlarm;

    public AlarmType(){
    }

    public AlarmType(int typeAlarm, String patternFormatDate, boolean isAlarm){
        this.typeAlarm = new SimpleIntegerProperty(typeAlarm);
        this.alarmMassage = new SimpleStringProperty(alarmTypeArry[typeAlarm]);
        this.isAlarm = new SimpleBooleanProperty (isAlarm);

        alarmStatus = isAlarm ? new SimpleStringProperty("+") : new SimpleStringProperty("-");

        setCurrentDate(patternFormatDate);
    }

    public AlarmType(int typeAlarm,boolean isAlarm){
        this(typeAlarm,"HH:mm:ss dd/MM/yyyy ",isAlarm);
    }

    /**
     * Сохранение даты возникновения аварии
     * @param patternFormatDate формат даты задается как:"yyyy/MM/dd HH:mm:ss"
     */
    public void setCurrentDate(String patternFormatDate){
        DateFormat dateFormat = new SimpleDateFormat(patternFormatDate);
        Date date = new Date();

        this.dateAlarm = new SimpleStringProperty(dateFormat.format(date));

        this.timeAlarm = new SimpleStringProperty(new SimpleDateFormat("HH:mm:ss").format(date));
        this.dayAlarm = new SimpleStringProperty(new SimpleDateFormat("dd/MM/yyyy").format(date));

    }

    public int getTypeAlarm() {
        return typeAlarm.get();
    }

    public IntegerProperty typeAlarmProperty() {
        return typeAlarm;
    }

    public void setTypeAlarm(int typeAlarm) {
        this.typeAlarm.set(typeAlarm);
    }

    public String getAlarmMassage() {
        return alarmMassage.get();
    }

    public StringProperty alarmMassageProperty() {
        return alarmMassage;
    }

    public void setAlarmMassage(String alarmMassage) {
        this.alarmMassage.set(alarmMassage);
    }

    public String getDateAlarm() {
        return dateAlarm.get();
    }

    public StringProperty dateAlarmProperty() {
        return dateAlarm;
    }

    public void setDateAlarm(String dateAlarm) {
        this.dateAlarm.set(dateAlarm);
    }

    public String getDayAlarm() {
        return dayAlarm.get();
    }

    public StringProperty dayAlarmProperty() {
        return dayAlarm;
    }

    public void setDayAlarm(String dayAlarm) {
        this.dayAlarm.set(dayAlarm);
    }

    public String getTimeAlarm() {
        return timeAlarm.get();
    }

    public StringProperty timeAlarmProperty() {
        return timeAlarm;
    }

    public void setTimeAlarm(String timeAlarm) {
        this.timeAlarm.set(timeAlarm);
    }

    public boolean isIsAlarm() {
        return isAlarm.get();
    }

    public BooleanProperty isAlarmProperty() {
        return isAlarm;
    }

    public void setIsAlarm(boolean isAlarm) {
        this.isAlarm.set(isAlarm);
    }


    public String getAlarmStatus() {
        return alarmStatus.get();
    }

    public StringProperty alarmStatusProperty() {
        return alarmStatus;
    }

    public void setAlarmStatus(String alarmStatus) {
        this.alarmStatus.set(alarmStatus);
    }


    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(getTypeAlarm());
        s.writeUTF(getAlarmMassage());
        s.writeUTF(getDateAlarm());
        s.writeUTF(getDayAlarm());
        s.writeUTF(getTimeAlarm());
        s.writeBoolean(isIsAlarm());
        s.writeUTF(getAlarmStatus());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        typeAlarm = new SimpleIntegerProperty(s.readInt());
        alarmMassage = new SimpleStringProperty(s.readUTF());
        dateAlarm = new SimpleStringProperty(s.readUTF());
        dayAlarm = new SimpleStringProperty(s.readUTF());
        timeAlarm = new SimpleStringProperty(s.readUTF());
        isAlarm = new SimpleBooleanProperty(s.readBoolean());
        alarmStatus = new SimpleStringProperty(s.readUTF());
    }
}
