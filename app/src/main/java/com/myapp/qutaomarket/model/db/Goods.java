package com.myapp.qutaomarket.model.db;

public class Goods {
    //商品id
    private int id;

    //商品名
    private String gname;

    //商品所属用户id
    private int uid;

    //商品发布时间
    private String gtime;

    //商品类型
    private String gtype;

    //商品细节
    private String gdetail;

    //商品价格
    private double gprice;

    //商品原价
    private double goprice;

    //商品图片
    private String gimage;

    //商品状态
    private int gstate;

    //是否是急售商品
    private int gemergent;

    //获取方式
    private String ggetway;

    //新旧程度
    private String ghownew;

    //浏览人数
    private int gscannum;

    //发布地址
    private String gaddress;

    //变量的get和set方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getGtime() {
        return gtime;
    }

    public void setGtime(String gtime) {
        this.gtime = gtime;
    }

    public String getGtype() {
        return gtype;
    }

    public void setGtype(String gtype) {
        this.gtype = gtype;
    }

    public String getGdetail() {
        return gdetail;
    }

    public void setGdetail(String gdetail) {
        this.gdetail = gdetail;
    }

    public double getGprice() {
        return gprice;
    }

    public void setGprice(double gprice) {
        this.gprice = gprice;
    }

    public double getGoprice() {
        return goprice;
    }

    public void setGoprice(double goprice) {
        this.goprice = goprice;
    }

    public String getGimage() {
        return gimage;
    }

    public void setGimage(String gimage) {
        this.gimage = gimage;
    }

    public int getGstate() {
        return gstate;
    }

    public void setGstate(int gstate) {
        this.gstate = gstate;
    }

    public int getGemergent() {
        return gemergent;
    }

    public void setGemergent(int gemergent) {
        this.gemergent = gemergent;
    }

    public String getGgetway() {
        return ggetway;
    }

    public void setGgetway(String ggetway) {
        this.ggetway = ggetway;
    }

    public String getGhownew() {
        return ghownew;
    }

    public void setGhownew(String ghownew) {
        this.ghownew = ghownew;
    }

    public int getGscannum() {
        return gscannum;
    }

    public void setGscannum(int gscannum) {
        this.gscannum = gscannum;
    }

    public String getGaddress() {
        return gaddress;
    }

    public void setGaddress(String gaddress) {
        this.gaddress = gaddress;
    }
}
