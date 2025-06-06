import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String url= "jdbc:mysql://localhost:3306/hoteldb";
    private static final String username = "root";
    private static final String password = "Lalit@7777";

    public static void main(String args[]) throws ClassNotFoundException,SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("driver loaded");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection con = DriverManager.getConnection(url,username,password);
            Statement stmt = con.createStatement();
            while (true){
                System.out.println();
                System.out.println("WELCOME TO HOTEL MANAGEMENT SYSTEM : ");
                Scanner sc =new Scanner(System.in);
                System.out.println("1: Reserve a room ");
                System.out.println("2: View Reservation ");
                System.out.println("3: Get Room Number ");
                System.out.println("4: Update Reservation ");
                System.out.println("5: Delete Reservation ");
                System.out.println("0: Exit ");
                System.out.println(" choose a option: ");
                int choice = sc.nextInt();

                switch (choice){
                    case 1:
                        reserveRoom(con,sc );
                        break;
                    case 2:
                        viewReservation(con);
                        break;
                    case 3:
                        getRoomNumber(con,sc);
                        break;
                    case 4:
                        updateReservation(con,sc);
                        break;
                    case 5:
                        deleteReservation(con,sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("invalid choice. Try again. ");
                }

            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection con, Scanner sc){
        try{
            System.out.println("Enter Guest Name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservation(gueat_name, room_number,contact_no) VALUES" +
                    "('" + guestName+"'," + roomNumber +",'"+contactNumber+"');";

            try(Statement stmt = con.createStatement()){
                int affectedRows = stmt.executeUpdate(sql);

                if (affectedRows>0){
                    System.out.println("Reservation success!");
                }else {
                    System.out.println("Reservation failed!");
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void viewReservation(Connection con){
        String sql = "SELECT rev_id,gueat_name, room_number,contact_no,rev_data FROM reservation";
        try(Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){
            System.out.println("current reservation; ");
            System.out.println("+----------------+--------------+-----------------+----------------+-----------------+");
            System.out.println("| Reservation ID | Guest        | Room Number     | Contact Number | Reservation Date");
            System.out.println("+----------------+--------------+-----------------+----------------+-----------------+");

            while (rs.next()){
                int reservationId = rs.getInt("rev_id");
                String guestName = rs.getString("gueat_name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_no");
                String reservationDate = rs.getTimestamp("rev_data").toString();
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s  |\n", reservationId,guestName,
                        roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+--------------+-----------------+----------------+-----------------+");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void getRoomNumber(Connection con , Scanner sc){
        try{
            System.out.println("Enter reservation ID: ");
            int rev_id = sc.nextInt();
            System.out.println("Enter guest name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number FROM reservation " + "WHERE rev_id = "
                    +  rev_id  +  " AND gueat_name = '"+ guestName + "'";

            try(Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)){
                if (rs.next()) {
                    int roomNumber = rs.getInt("room_number");
                    System.out.println("Room no for Reservation id "+ rev_id +
                            "and guest "+ guestName+" is " + roomNumber);
                }else {
                    System.out.println("Room no not found for given reservation id and " +
                            "guest name ");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
    public static void updateReservation(Connection con, Scanner sc){
        try{
            System.out.println("Enter your reservation id : ");
            int revs_id = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(con,revs_id)){
                System.out.println("reservation not found for the given id !");
                return;
            }

            System.out.println("Enter the new guest name:");
            String newguestName= sc.nextLine();
            System.out.println("Enter new room number");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter new contact number");
            String newContactNo = sc.next();

            String sql = "UPDATE reservation SET gueat_name = '"+ newguestName + "',"+
                    "room_number = "+ newRoomNumber + ", "+ "contact_no = '"+ newContactNo + "' "+
                    "WHERE rev_id = " + revs_id;

            try(Statement stmt = con.createStatement()){
                int affectedRows = stmt.executeUpdate(sql);

                if (affectedRows>0){
                    System.out.println("reservation updated successfully !");
                }
                else {
                    System.out.println("reservation update failed !");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void deleteReservation(Connection con, Scanner sc ){
        try{
            System.out.println("Enter reservation id : ");
            int reservationId = sc.nextInt();

            if (!reservationExists(con,reservationId)){
                System.out.println("reservation not found for the given id !");
                return;
            }

            String sql = "DELETE FROM reservation WHERE rev_id = " + reservationId;

            try (Statement stmt = con.createStatement()){
                int affectedRows = stmt.executeUpdate(sql);

                if (affectedRows>0){
                    System.out.println("reservation id delected ");
                }
                else {
                    System.out.println("reservation deletion failed! ");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean reservationExists(Connection con , int reservationId){
        try{
            String sql = "SELECT rev_id FROM reservation WHERE rev_id = "+ reservationId;

            try(Statement stmt = con.createStatement();
            ResultSet rs =stmt.executeQuery(sql)){
                return rs.next();
            }
        }catch (SQLException e ){
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException{
        System.out.println("Exiting system ");
        int i=5;
        while (i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("thanks for using hotel reservation system !!!");
    }
}