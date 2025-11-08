package com.obsex;
import java.util.List;
import java.util.ArrayList;
import com.emanuelef.remote_capture.activities.LogUtil;

public class obseimp {
    Object obj;
    List<obseobj> obs=new ArrayList<>();
    public void addObs(obseobj o){
        boolean faund =false;
        for(obseobj ob:obs){
            if(o.getClass().equals(ob.getClass())){
                remObs(ob);
                faund=true;
                //LogUtil.logToFile("rem obseobj"+o.getClass()+ob.getClass());
            }
        }
        obs.add(o);
        /*
        if(obs.contains(o)){
            remObs(o);
            LogUtil.logToFile("rem obseobj");
        } else
            obs.add(o);*/
    }
    public void remObs(obseobj o){
        obs.remove(o);
    }
    public void remall(){
        obs.clear();
    }
    public void upall(Object arg){
        obj=arg;
        for(obseobj o:obs){
            o.update(arg);
        }
    }
    public Object getValue(){
        return obj;
    }
    public void postValue(Object arg){
        upall(arg);
    }
    public obseimp(){
        
    }
    public obseimp(Object arg){
        obj=arg;
    }
}
