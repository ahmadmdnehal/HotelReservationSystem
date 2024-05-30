package org.example;

import java.sql.*;
import java.util.Scanner;

public class HotelReservation {
    public static final String url = "jdbc:mysql://localhost:3306/hotel_database";
    public static final String username = "root";
    public static final String password = "Nehal@123";

    public static void main (String[] args) {
        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            while (true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete reservation");
                System.out.println("0. Exit...");
                System.out.println("Choose an Option");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1:
                        ReserveReservation(connection,scanner);
                        break;
                    case 2:
                        ViewReservation(connection);
                        break;
                    case 3:
                        GetRoomNumber(connection,scanner);
                        break;
                    case 4:
                        UpdateReservation(connection,scanner);
                        break;
                    case 5:
                        DeleteReservation(connection,scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid Choice, try again");
                }
                
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void exit() throws InterruptedException {
        System.out.println("Exiting the System !!");
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
    private static void DeleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter Reservation Id to delete: ");
            int reservationId = scanner.nextInt();
            if (!ReservationIdExist(connection, reservationId)) {
                System.out.println("Reservation Not Found For given Id ");
                return;
            }

            String sql = "DELETE FROM reservation WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int rowAffect = statement.executeUpdate(sql);
                if (rowAffect > 0) {
                    System.out.println("Reservation Deletion successFully!!");
                } else {
                    System.out.println("Reservation deletion Failed !!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void UpdateReservation (Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter Reservation id to Update: " );
            int ReservationId = scanner.nextInt();
            scanner.nextLine();
            if (!ReservationIdExist(connection,ReservationId)){
                System.out.println("Reservation not found");
                return;
            }
            System.out.println("Enter a new guest name : ");
            String NewGuestName = scanner.nextLine();
            System.out.println("Enter new room Number : ");
            int NewRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact Number : ");
            String NewContactNumber = scanner.next();
            String sql = "UPDATE reservation SET guest_name = '" + NewGuestName + "', " +
                    "room_number = " + NewRoomNumber + ", " +
                    "contact_number = '" + NewContactNumber + "' " +
                    "WHERE reservation_id = " + ReservationId;
            try (Statement statement = connection.createStatement()){
                int affectedRow = statement.executeUpdate(sql);
                if (affectedRow>0){
                    System.out.println("Reservation Update successfully ! ");
                }else {
                    System.out.println("Reservation Update Failed");
                }
                
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static boolean ReservationIdExist (Connection connection,int ReservationID) {
        try {
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = " + ReservationID;
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)){
                return  resultSet.next();
            }

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static void GetRoomNumber (Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter Reservation ID: ");
            int ReservationId = scanner.nextInt();
            System.out.println("Enter Guest Name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservation " +
                    "WHERE reservation_id = " + ReservationId +
                    " AND guest_name = '" + guestName + "'";


            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)){
                if (resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room Number for Reservation Id "+ ReservationId +
                            " And guest "+ guestName + " is : "+ roomNumber);
                }else {
                    System.out.println("Reservation Not Found For the Given Id and Given guest Name");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static void ViewReservation (Connection connection) {
        String sql1 = "SELECT * FROM reservation";
        String sql2 = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservation";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql2)){

            System.out.println("Current Reservation");
            System.out.println("+----------------+----------------+----------------+------------------+------------------------------+");
            System.out.println("| Reservation ID |  Guest         | Room Number    |  Contact Number  |  Reservation date            |");
            System.out.println("+----------------+----------------+----------------+------------------+------------------------------+");

            while (resultSet.next()){
                int reservationID = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                // format and display the reservation  data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationID,guestName,roomNumber,contactNumber,reservationDate);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void ReserveReservation (Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter Room Number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact Number");
            String contactNumber = scanner.next();

//            String ReserveQuery =

            String sql = "INSERT INTO reservation (guest_name, room_number, contact_number)"+
                    "VALUES( '"+guestName+"' , "+roomNumber+" ,'"+contactNumber+"')";
            try (Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows>0){
                    System.out.println("Reservation Successful!!");
                }else {
                    System.out.println("Reservation Failed!");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
