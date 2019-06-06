package com.example.demo.larva; 
public class _BadStateExceptionddos_monitor extends Exception {
public String toString(){
String temp = "";
for (int i = 4; i < getStackTrace().length; i++) temp += "\r\n" + getStackTrace()[i];
return temp;
}
}