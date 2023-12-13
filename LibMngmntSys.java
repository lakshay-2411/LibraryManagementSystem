import javax.swing.plaf.metal.MetalBorders;
import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * takes userID, bookID, Date (automatically)
 * and store it in a file for future reference
 */
class IssueBook implements Serializable{
    public String userID,bookID;
    Date date;
    IssueBook(String userID, String bookID)
    {
        this.userID = userID;
        this.bookID = bookID;
        this.date = new Date();
    }
}

/**
 * Store data of users in a file
 * username, password, role get saved in file as login Info.
 */
class User implements Serializable {
    public String username, password, role;

    Book[] issuedBooks = new Book[100];

    User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

/**
 * Entity Class which is representing a book in our Library Management System
 */
class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    public String name, department, author;
    public int cost, ttlCount, avlCount;
    public long id;
    public String[] tags = new String[100];

    Book(String bookName, String author, String department, int cost, int ttlCount,int avlCount, String[] tags) {

        this.id = System.currentTimeMillis();
        this.name = bookName;
        this.author = author;
        this.department = department;
        this.cost = cost;
        this.ttlCount = ttlCount;
        this.avlCount = avlCount;
        this.tags = tags;

    }
}

/**
 * MyBookList is used to get maximum length of particular Column
 * Based on Length, Borders of Table grow or shrink.
 * @param <T>
 */
class MyBookList<T extends Book> extends ArrayList<T>  implements  Serializable {
    int nameLength = "Book Name".length();
    int departmentLength = "Department".length();
    int authorLength = "Author".length();
    int costLength = "Cost".length();
    int ttlCountLength = "Total Books".length();
    int avlCountLength = "Available Books".length() ;
    int idLength = "Id".length();
    int tagsLength = "Tags".length();
    int totalLength = nameLength + authorLength + departmentLength  + costLength + ttlCountLength + avlCountLength
            + tagsLength;

    @Override
    public boolean add(T b) {
        nameLength = Math.max(nameLength, b.name.length());
        authorLength = Math.max(authorLength, b.author.length());
        departmentLength = Math.max(departmentLength, b.department.length());
        costLength = Math.max(costLength, String.valueOf(b.cost).length());
        ttlCountLength = Math.max(ttlCountLength,String.valueOf(b.ttlCount).length());
        avlCountLength = Math.max(avlCountLength,String.valueOf(b.avlCount).length());
        idLength = Math.max(idLength, String.valueOf(b.id).length());
        tagsLength = Math.max(tagsLength, b.tags.toString().length());

        totalLength = nameLength + authorLength + departmentLength  + costLength + ttlCountLength + avlCountLength
                + tagsLength;
        return super.add(b);
    }

    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);

        nameLength = "Book Name".length();
        departmentLength = "Department".length();
        authorLength = "Author".length();
        costLength = "Cost".length();
        ttlCountLength = "Total Books".length();
        avlCountLength = "Available Books".length() ;
        idLength = "Id".length();
        tagsLength = "Tags".length();
        totalLength = nameLength + authorLength + departmentLength  + costLength + ttlCountLength + avlCountLength
                + tagsLength;


        Iterator itr = this.iterator();

        while(itr.hasNext())
        {
            Book bk = (Book) itr.next();
            nameLength = Math.max(nameLength, bk.name.length());
            authorLength = Math.max(authorLength, bk.author.length());
            departmentLength = Math.max(departmentLength, bk.department.length());
            costLength = Math.max(costLength, String.valueOf(bk.cost).length());
            ttlCountLength = Math.max(ttlCountLength,String.valueOf(bk.ttlCount).length());
            avlCountLength = Math.max(avlCountLength,String.valueOf(bk.avlCount).length());
            idLength = Math.max(idLength, String.valueOf(bk.id).length());
            tagsLength = Math.max(tagsLength, bk.tags.toString().length());
            totalLength = nameLength + authorLength + departmentLength  + costLength + ttlCountLength + avlCountLength
                    + tagsLength;
        }

        return result;
    }

}

/**
 * Library Management Class controlling the CRUD Operation of Books in our System
 */
public class LibMngmntSys {

    public static MyBookList<Book> books = new MyBookList<>();
    public static User[] users = new User[100];
    public static IssueBook[] issuedBooks = new IssueBook[100];

    public static Scanner sc = new Scanner(System.in);
    public static User curUser = null;

    public static void main(String[] args) {

        readBooks();
        readUsers();
        readIssuedBooks();

        curUser = login();
        while (curUser == null) {
            System.out.println("Invalid Credentials, Do you want to Continue (y/n) :");
            String choice = sc.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                curUser = login();
            } else {
                System.exit(0);
            }

        }

        boolean run = true;

        while (run) {
            showMenu();
            int choice = sc.nextInt();
            switch (choice) {
                case 1: {
                    viewBook();
                    break;
                }
                case 2: {
                    searchBook();
                    break;
                }
                case 3: {
                    if (curUser.role.equalsIgnoreCase("admin")) {
                        issueBook();
                    }
                    break;
                }
                case 4:{
                    if (curUser.role.equalsIgnoreCase("admin")) {
                        viewIssuedBooks();
                    }
                    break;
                }
                case 5:{
                    if (curUser.role.equalsIgnoreCase("admin")) {
                        returnIssuedBook();
                    }
                    break;
                }
                case 6: {
                    if (curUser.role.equalsIgnoreCase("admin")) {
                        addBook();
                    }
                    break;
                }
                case 7: {
                    if (curUser.role.equalsIgnoreCase("admin")) {
                        deleteBook();
                    }
                    break;
                }
                case 8: {
                    if (curUser.role.equalsIgnoreCase("admin")) {
                        writeBooks();
                        writeUsers();
                        writeIssuedBooks();
                    }
                    System.out.println("Data has been Saved !!!");
                    break;
                }
                case 9: {
                    if (curUser.role.equalsIgnoreCase("admin")) {
                        addUsers();
                    }
                    break;
                }
                case 10: {
                    if (curUser.role.equalsIgnoreCase("admin")) {
                        viewUsers();
                    }
                    break;
                }
                case 11:
                {
                    if (curUser.role.equalsIgnoreCase("admin")) {
                        writeBooks();
                        writeUsers();
                        writeIssuedBooks();
                        System.out.println("Thanks for visiting the Library!!!");
                        System.exit(0);
                    }
                    System.exit(0);
                }
                default: {
                    System.out.println("Library Management System is Under Process !!!");
                    break;
                }
            }
            System.out.println("\nDo you want to do more Operations(y/n) ");
            char ans = sc.next().charAt(0);
            if (ans == 'y' || ans == 'Y') {
                run = true;
            } else if (ans == 'n' || ans == 'N') {
                run = false;
                if (curUser.role.equalsIgnoreCase("admin")) {
                    writeBooks();
                    writeUsers();
                    writeIssuedBooks();
                    System.out.println("Thanks for visiting the Library!!!");
                    System.exit(0);
                }
            } else {
                System.out.println("You entered wrong value, Please try Again");
                run = true;
            }
        }
    }

    /**
     * Display all Operations for user based on their role
     * Gives all authorities to Admin and certain to User
     */
    public static void showMenu() {
        System.out.println("Welcome to Library Management System !!!");
        System.out.println("\nWhich Operation you want to perform : ");
        System.out.println("1.) View Books");
        System.out.println("2.) Search Books");

        if (curUser.role.equalsIgnoreCase("admin")) {
            System.out.println("3.) Issue Books");
            System.out.println("4.) View Issued Books");
            System.out.println("5.) Return Issued Books");
            System.out.println("6.) Add Books");
            System.out.println("7.) Delete Books");
            System.out.println("8.) Save the Data");
            System.out.println("9.) Add Users");
            System.out.println("10.) View Users");
        }
        System.out.println("11.) Exit");
    }

    /**
     * Fetch the book details from the user and add it in Books Collection.
     */
    public static void addBook() {

    	try {
    		Connection con = DBConnection.getConnection();
    		sc.nextLine();
            System.out.println("Enter Book Name : ");
            String bookName = sc.nextLine();
            while (contains(bookName)) {
                System.out.println("Book with input name already exist, Do you want to continue adding books (y/n): ");
                String choice = sc.nextLine();

                if (choice.equalsIgnoreCase("Y")) {
                    System.out.println("Enter Book Name : ");
                    bookName = sc.nextLine();
                } else {
                    return;
                }
            }
            System.out.println("Enter Author Name : ");
            String author = sc.nextLine();

            System.out.println("Enter Department Name : ");
            String department = sc.nextLine();

            String cost = "";
            boolean askCost = true;
            while (askCost) {
                System.out.println("Enter Book Cost : ");
                cost = sc.nextLine();
                if (!isNumeric(cost)) {
                    System.out.println("Input cost is not in Correct Format, Cost should be in Whole Number !!!");
                } else {
                    askCost = false;
                }
            }
            System.out.println("Enter the Total number of Copies for Book :");
            int ttlBooks = sc.nextInt();
            int avlBooks = ttlBooks;
            sc.nextLine();
            System.out.println("Enter Tags representing Books using commas(,) and Press Enter to Skip : ");
            String inputTags = sc.nextLine().trim();

            ArrayList<String> tags = new ArrayList<>(Arrays.asList(inputTags.split(",")));
            String sql = "INSERT INTO book_details(name,author,department,cost,ttlCount,avlCount,tags) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, bookName);
            pst.setString(2, author);
            pst.setString(3, department);
            pst.setInt(4, Integer.parseInt(cost));
            pst.setInt(5, ttlBooks);
            pst.setInt(6, avlBooks);
            pst.setString(7, String.join(",", tags));
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("New Book added to the Library Successfully");
            } else {
                System.out.println("Failed to add the book");
            }
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    /**
     * Displays all the books in out Library on the Console.
     */
    public static void viewBook() {

    	
    	try {
    		Connection con = DBConnection.getConnection();
    		
    		Iterator itr = books.iterator();

            String author="", department = "";
            System.out.println("Do you want to apply Filter (y/n), Press Enter to Skip");
            sc.nextLine();
            String choice = sc.nextLine();
            
            if (choice.equalsIgnoreCase("Y")) {
                System.out.println("Enter Author Name to apply author Filter, Press Enter to Skip: ");
                author = sc.nextLine();

                System.out.println("Enter Department Name to apply Department Filter, Press Enter to Skip: : ");
                department = sc.nextLine();

                
                String sql = "SELECT* From book_details WHERE author LIKE ? AND department LIKE ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, "%" + author + "%");
                pst.setString(2, "%" + department + "%");
                
                ResultSet rs = pst.executeQuery();
                
                System.out.printf("| %-" + (books.nameLength + 3) + "s | %-" + (books.authorLength + 3) + "s | %-" + (books.departmentLength + 3) +
                	    "s | %-" + (books.costLength + 3) + "s | %-" + (books.ttlCountLength + 3) + "s | %-" + (books.avlCountLength + 3) + "s | %-" +
                	    (books.tagsLength + 3) + "s |\n",
                	    "Book Name", "Author", "Department", "Cost",
                	    "Total Books", "Available Books", "Tags");
                
                while (rs.next()) {
                    System.out.printf("| %-" + books.nameLength + "s | %-" + books.authorLength + "s | %-" + books.departmentLength +
                            "s | %-" + books.costLength + "d | %-" + books.ttlCountLength + "d | %-" + books.avlCountLength + "d | %-" +
                            books.tagsLength + "s |\n",
                            rs.getString("name"), rs.getString("author"),
                            rs.getString("department"), rs.getInt("cost"),
                            rs.getInt("ttlCount"), rs.getInt("avlCount"),
                            rs.getString("tags"));
                }
            }else {
            	String sql = "SELECT* From book_details";
            	PreparedStatement pst = con.prepareStatement(sql);
            	
            	ResultSet rs = pst.executeQuery();
            	
            	System.out.printf("| %-" + (books.nameLength + 3) + "s | %-" + (books.authorLength + 3) + "s | %-" + (books.departmentLength + 3) +
            		    "s | %-" + (books.costLength + 3) + "s | %-" + (books.ttlCountLength + 3) + "s | %-" + (books.avlCountLength + 3) + "s | %-" +
            		    (books.tagsLength + 3) + "s |\n",
            		    "Book Name", "Author", "Department", "Cost",
            		    "Total Books", "Available Books", "Tags");
            	
            	while (rs.next()) {
            	    System.out.printf("| %-" + books.nameLength + "s | %-" + books.authorLength + "s | %-" + books.departmentLength +
            	            "s | %-" + books.costLength + "d | %-" + books.ttlCountLength + "d | %-" + books.avlCountLength + "d | %-" +
            	            books.tagsLength + "s |\n",
            	            rs.getString("name"), rs.getString("author"),
            	            rs.getString("department"), rs.getInt("cost"),
            	            rs.getInt("ttlCount"), rs.getInt("avlCount"),
            	            rs.getString("tags"));
            	}
            	}
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    	}

        }

    /**
     * This can search the book from the Books Catalogue based on
     * . Matching the exact input keyword by ignoring case
     * . Matching if the book contains the input text ignoring case
     * . Matching by using the specified tags
     */
    public static void searchBook() {
    	
    	
    	try {
    		Connection con = DBConnection.getConnection();
    		
    		Iterator itr = books.iterator();
            System.out.println("Enter Book Name you want to Search");
            sc.nextLine();
            String searcher = sc.nextLine();
            
            String sql = "SELECT * FROM book_details WHERE name = ? OR LOWER(name) LIKE LOWER(?) OR tags LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, searcher);
            pst.setString(2, "%" + searcher + "%");
            pst.setString(3, "%" + searcher + "%");
    		
            ResultSet rs = pst.executeQuery();
            System.out.println("Book Name    " + "    Author    " + "      Department   " + "      Cost    "
                    + "    Tags     ");
            while (rs.next()) {
                String name = rs.getString("name");
                String author = rs.getString("author");
                String department = rs.getString("department");
                int cost = rs.getInt("cost");
                String tags = rs.getString("tags");

                System.out.printf("%-16s %-15s %-18s %-12d %-10s\n", name, author, department, cost, tags);
            }
            rs.close();
            pst.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }

    /**
     * Delete book from Collection of Books
     * by taking Book Name from the user.
     */
    public static void deleteBook() {
    	
    	
    	try {
    		Connection con = DBConnection.getConnection();
    		
    		System.out.println("Enter Book Name you want to delete");
            sc.nextLine();
            String delete = sc.nextLine();
            
            String sql = "DELETE FROM book_details WHERE name = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, delete);
            
            int rowsAffected = pst.executeUpdate();
            
            if(rowsAffected > 0) {
            	System.out.println("\nBook with this particular Data has been deleted");
            }else {
            	System.out.println("\n There is no Book in the library with the matching entered book name");
            }
            
            pst.close();
            
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    /**
     * Checks weather given string is Numeric or Not.
     */
    public static boolean isNumeric(final String str) {

        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;

    }

    /** Return true or false based on book name exist or not
     */
    public static boolean contains(final String name) {
        Iterator itr = books.iterator();

        while (itr.hasNext()) {
            Book book = (Book) itr.next();
            if (book.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * . Check weather a string is empty or not
     * . Check weather a string contains other or not
     */
    public static boolean equalsIgnoreEmpty(final String x, final String y) {
        if (x.isEmpty() || y.isEmpty()) {
            return true;
        } else {
            return x.equalsIgnoreCase(y);
        }
    }

    /**
     * This function compares the coast input by user,
     * the input coast could be a single value representing exact max.
     * two values representing the int-between logic.
     *
     * @return true/false
     */
    public static boolean compareCost(String[] inputRange, int bookCost) {
        // case when user skips the coast filter by pressing enter then inputRange = [""]
        if (inputRange.length == 1 && inputRange[0].isEmpty()) {
            return true;
        }
        // case when user enters single coast eg : ["100"]
        else if (inputRange.length == 1) {
            return equalsIgnoreEmpty(inputRange[0].trim(), Integer.toString(bookCost));
        }

        // case when user enter more thn one cost eg : ["100", "200"]
        else
            return bookCost >= Integer.parseInt(inputRange[0].trim()) && bookCost <= Integer.parseInt(inputRange[1].trim());
    }

    /** Add Users based on their unique Username, password and role.
     */
    public static void addUsers() {
    	try {
    		Connection connection = DBConnection.getConnection();
    		sc.nextLine();
            System.out.println("Enter Username : ");
            String userName = sc.nextLine();
            while (contains(userName)) {
                System.out.println("User with input name already exist, Do you want to continue (y/n): ");
                String choice = sc.nextLine();

                if (choice.equalsIgnoreCase("Y")) {
                    System.out.println("Enter Username : ");
                    userName = sc.nextLine();
                } else {
                    return;
                }
            }
            System.out.println("Enter Password : ");
            String password = sc.nextLine();

            boolean askRole = true;
            String role = "";
            
         // Keep Asking role until the user enter correct role.
            while (askRole) {
                System.out.println("Enter Role of Person (User/Admin) :");
                role = sc.nextLine();
                if (role.equalsIgnoreCase("User") || role.equalsIgnoreCase("Admin")) {
                    askRole = false;
                } else {
                    System.out.println("Please Enter valid Role (User/Admin): ");
                }
            }
            
            String sql = "INSERT INTO users(username,password,role) VALUES (?,?,?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, userName);
            pst.setString(2, password);
            pst.setString(3, role);
            
            int rowsAffected = pst.executeUpdate();
            if(rowsAffected > 0) {
            	System.out.println("User is added!!!");
            }else {
            	System.out.println("Failed to add user!!!");
            }
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    /** View Users by their username and role
     * have functionality to also show all Issued Books
     */
    public static void viewUsers() {
    	
    	try {
    		Connection con = DBConnection.getConnection();
    		Statement st = con.createStatement();
    		
    		ResultSet rs = st.executeQuery("SELECT* From users");
    		
    		System.out.println("UserName     " + "    Roles    " + " Issued Book Name " );
    		
    		while(rs.next()) {
    			String username = rs.getString("username");
    			String role = rs.getString("role");
    			
    			// Fetch issued book names for the current user
    			int userId = getUserIdFromUsername(username);
                StringBuilder issuedBooksStr = new StringBuilder();
                // Use JOIN to fetch book names directly in the query
                String query = "SELECT DISTINCT b.name FROM issue_book_details ibd " +
                        "JOIN book_details b ON ibd.book_id = b.id " +
                        "WHERE ibd.user_id = ? && status = 'Pending'";
                
                try (PreparedStatement pst = con.prepareStatement(query)) {
                    pst.setInt(1, userId);
                    ResultSet issuedBooksRS = pst.executeQuery();
                    
                    while (issuedBooksRS.next()) {
                        issuedBooksStr.append(issuedBooksRS.getString("name")).append(",");
                    }

                    if (issuedBooksStr.length() > 0) {
                        issuedBooksStr = new StringBuilder(issuedBooksStr.substring(0, issuedBooksStr.length() - 1));
                    }
                    System.out.printf("%-16s %-15s %-15s\n", username, role, issuedBooksStr.toString());

                    issuedBooksRS.close();
                }
    		}
    		rs.close();
            st.close();
    		
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    /**Takes username and password as Input &
     * Checks weather they exist in the system or not
     * and gives them permission to login into  the system.
     */
    public static User login() {
        System.out.println("Enter Username : ");
        String username = sc.nextLine();
        System.out.println("Enter Password : ");
        String password = sc.nextLine();

        try {
        	Connection con = DBConnection.getConnection();
        	PreparedStatement pst = con.prepareStatement("Select * from users where username = ? AND password = ?");
        	pst.setString(1, username);
        	pst.setString(2, password);
        	
        	ResultSet rs = pst.executeQuery();
        	
        	if(rs.next()) {
        		String role = rs.getString("role");
        		return new User(username,password,role);
        	}
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return null;
    }

    /**
     * Issue Book to particular username based on :
     * availability of book
     * User already issued that book or not
     */
    public static void issueBook() {
    	String choice = "y";
    	int userId = -1;
        User toBeIssuedUser = null;
        sc.nextLine();

        // Keep asking the username until the entered username is correct.
        // Has the provision to abort issueBook.
        while (choice.equalsIgnoreCase("y")) {
            System.out.println("Enter the username to whom the Books should be Issued : ");
            String username = sc.nextLine();

            try {
                Connection connection = DBConnection.getConnection();

                // Check if the user exists
                PreparedStatement checkUserStmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
                checkUserStmt.setString(1, username);

                ResultSet userResult = checkUserStmt.executeQuery();
                if (userResult.next()) {
                    toBeIssuedUser = new User(userResult.getString("username"), userResult.getString("password"), userResult.getString("role"));
                    userId  = userResult.getInt("id");
                    break;
                } else {
                    System.out.println("Entered Username doesn't exist in our System, Do you want to Re-enter (y/n) ");
                    choice = sc.nextLine();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // If toBeIssuedUser is null here, then the user has chosen to abort issueBook.
        if (toBeIssuedUser == null) {
            return;
        }

        String bookName;
        choice = "y";
        while (choice.equalsIgnoreCase("y")) {
            System.out.println("Enter the Book Name to whom the Books should be Issued : ");
            bookName = sc.nextLine();

            try {
                Connection connection = DBConnection.getConnection();

                // Check if the book exists
                PreparedStatement checkBookStmt = connection.prepareStatement("SELECT * FROM book_details WHERE name = ?");
                checkBookStmt.setString(1, bookName);

                ResultSet bookResult = checkBookStmt.executeQuery();
                if (bookResult.next()) {
                    // Book exists, proceed with issuing
                	String tagsString = bookResult.getString("tags");
                	String [] tags = tagsString.split(",");
                    Book toBeIssuedBook = new Book(bookResult.getString("name"), bookResult.getString("author"), bookResult.getString("department"), bookResult.getInt("cost"), bookResult.getInt("ttlCount"), bookResult.getInt("avlCount"), tags);

                 // Check if the book has already been issued to this user
                    PreparedStatement checkIssuedStmt = connection.prepareStatement("SELECT * FROM issue_book_details WHERE user_id = ? AND book_id = ? AND status = 'Pending'");
                    checkIssuedStmt.setInt(1, userId);
                    checkIssuedStmt.setInt(2, bookResult.getInt("id"));

                    if (checkIssuedStmt.executeQuery().next()) {
                        System.out.println("Book has already been Issued to this User");
                        return;
                    }

                    // Insert into issued_books table
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String issueDate = dateFormat.format(new Date());

                    // Calculate the return date, for example, 7 days from the issue date
                    Date currentDate = new Date();
                    Date returnDate = new Date(currentDate.getTime() + (7 * 24 * 60 * 60 * 1000));
                    String formattedReturnDate = dateFormat.format(returnDate);

                    PreparedStatement insertIssuedStmt = connection.prepareStatement("INSERT INTO issue_book_details(user_id, book_id, date_issued, return_date, status) VALUES (?, ?, ?, ?, ?)");
                    insertIssuedStmt.setInt(1, userId);
                    insertIssuedStmt.setInt(2, bookResult.getInt("id"));
                    insertIssuedStmt.setString(3, issueDate);
                    insertIssuedStmt.setString(4, formattedReturnDate);
                    insertIssuedStmt.setString(5, "Pending"); // Assuming initially the status is "Pending"
                    insertIssuedStmt.executeUpdate();

                    // Update the avlCount in the books table
                    PreparedStatement updateBookStmt = connection.prepareStatement("UPDATE book_details SET avlCount = avlCount - 1 WHERE name = ?");
                    updateBookStmt.setString(1, bookName);
                    updateBookStmt.executeUpdate();

                    System.out.println("Book Has been Issued to User");
                    break;

                } else {
                    System.out.println("Entered Book name doesn't exist in our System, Do you want to Re-enter (y/n) ");
                    choice = sc.nextLine();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    private static int getUserIdFromUsername(String username) {
    	int userId = -1;
    	try {
    		Connection con = DBConnection.getConnection();
    		String sql = "SELECT id From users WHERE username = ?";
    		PreparedStatement pst = con.prepareStatement(sql);
    		pst.setString(1, username);
    		
    		ResultSet rs = pst.executeQuery();
    		if(rs.next()) {
    			userId = rs.getInt("id");
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return userId;
    }
    
    private static int getBookIdFromName(String bookName) {
    	int bookId = -1;
    	try {
    		Connection con = DBConnection.getConnection();
    		String sql = "SELECT id From book_details WHERE name = ?";
    		PreparedStatement pst = con.prepareStatement(sql);
    		pst.setString(1, bookName);
    		
    		ResultSet rs = pst.executeQuery();
    		if(rs.next()) {
    			bookId = rs.getInt("id");
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return bookId;
    }
    
    private static String getUserNameFromId(int userId) {
    	String userName = null;
    	try {
    		Connection con = DBConnection.getConnection();
    		String sql = "SELECT username From users WHERE id = ?";
    		PreparedStatement pst = con.prepareStatement(sql);
    		pst.setInt(1, userId);
    		
    		ResultSet rs = pst.executeQuery();
    		if(rs.next()) {
    			userName = rs.getString("username");
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return userName;
    }
    
    private static String getBookNameFromId(int bookId) {
    	String bookName = null;
    	try {
    		Connection con = DBConnection.getConnection();
    		String sql = "SELECT name From book_details WHERE id = ?";
    		PreparedStatement pst = con.prepareStatement(sql);
    		pst.setInt(1, bookId);
    		
    		ResultSet rs = pst.executeQuery();
    		if(rs.next()) {
    			bookName = rs.getString("name");
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return bookName;
    }
    
    

    /**
     * View Issued Books based on filters also
     * Shows Username, BookName, Date
     */
    public static void viewIssuedBooks(){

    	try {
    		Connection con = DBConnection.getConnection();
    		String user = "", book = "";
            System.out.println("Do you want to apply Filter (y/n), Press Enter to Skip");
            sc.nextLine();
            String choice = sc.nextLine();
            
            String sql = "SELECT * FROM issue_book_details WHERE 1=1";
            
            if (choice.equalsIgnoreCase("Y")) {
                System.out.println("Enter User Name to apply User Filter, Press Enter to Skip: ");
                user = sc.nextLine();

                System.out.println("Enter Book Name to apply Book Filter, Press Enter to Skip: : ");
                book = sc.nextLine();
                
                if (!user.isEmpty()) {
                    int userId = getUserIdFromUsername(user);
                    sql += " AND user_id = " + userId;
                }
                
                if (!book.isEmpty()) {
                    int bookId = getBookIdFromName(book);
                    sql += " AND book_id = " + bookId;
                }
            }
            
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            System.out.println("User Name    " + " Book Name    " + "   Issued Date   " + " Status    " );
            while(rs.next()) {
            	int userId = rs.getInt("user_id");
                int bookId = rs.getInt("book_id");

                String userName = getUserNameFromId(userId);
                String bookName = getBookNameFromId(bookId);
                String date = rs.getString("date_issued");
                String status = rs.getString("status");
                System.out.printf("%-16s %-15s %-18s %-12s \n", userName, bookName, date,status);
            }
            
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    /**
     * Return Issued Books based on several Criteria
     * Check for Username, whether such person Exist or Not.
     * Check weather book is Issued to particular User or Not.
     * Increase Available Count of Book when Returned in Library.
     */
    public static void returnIssuedBook() {
        String choice = "y";
        User toBeIssuedUser = null;
        sc.nextLine();
        // keep asking the username till the entered username is not correct.
        // Has the provision to abort issueBook
        while (choice.equalsIgnoreCase("y")) {
            System.out.println("Enter the username who want to return Issued Book : ");
            String username = sc.nextLine();

            try {
            	Connection con = DBConnection.getConnection();
            	PreparedStatement pst = con.prepareStatement("Select * from users WHERE username = ?");
            	pst.setString(1, username);
            	
            	ResultSet rs = pst.executeQuery();
            	if(rs.next()) {
            		toBeIssuedUser = new User(rs.getString("username"), rs.getString("password"), rs.getString("role"));
            		break;
            	} else {
            		System.out.println("Entered Username doesn't exist in our System, Do you want to Re-enter (y/n) ");
            		choice = sc.nextLine();
            	}
            }catch(Exception e) {
            	e.printStackTrace();
            }
        }
     // if toBeIssued is null here, then user has chosen to abort issueBook.
        if(toBeIssuedUser == null)
        {
            return;
        }
            

        IssueBook toBeIssuedBook = null;
        String bookName;

        System.out.println("Enter the Issued Book Name to be Returned : ");
        bookName = sc.nextLine();
        
        try {
        	Connection con = DBConnection.getConnection();
        	PreparedStatement pst1 = con.prepareStatement("Select * from issue_book_details WHERE user_id = ? AND book_id = ? AND status = 'Pending'");
        	
        	pst1.setInt(1, getUserIdFromUsername(toBeIssuedUser.username));
        	pst1.setInt(2, getBookIdFromName(bookName));
            
            ResultSet rs = pst1.executeQuery();
            
            if(!rs.next()) {
            	System.out.println("Book has not been Issued to the User");
            	return;
            }
            
            PreparedStatement pst2 = con.prepareStatement("UPDATE issue_book_details SET status = 'Returned' WHERE user_id = ? AND book_id = ?");
            pst2.setInt(1, getUserIdFromUsername(toBeIssuedUser.username));
            pst2.setInt(2, getBookIdFromName(bookName));
            pst2.executeUpdate();
            
            PreparedStatement pst3 = con.prepareStatement("UPDATE book_details SET avlCount = avlCount + 1 WHERE name = ?");
            pst3.setString(1, bookName);
            pst3.executeUpdate();
            System.out.println("Book has been successfully returned");
        }catch(Exception e) {
        	e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
	public static void readBooks() {
        try {
            FileInputStream f = new FileInputStream(new File("myBooksObject.txt"));
            ObjectInputStream o = new ObjectInputStream(f);

            books = (MyBookList<Book>) o.readObject();


            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.print("");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeBooks() {
    	
    	try {
    		Connection con = DBConnection.getConnection();
    		Statement st = con.createStatement();
    		ResultSet rs = st.executeQuery("Select * from book_details");
    		
    		// Specify the file path
            String filePath = "myBooksObject.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
         // Write header line
            bufferedWriter.write("Name,Author,Department,Cost,TtlCount,AvlCount,Tags");
    		
    		while(rs.next()) {
    			String name = rs.getString("name");
    			String author = rs.getString("author");
    			String department = rs.getString("department");
    			int cost = rs.getInt("cost");
    			int ttlCount = rs.getInt("ttlCount");
    			int avlCount = rs.getInt("avlCount");
    			
    			// Convert tags from a comma-separated string to ArrayList
                String tagsString = rs.getString("tags");
                ArrayList<String> tags = new ArrayList<>(Arrays.asList(tagsString.split(",")));

             // Write book data to the file
                bufferedWriter.newLine();
                bufferedWriter.write(name + "," + author + "," + department + "," +
                        cost + "," + ttlCount + "," + avlCount + "," + String.join(",", tags));
    		}
            
    		bufferedWriter.close();
    		fileWriter.close();
            
            System.out.println("Books data written to the file successfully.");
    		
    	}catch(SQLException | IOException e) {
    		e.printStackTrace();
    	}
    }

    @SuppressWarnings("unchecked")
	public static void readUsers() {
        try {
            FileInputStream f = new FileInputStream(new File("myUsersObject.txt"));
            ObjectInputStream o = new ObjectInputStream(f);

            users = (User[]) o.readObject();

            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.print("");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeUsers() {
    	try {
    		Connection con = DBConnection.getConnection();
    		Statement st = con.createStatement();
    		ResultSet rs = st.executeQuery("Select * from users");
    		
    		// Specify the file path
            String filePath = "myUsersObject.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
         // Write header line
            bufferedWriter.write("Username,Password,Role");
    		
    		while(rs.next()) {
    			String username = rs.getString("username");
    			String password = rs.getString("password");
    			String role = rs.getString("role");

             // Write user data to the file
                bufferedWriter.newLine();
                bufferedWriter.write(username + "," + password + "," + role);
    		}
            
    		bufferedWriter.close();
    		fileWriter.close();
            
            System.out.println("Books data written to the file successfully.");
    		
    	}catch(SQLException | IOException e) {
    		e.printStackTrace();
    	}
    }

    @SuppressWarnings("unchecked")
	public static void readIssuedBooks() {
        try {
            FileInputStream f = new FileInputStream(new File("myIssuedBooksObject.txt"));
            ObjectInputStream o = new ObjectInputStream(f);

            issuedBooks = (IssueBook[])o.readObject();

            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.print("");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeIssuedBooks() {
        try {
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM issue_book_details");

            // Specify the file path
            String filePath = "myIssuedBooksObject.txt";
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

         // Write header line
            bufferedWriter.write("UserName,BookName,DateIssued,ReturnDate,Status");

            while (resultSet.next()) {
                // Extract data from the result set
                int userId = resultSet.getInt("user_id");
                int bookId = resultSet.getInt("book_id");
                String dateIssued = resultSet.getString("date_issued");
                String returnDate = resultSet.getString("return_date");
                String status = resultSet.getString("status");
                
             // Convert user and book IDs into names
                String userName = getUserNameFromId(userId);
                String bookName = getBookNameFromId(bookId);


             // Write issued book data to the file
                bufferedWriter.newLine();
                bufferedWriter.write(userName + "," + bookName + "," + dateIssued + "," +
                        returnDate + "," + status);
            }

            // Close writers
            bufferedWriter.close();
            fileWriter.close();

            System.out.println("Issued books data written to the file successfully.");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


}
