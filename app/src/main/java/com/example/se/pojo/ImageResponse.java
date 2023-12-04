package com.example.se.pojo;

import java.util.List;

public class ImageResponse {
    private String image;

    private List<String> labels;
    private List<String> additional_text;

    public String getImage() {
        return image;
    }


    public List<String> getLabels() {
        return labels;
    }

    public List<String> getAdditionalText() {
        return additional_text;
    }
}
