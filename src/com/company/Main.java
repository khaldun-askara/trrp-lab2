package com.company;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.function.Consumer;

public class Main {

    public static void Read1NF(Consumer<FirstNFLine> lineSender){
        Connection co;
        // открытие бд
        try {
            Class.forName("org.sqlite.JDBC");
            co = DriverManager.getConnection ("jdbc:sqlite:C:\\Users\\Aisen Sousuke\\OneDrive\\учебное дерьмо\\10 трим\\тррррррррррррррп\\lab 2\\lab2\\cats_food_and places.sqlite");
            System.out.println("cats_food_and places.sqlite открыта");
            // чтение из бд и построчная отправка
            try{
                Statement st = co.createStatement();
                ResultSet rs = st.executeQuery("""
                                                    SELECT cat_id,
                                                    cat_name,
                                                    color,
                                                    breed_id,
                                                    breed,
                                                    place_id,
                                                    place_name,
                                                    type,
                                                    food_id,
                                                    food_name,
                                                    price
                                                    FROM table_name""");
                while (rs.next())
                {
                    lineSender.accept(new FirstNFLine(rs.getInt(1),
                                                    rs.getString(2),
                                                    rs.getString(3),
                                                    rs.getString(4),
                                                    rs.getInt(5),
                                                    rs.getString(6),
                                                    rs.getString(7),
                                                    rs.getInt(8),
                                                    rs.getString(9),
                                                    rs.getInt(10),
                                                    rs.getInt(11)));
                }
                st.close();
            }
            catch (Exception e){
                System.out.println("Ошибка чтения из cats_food_and places.sqlite " + e.getMessage());}

            // закрытие бд
            try {
                co.close();
                System.out.println("cats_food_and places.sqlite закрыта");
            }
            catch (Exception e){
                System.out.println("Ошибка закрытия cats_food_and places.sqlite " + e.getMessage());}
        }
        catch (Exception e){
            System.out.println("Ошибка открытия cats_food_and places.sqlite " + e.getMessage());
        }
    }

    public static void consoleLineSender(FirstNFLine line){
        System.out.println(line.toString());
    }

    public static void main(String[] args) {
        if (args != null)
            if (args[0].equals("1"))
                System.out.println("сокеты");
            else if (args[0].equals("2"))
                System.out.println("очередь сообщений");
            else Read1NF(Main::consoleLineSender);
    }
}
