package com.mike012610.guidebook;

/**
 * Created by mike012610 on 2015/6/16.
 */
public class NodeInfo {
    private int id;
    public String name;
    public String lat;
    public String lng;
    public String info;
    public int order;
    public NodeInfo(int id,String name,String lat,String lng,String info,int order) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.info = info;
        this.order = order;
    }
}
