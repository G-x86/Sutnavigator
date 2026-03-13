package User;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Data.Building;
import Data.DataAll;
import Data.Room;

public class Datauser {

	public static String username = null;
	private String email;
	private String password;

	// Database Configuration
//	private static final String DB_URL = "";
//
//	private static final String DB_USER = "admin";
//
//	private static final String DB_PASS = "";

	 private static final String DB_URL =
	 "jdbc:mysql://172.27.152.146:3306/sut_navigator";

	 private static final String DB_USER = "admin";

	 private static final String DB_PASS = "1234";

	private static final String CACHE_FILE = "cache_data.txt";

	// In-Memory Cache List (เก็บข้อมูลไว้ใน RAM ตลอดเวลาที่เปิดโปรแกรม)
	private static List<DataAll> memoryCache = new ArrayList<>();

	public Datauser(String username, String email, String password) {
		Datauser.username = username;
		this.email = email;
		this.password = password;
	}

	public Datauser() {
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
	}

	// =============================================================
	// PART 1: Authentication & User Management
	// =============================================================

	public boolean registerUser() {
		if (isUserExists(this.username, this.email)) {
			return false;
		}
		String sql = "INSERT INTO Users (username, email, password, role) VALUES (?, ?, ?, 'user')";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, Datauser.username);
			pstmt.setString(2, this.email);
			pstmt.setString(3, this.password);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isUserExists(String chkUser, String chkEmail) {
		String sql = "SELECT username, email FROM Users WHERE username = ? OR email = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, chkUser);
			pstmt.setString(2, chkEmail);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean checkLogin(String inputUsername, String inputPassword) {
		String sql = "SELECT * FROM Users WHERE (username = ? OR email = ?) AND password = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, inputUsername);
			pstmt.setString(2, inputUsername);
			pstmt.setString(3, inputPassword);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					User user = new User();
					user.userId = rs.getInt("user_id");
					user.username = rs.getString("username");
					user.email = rs.getString("email");
					user.password = rs.getString("password");
					user.role = rs.getString("role");
					user.registeredAt = rs.getString("registered_at");

					Datauser.username = user.username;
					saveUserToFile(user);

					refreshCacheFromServer();

					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public User readUserFromFile() {
		File file = new File("user.txt");
		if (!file.exists())
			return null;

		User user = new User();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("=", 2);
				if (parts.length < 2)
					continue;
				String key = parts[0];
				String value = parts[1];
				switch (key) {
				case "user_id" -> user.userId = Integer.parseInt(value);
				case "username" -> user.username = value;
				case "email" -> user.email = value;
				case "password" -> user.password = value;
				case "role" -> user.role = value;
				case "registered_at" -> user.registeredAt = value;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user;
	}

	public void saveUserToFile(User user) {
		try (FileWriter fw = new FileWriter("user.txt")) {
			fw.write("user_id=" + user.userId + "\n");
			fw.write("username=" + user.username + "\n");
			fw.write("email=" + user.email + "\n");
			fw.write("password=" + user.password + "\n");
			fw.write("role=" + user.role + "\n");
			fw.write("registered_at=" + user.registeredAt + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String autoLoginIfPossible() {
		User user = readUserFromFile();
		if (user != null && user.username != null) {
			return user.username;
		}
		return null;
	}

	public List<Data_user> select_alluser() {
		List<Data_user> userList = new ArrayList<>();
		String sql = "SELECT * FROM Users";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Data_user user = new Data_user();
				user.setId(rs.getInt("user_id"));
				user.setUsername(rs.getString("username"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setRegisteredDate(rs.getTimestamp("registered_at").toLocalDateTime().toString());
				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}

	private void saveMemoryToCacheFile() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(CACHE_FILE))) {
			for (DataAll item : memoryCache) {
				StringBuilder line = new StringBuilder();
				if (item instanceof Room) {
					Room r = (Room) item;
					line.append("ROOM|").append(r.getId()).append("|").append(r.getName()).append("|")
							.append(r.getDescription() == null ? "" : r.getDescription().replace("\n", " ")).append("|")
							.append(r.getCategory()).append("|")
							.append(r.getImagePath() == null ? "" : r.getImagePath()).append("|").append(r.getMapX())
							.append("|").append(r.getMapY()).append("|").append(r.getFloor()).append("|")
							.append(r.getParentBuildingName()).append("|").append(r.getBId());
				} else if (item instanceof Building) {
					Building b = (Building) item;
					line.append("BUILDING|").append(b.getId()).append("|").append(b.getName()).append("|")
							.append(b.getDescription() == null ? "" : b.getDescription().replace("\n", " ")).append("|")
							.append(b.getCategory()).append("|")
							.append(b.getImagePath() == null ? "" : b.getImagePath()).append("|").append(b.getMapX())
							.append("|").append(b.getMapY());
				}
				writer.write(line.toString());
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean loadCacheFromFileToMemory() {
		File file = new File(CACHE_FILE);
		if (!file.exists())
			return false;

		memoryCache.clear();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\\|", -1);
				if (parts.length < 2)
					continue;

				if ("ROOM".equals(parts[0]) && parts.length >= 11) {
					Room r = new Room();
					r.setId(Integer.parseInt(parts[1]));
					r.setName(parts[2]);
					r.setDescription(parts[3]);
					r.setCategory(parts[4]);
					r.setImagePath(parts[5]);
					try {
						r.setMapX(Double.parseDouble(parts[6]));
						r.setMapY(Double.parseDouble(parts[7]));
					} catch (Exception e) {
						r.setMapX(0.0);
						r.setMapY(0.0);
					}
					r.setFloor(parts[8]);
					r.setParentBuildingName(parts[9]);
					r.setBId(Integer.parseInt(parts[10]));
					memoryCache.add(r);
				} else if ("BUILDING".equals(parts[0]) && parts.length >= 8) {
					Building b = new Building();
					b.setId(Integer.parseInt(parts[1]));
					b.setName(parts[2]);
					b.setDescription(parts[3]);
					b.setCategory(parts[4]);
					b.setImagePath(parts[5]);
					try {
						b.setMapX(Double.parseDouble(parts[6]));
						b.setMapY(Double.parseDouble(parts[7]));
					} catch (Exception e) {
						b.setMapX(0.0);
						b.setMapY(0.0);
					}
					memoryCache.add(b);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void refreshCacheFromServer() {
		memoryCache.clear();
		String sql = "SELECT building_id AS id, building_id AS BID, name, description, "
				+ "CAST(category_id AS CHAR) AS category, image_path, "
				+ "NULL AS floor, NULL AS room_num, NULL AS parent_bld, 0 AS is_room, map_x, map_y FROM Buildings "
				+ "UNION ALL "
				+ "SELECT r.room_id AS id, b.building_id AS BID, r.room_name AS name, r.detail AS description, "
				+ "'Classroom' AS category, NULL AS image_path, "
				+ "CAST(r.floor_id AS CHAR) AS floor, r.room_name AS room_num, b.name AS parent_bld, 1 AS is_room, b.map_x, b.map_y "
				+ "FROM Rooms r JOIN Buildings b ON r.building_id = b.building_id";

		try (Connection conn = getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				boolean isRoom = rs.getInt("is_room") == 1;
				if (isRoom) {
					Room r = new Room();
					r.setId(rs.getInt("id"));
					r.setBId(rs.getInt("BID"));
					r.setName(rs.getString("name"));
					r.setDescription(rs.getString("description"));
					r.setCategory(rs.getString("category"));
					r.setImagePath(rs.getString("image_path"));
					r.setMapX(rs.getDouble("map_x"));
					r.setMapY(rs.getDouble("map_y"));
					r.setFloor(rs.getString("floor"));
					r.setRoomNumber(rs.getString("room_num"));
					r.setParentBuildingName(rs.getString("parent_bld"));
					memoryCache.add(r);
				} else {
					Building b = new Building();
					b.setId(rs.getInt("id"));
					b.setName(rs.getString("name"));
					b.setDescription(rs.getString("description"));
					b.setCategory(rs.getString("category"));
					b.setImagePath(rs.getString("image_path"));
					b.setMapX(rs.getDouble("map_x"));
					b.setMapY(rs.getDouble("map_y"));
					memoryCache.add(b);
				}
			}

			saveMemoryToCacheFile();
			System.out.println("Cache refreshed from Server.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getAllData(List<DataAll> listToFill) {
		if (listToFill == null)
			return;
		listToFill.clear();

		if (memoryCache.isEmpty()) {
			loadCacheFromFileToMemory();
		}

		if (memoryCache.isEmpty()) {
			refreshCacheFromServer();
		}
		// 3. ส่งข้อมูลจาก Memory
		listToFill.addAll(memoryCache);
	}

	public void refreshCache() {
		File file = new File(CACHE_FILE);
		if (file.exists()) {
			file.delete();
		}
		memoryCache.clear();
	}

	// =============================================================
	// PART 3: Data Retrieval & Searching
	// =============================================================

	public Building getBuildingById(int bid) {

		// 1. เช็คใน Cache ก่อน (เร็วสุด)
		if (memoryCache.isEmpty())
			loadCacheFromFileToMemory();
		for (DataAll item : memoryCache) {
			if (item instanceof Building && item.getId() == bid) {
				return (Building) item;
			}
		}

		long startTime = System.currentTimeMillis();
		Building b = null;

		// 2. ดึงข้อมูลทั้งหมดออกมา (WHERE 1 คือเอาหมด)
		String sql = "SELECT * FROM Buildings WHERE 1";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	 

			ResultSet rs = pstmt.executeQuery();

		 
			while (rs.next()) {
			 
				int currentId = rs.getInt("building_id");

 
				if (currentId == bid) {
					b = new Building();
					b.setId(currentId);
					b.setName(rs.getString("name"));
					b.setDescription(rs.getString("description"));

				 
					int catId = rs.getInt("category_id");
					b.setCategory(String.valueOf(catId));

					b.setImagePath(rs.getString("image_path"));
					b.setMapX(rs.getDouble("map_x"));
					b.setMapY(rs.getDouble("map_y"));
 
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Time Taken (Manual Search) : " + duration + " ms");

		return b;
	}

	public List<String[]> getTopViewedBuildings(int lm) {
		List<String[]> allData = new ArrayList<>();
		String sql = "SELECT b.building_id, b.name AS building_name, c.name AS category_name, b.image_path, "
				+ "b.map_x, b.map_y, COUNT(a.log_id) AS view_count " + "FROM AccessLogs a "
				+ "JOIN Buildings b ON a.building_id = b.building_id "
				+ "LEFT JOIN Categories c ON b.category_id = c.category_id "
				+ "GROUP BY b.building_id, b.name, c.name, b.image_path, b.map_x, b.map_y";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				String name = rs.getString("building_name");
				String category = rs.getString("category_name");
				if (category == null)
					category = "ทั่วไป";
				String imgPath = rs.getString("image_path");
				String count = String.valueOf(rs.getInt("view_count"));
				String mapX = String.valueOf(rs.getDouble("map_x"));
				String mapY = String.valueOf(rs.getDouble("map_y"));
				String bid = String.valueOf(rs.getInt("building_id"));

				allData.add(new String[] { "0", name, category, count, imgPath, mapX, mapY, bid });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Bubble Sort
		int n = allData.size();
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				int countA = Integer.parseInt(allData.get(j)[3]);
				int countB = Integer.parseInt(allData.get(j + 1)[3]);
				if (countB > countA) {
					String[] temp = allData.get(j);
					allData.set(j, allData.get(j + 1));
					allData.set(j + 1, temp);
				}
			}
		}

		List<String[]> top5List = new ArrayList<>();
		int limit = Math.min(lm, allData.size());
		for (int i = 0; i < limit; i++) {
			String[] row = allData.get(i);
			row[0] = String.valueOf(i + 1);
			top5List.add(row);
		}
		return top5List;
	}

	public Map<String, Integer> getCategoryStats() {
		Map<String, Integer> stats = new HashMap<>();
		String sql = "SELECT c.name AS category_name, COUNT(a.log_id) AS view_count " + "FROM AccessLogs a "
				+ "JOIN Buildings b ON a.building_id = b.building_id "
				+ "JOIN Categories c ON b.category_id = c.category_id " + "GROUP BY c.category_id, c.name";
		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				stats.put(rs.getString("category_name"), rs.getInt("view_count"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stats;
	}

	public List<SearchResult> searchFromDatabase(String keyword) {
		List<SearchResult> results = new ArrayList<>();

		if (memoryCache.isEmpty()) {
			loadCacheFromFileToMemory();
			if (memoryCache.isEmpty())
				refreshCacheFromServer();
		}

		String searchKey = (keyword == null) ? "" : keyword.toLowerCase().trim();

		for (DataAll item : memoryCache) {
			String name = (item.getName() != null) ? item.getName() : "";
			String desc = (item.getDescription() != null) ? item.getDescription() : "";

			String parentBuildingName = "";
			if (item instanceof Room) {
				parentBuildingName = ((Room) item).getParentBuildingName();
				if (parentBuildingName == null)
					parentBuildingName = "";
			}

			if (searchKey.isEmpty() || name.toLowerCase().contains(searchKey) || desc.toLowerCase().contains(searchKey)
					|| parentBuildingName.toLowerCase().contains(searchKey)) {

				int buildingId = 0;
				String imagePath = item.getImagePath();
				String categoryStr = item.getCategory();  
				int categoryId = 0;  

				double lat = item.getMapX();
				double lon = item.getMapY();
				String displayText = name;

				if (item instanceof Building) {
					Building b = (Building) item;
					buildingId = b.getId();
			 
					try {
						categoryId = Integer.parseInt(categoryStr);
					} catch (Exception e) {
					}

				} else if (item instanceof Room) {
					Room r = (Room) item;
					buildingId = r.getBId();
					displayText = "ห้อง " + name + " (" + parentBuildingName + ")";

				 
					for (DataAll parent : memoryCache) {
						if (parent instanceof Building && parent.getId() == buildingId) {
					 
							if (parent.getImagePath() != null && !parent.getImagePath().isEmpty()) {
								imagePath = parent.getImagePath();
							}
							 
							try {
								categoryId = Integer.parseInt(parent.getCategory());
							 
							} catch (Exception e) {
								categoryId = 1; 
							}
							break;
						}
					}
					 
				}

		 
				String displayCategory = getCategoryName(categoryId);

			 
				if (categoryId == 0 && "Classroom".equals(categoryStr)) {
					displayCategory = "ห้องเรียน";
				}

				results.add(
						new SearchResult(displayText, imagePath, displayCategory, categoryId, lat, lon, buildingId));
			}
		}
		return results;
	}

	private String getCategoryName(int catId) {
		switch (catId) {
		case 1:
			return "อาคารเรียน";
		case 2:
			return "ร้านค้า/อาหาร";
		case 3:
			return "หอพัก";
		case 4:
			return "บรรณสาร";
		case 5:
			return "บริการ/เครื่องมือ";
		case 6:
			return "สนามกีฬา";
		case 7:
			return "ยิม/ฟิตเนส";
		case 8:
			return "ตู้ ATM";
		case 9:
			return "หอดูดาว";
		case 10:
			return "อาคารค้นคว้า";
		case 11:
			return "ท่องเที่ยวธรรมชาติ";
		case 12:
			return "อุโมงค์ต้นไม้";
		case 13:
			return "เรื่องเล่าผี";
		default:
			return "ทั่วไป";
		}
	}

	public void insertAccessLog(int buildingId, int userId) {
		String sql = "INSERT INTO AccessLogs (building_id, user_id) VALUES (?, ?)";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, buildingId);
			if (userId > 0)
				pstmt.setInt(2, userId);
			else
				pstmt.setNull(2, java.sql.Types.INTEGER);
			pstmt.executeUpdate();
			System.out.println("Log saved: Building " + buildingId + ", User " + userId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// =============================================================
	// PART 4: CRUD Operations (Update Memory -> Write File)
	// =============================================================

	public boolean addBuilding(String name, int categoryId, String desc, String imgPath, double x, double y) {
		String sql = "INSERT INTO Buildings (name, category_id, description, image_path, map_x, map_y) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection conn = getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, name);
			stmt.setInt(2, categoryId);
			stmt.setString(3, desc);
			stmt.setString(4, imgPath);
			stmt.setDouble(5, x);
			stmt.setDouble(6, y);

			if (stmt.executeUpdate() > 0) {
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						// Update Memory
						int newId = rs.getInt(1);
						Building b = new Building();
						b.setId(newId);
						b.setName(name);
						b.setCategory(String.valueOf(categoryId));
						b.setDescription(desc);
						b.setImagePath(imgPath);
						b.setMapX(x);
						b.setMapY(y);
						memoryCache.add(b);
						// Save to File
						saveMemoryToCacheFile();
					}
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateBuilding(int id, String name, int categoryId, String desc, String imgPath, double x,
			double y) {
		String sql = "UPDATE Buildings SET name=?, category_id=?, description=?, image_path=?, map_x=?, map_y=? WHERE building_id=?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setInt(2, categoryId);
			stmt.setString(3, desc);
			stmt.setString(4, imgPath);
			stmt.setDouble(5, x);
			stmt.setDouble(6, y);
			stmt.setInt(7, id);

			if (stmt.executeUpdate() > 0) {
				// Update Memory
				for (DataAll item : memoryCache) {
					if (item instanceof Building && item.getId() == id) {
						Building b = (Building) item;
						b.setName(name);
						b.setCategory(String.valueOf(categoryId));
						b.setDescription(desc);
						b.setImagePath(imgPath);
						b.setMapX(x);
						b.setMapY(y);
						break;
					}
				}
				// Save to File
				saveMemoryToCacheFile();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteBuilding(int id) {
		String delRooms = "DELETE FROM Rooms WHERE building_id = ?";
		String delBuild = "DELETE FROM Buildings WHERE building_id = ?";
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement s1 = conn.prepareStatement(delRooms)) {
				s1.setInt(1, id);
				s1.executeUpdate();
			}
			try (PreparedStatement s2 = conn.prepareStatement(delBuild)) {
				s2.setInt(1, id);
				s2.executeUpdate();
			}
			conn.commit();

			// Update Memory (Remove Building and its Rooms)
			memoryCache.removeIf(item -> {
				if (item instanceof Building && item.getId() == id)
					return true;
				if (item instanceof Room && ((Room) item).getBId() == id)
					return true;
				return false;
			});
			// Save to File
			saveMemoryToCacheFile();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addRoom(String name, String floor, String detail, int buildingId) {
		String sql = "INSERT INTO Rooms (room_name, floor_id, detail, building_id) VALUES (?, ?, ?, ?)";
		try (Connection conn = getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, name);
			stmt.setString(2, floor);
			stmt.setString(3, detail);
			stmt.setInt(4, buildingId);

			if (stmt.executeUpdate() > 0) {
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						// Find Parent Building for Coords
						double px = 0, py = 0;
						String pName = "";
						for (DataAll d : memoryCache) {
							if (d instanceof Building && d.getId() == buildingId) {
								px = d.getMapX();
								py = d.getMapY();
								pName = d.getName();
								break;
							}
						}
						// Update Memory
						Room r = new Room();
						r.setId(rs.getInt(1));
						r.setName(name);
						r.setFloor(floor);
						r.setDescription(detail);
						r.setBId(buildingId);
						r.setParentBuildingName(pName);
						r.setCategory("Classroom");
						r.setMapX(px);
						r.setMapY(py);
						memoryCache.add(r);
						// Save to File
						saveMemoryToCacheFile();
					}
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateRoom(int roomId, String name, String floor, String detail) {
		String sql = "UPDATE Rooms SET room_name=?, floor_id=?, detail=? WHERE room_id=?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setString(2, floor);
			stmt.setString(3, detail);
			stmt.setInt(4, roomId);

			if (stmt.executeUpdate() > 0) {
				// Update Memory
				for (DataAll item : memoryCache) {
					if (item instanceof Room && item.getId() == roomId) {
						Room r = (Room) item;
						r.setName(name);
						r.setFloor(floor);
						r.setDescription(detail);
						break;
					}
				}
				// Save to File
				saveMemoryToCacheFile();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteRoom(int roomId) {
		String sql = "DELETE FROM Rooms WHERE room_id=?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, roomId);
			if (stmt.executeUpdate() > 0) {
				// Update Memory
				memoryCache.removeIf(item -> item instanceof Room && item.getId() == roomId);
				// Save to File
				saveMemoryToCacheFile();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// =============================================================
	// PART 5: Reset Password
	// =============================================================

	/**
	 * ตรวจสอบว่ามี username หรือ email นี้อยู่ในระบบจริงหรือไม่
	 */
	public boolean isUserExistsByUsernameOrEmail(String input) {
		String sql = "SELECT user_id FROM Users WHERE username = ? OR email = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, input);
			pstmt.setString(2, input);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * อัปเดตรหัสผ่านใหม่ในฐานข้อมูล โดยค้นหาจาก username หรือ email
	 */
	public boolean updatePassword(String usernameOrEmail, String newPassword) {
		String sql = "UPDATE Users SET password = ? WHERE username = ? OR email = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, newPassword);
			pstmt.setString(2, usernameOrEmail);
			pstmt.setString(3, usernameOrEmail);
			int rows = pstmt.executeUpdate();
			if (rows > 0) {
				System.out.println("Password updated for: " + usernameOrEmail);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}