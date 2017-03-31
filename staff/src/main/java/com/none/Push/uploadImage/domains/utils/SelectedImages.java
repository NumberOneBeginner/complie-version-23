package com.none.Push.uploadImage.domains.utils;


import java.util.List;

import com.none.Push.uploadImage.domains.Image;


public class SelectedImages {

    private List<Image> images;

    public SelectedImages(List<Image> size){

        this.images = size;
    }
    public List<Image> getImages(){

        return images;
    }

}
