package com.company;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
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
                                                    rs.getInt(4),
                                                    rs.getString(5),
                                                    rs.getInt(6),
                                                    rs.getString(7),
                                                    rs.getString(8),
                                                    rs.getInt(9),
                                                    rs.getString(10),
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


    public static Connection connection;

    public static void OpenConnection(String dbname){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname, "postgres", "postgres");
            System.out.println("connection " + dbname + " открыто");
        }
        catch (Exception e){
            System.out.println("Ошибка открытия connection " + dbname + " " + e.getMessage());
        }
    }

    public static void CloseConnection() {
        // закрытие бд
        try {
            connection.close();
            System.out.println("connection закрыто");
        }
        catch (Exception e){
            System.out.println("Ошибка закрытия connection " + e.getMessage());
        }
    }

    public static void CreateDBWithTables() {
        // создание бд
        try {
            OpenConnection("");
            Statement st = connection.createStatement();
            st.execute("DROP DATABASE bruh; CREATE DATABASE BRUH;");
            st.close();
            CloseConnection();
        } catch (Exception e) {
            System.out.println("Ошибка при создании бд " + e.getMessage());
        }

        // создание таблиц
        try {
            OpenConnection("bruh");
            Statement st = connection.createStatement();
            st.execute(new String(Files.readAllBytes(Paths.get(
                    "C:\\Users\\Aisen Sousuke\\OneDrive\\учебное дерьмо\\10 трим\\тррррррррррррррп\\lab 2\\lab2\\createNormalisedDatabaseScript.txt"))));
            st.close();
            CloseConnection();
        } catch (IOException exception){
            System.out.println("Ошибка при чтении скрипта создания табличек " + exception.getMessage());
        } catch (SQLException exception){
            System.out.println("Ошибка при создании табличек " + exception.getMessage());
        }
    }

    public static void PrintToPostgress(FirstNFLine line) {
        try {
            OpenConnection("bruh");
            PreparedStatement st = connection.prepareStatement("""
                            INSERT INTO breed (id, name) VALUES (?, ?) ON CONFLICT DO NOTHING;
                            INSERT INTO place (id, name, type) VALUES (?, ?, ?) ON CONFLICT DO NOTHING;
                            INSERT INTO food (id, name, price) VALUES (?, ?, ?) ON CONFLICT DO NOTHING;
                            INSERT INTO cat (id, name, color, breed_id) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING;
                            INSERT INTO cat_places(cat_id, place_id) VALUES (?, ?) ON CONFLICT DO NOTHING;
                            INSERT INTO taste_preferences(cat_id, food_id) VALUES (?, ?) ON CONFLICT DO NOTHING;""");
            st.setInt(1, line.breed_id);
            st.setString(2, line.breed);
            st.setInt(3, line.place_id);
            st.setString(4, line.place_name);
            st.setString(5, line.type);
            st.setInt(6, line.food_id);
            st.setString(7, line.food_name);
            st.setInt(8, line.price);
            st.setInt(9, line.cat_id);
            st.setString(10, line.cat_name);
            st.setString(11, line.color);
            st.setInt(12, line.breed_id);
            st.setInt(13, line.cat_id);
            st.setInt(14, line.place_id);
            st.setInt(15, line.cat_id);
            st.setInt(16, line.food_id);
            st.executeUpdate();
            st.close();
            CloseConnection();
        } catch (Exception e){
            System.out.println("Ошибка при записи строки " + line.toString() + " " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        CreateDBWithTables();
        Read1NF(Main::PrintToPostgress);
//        if (args != null)
//            if (args[0].equals("1"))
//                System.out.println("сокеты");
//            else if (args[0].equals("2"))
//                System.out.println("очередь сообщений");
//            else Read1NF(Main::consoleLineSender);
    }
}
