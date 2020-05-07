package com.max31415.projects.test;

import com.max31415.util.Display;

public class TestMain {
    public static void main(String[] args){
        Display panel = new TestDisplay();
        panel.init();
        while(!panel.closeRequested){
            System.out.println("hello world");
        }
        System.out.println("goodbye world");
    }
}
