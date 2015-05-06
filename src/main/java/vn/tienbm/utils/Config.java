package vn.tienbm.utils;

/**
 * Created by tienbm on 25/04/2015.
 */
public class Config {
    public String getProfilePath(){
        String path = getClass().getResource("/").getPath();
        return path + "profiles";
    }
    public String getRegexPath(String filename){

        String path = getClass().getResource("/regex"+"/"+filename).getPath();
        return path;
    }
}
