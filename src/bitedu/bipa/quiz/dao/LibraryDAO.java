package bitedu.bipa.quiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import bitedu.bipa.quiz.util.ConnectionManager;
import bitedu.bipa.quiz.vo.BookUseStatusVO;
import bitedu.bipa.quiz.vo.UserVO;

public class LibraryDAO {

	private ConnectionManager manager;
	
	// 비즈니스 판별은 로직에서
	public LibraryDAO() {
		this.manager = ConnectionManager.getInstance();
	}
	
	
	public UserVO selectUser(String userId) throws SQLException {
		UserVO user = null;
		String sql = "select user_status,max_book,service_stop from book_user where user_id = ?";
		Connection con = manager.getConnection();
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()) {
			user = new UserVO();
			user.setUserId(userId);
			user.setUserState(rs.getString(1));
			user.setAvailableBook(rs.getInt(2));
			user.setServiceStop(rs.getTimestamp(3));
		}
		
		manager.closeConnection(rs, pstmt, con);
		return user;
	}
	

	public ArrayList<BookUseStatusVO> selectBookInfoByUser(String userId,String startMonth) throws SQLException {
		ArrayList<BookUseStatusVO> list = null;
		list = new ArrayList<BookUseStatusVO>();
		StringBuilder sb = new StringBuilder("select i.book_isbn,i.book_title,i.book_author,s.* ");
		sb.append("from book_copy c  inner join (book_info i) on c.book_isbn = i.book_isbn ");
		sb.append("inner join book_use_status s on s.book_seq = c.book_seq ");
		sb.append("where s.user_id = ? and s.borrow_start between '2023-6-1' and '2023-6-30'");
		String sql = sb.toString();
		Connection con = manager.getConnection();
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, userId);
		ResultSet rs = pstmt.executeQuery();
		BookUseStatusVO vo = null;
		while(rs.next()) {
			vo = new BookUseStatusVO(rs.getInt(4), rs.getString(5), rs.getTimestamp(6), rs.getTimestamp(7), rs.getTimestamp(8));
			vo.setBookIsbn(rs.getString(1));
			vo.setBookTitle(rs.getString(2));
			vo.setBookAuthor(rs.getString(3));
			list.add(vo);
		}
		
		manager.closeConnection(rs, pstmt, con);
		
		return list;
	}
	
	public boolean updateUserStopStatus(String userId, Timestamp stopDate) throws SQLException {
		boolean flag = false;
		String sql = "update book_user set user_status= ?, service_stop = ? where user_id = ?";
		try {
			Connection con = manager.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "01");
			pstmt.setTimestamp(2, stopDate);
			pstmt.setString(3, userId);
			int affectedCount = pstmt.executeUpdate();
			if(affectedCount>0) {
				flag = true;
				//System.out.println("success");
			}
			manager.closeConnection(null, pstmt, con);
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return flag;
	}
		
}
