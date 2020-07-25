package flirt.and.date;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserInfo implements Serializable {

    private String sex;
    private String target;
    private String dateBirth;



    private int photosCount;
    private String description;
    private String distance;


    public UserInfo(){

    }


    public void setDistance(String distance) {
        this.distance = distance;
    }





    public void setDescription(String description) {
        this.description = description;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public void setPhotosCount(int photosCount) {
        this.photosCount = photosCount;
    }



    public int getPhotosCount() {
        return photosCount;
    }

    public String getSex() {
        return sex;
    }

    public String getTarget() {
        return target;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public String getDescription() {
        return description;
    }

    public String getDistance() {
        return distance;
    }

    public String getAge() {
        Date date;
        DateFormat dateFormat =  new SimpleDateFormat("dd/MM/yyyy");
        try {
           date =  dateFormat.parse(getDateBirth());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        Date now  = new Date();
        long timeBetween = now.getTime() - date.getTime();
        double yearsBetween = timeBetween / 3.15576e+10;
        int age = (int) Math.floor(yearsBetween);
        return Integer.toString(age);
    }



}
