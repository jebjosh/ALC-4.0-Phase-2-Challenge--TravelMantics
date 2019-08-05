package com.jeb.travelmantics;

import java.io.Serializable;

public class  TravelDeal  implements Serializable {

    private String id;
    private String title;
    private String price;
    private String desc;
    private String imageUrl;
    private String imageName;

    public  TravelDeal(){}
//
//    public TravelDeal(String id, String title,  String price,String desc, String imageUrl) {
//        this.setId(id);
//        this.setTitle(title);
//        this.setDesc(desc);
//        this.setPrice(price);
//        this.setImageUrl(imageUrl);
//    }
//
    public TravelDeal(String id, String title,  String price,String desc, String imageUrl,String imageName) {
        this.setId(id);
        this.setTitle(title);
        this.setDesc(desc);
        this.setPrice(price);
        this.setImageUrl(imageUrl);
        this.setImageName(imageName);
    }

    public TravelDeal( String title, String price,String desc,  String imageUrl) {

        this.setTitle(title);
        this.setDesc(desc);
        this.setPrice(price);
        this.setImageUrl(imageUrl);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
