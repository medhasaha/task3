package com.example.sonali.task3;

/**
 * Created by Sonali on 08-09-2018.
 */

public class persons {
    private String _id;
    private String _name;
    public String _role;
    private String _loc;
    private byte[] _img;


    public persons(String name,String loc,String role, byte[] img){
        this._role=role;
        this._name=name;
        this._loc=loc;
        this._img=img;
    }

    public String get_name() {
        return _name;
    }

    public String get_role(){return _role;}

    public String get_loc(){return _loc;}

    public byte[] get_img(){return _img;}

    public void setName(String name) {
        this._name = name;
    }

    public void setRole(String role){this._role=role;}

    public void setId(String id) {
        this._id=id;
    }

    public void setLoc(String location){this._loc=location;}

}
