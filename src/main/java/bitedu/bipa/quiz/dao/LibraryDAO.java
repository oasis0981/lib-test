package bitedu.bipa.quiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import bitedu.bipa.quiz.util.ConnectionManager;
import bitedu.bipa.quiz.util.DateCalculation;
import bitedu.bipa.quiz.vo.BookUseStatusVO;
import bitedu.bipa.quiz.vo.BookVO;
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
	
	
	/**
	 * ISBN으로 책제목 찾기
	 */
	public String searchBookTitle(String isbn) {
		String title = "";
		String sql = "select book_title from book_info where book_isbn=?";
		Connection con = manager.getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, isbn);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				title = rs.getString(1);
			}
			manager.closeConnection(rs, pstmt, con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
		return title;
	}
	
	/**
	 * bookseq로 검색할 값을 입력받아 책 정보를 받아오는 메소드
	 */
	public BookVO selectSearchedBook(int bookSeq) {
		BookVO book = null;
		String sql = "select * from book_copy where book_seq = ?";
		try {
			Connection con = manager.getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, bookSeq);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				book = new BookVO(rs.getInt(1), rs.getString(2),
						rs.getString(3), rs.getString(4));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return book;
	}

	/**
	 * 대출 트랜잭션
	 */
	public void updateBorrow(int bookSeq, String userId) {
		Connection con = null;
		try {
			con = manager.getConnection();
			con.setAutoCommit(false);


			// book_copy 테이블 book_position 업데이트
			String sql = "update book_copy set book_position=? where book_seq=?";
			PreparedStatement pstmt1 = con.prepareStatement(sql);
			pstmt1.setString(1, "BB-" + userId);
			pstmt1.setInt(2, bookSeq);
			int rows1 = pstmt1.executeUpdate();
			if (rows1 == 0) throw new Exception("book_copy 업데이트 실패");
			pstmt1.close();


			// book_use_status 테이블에 새로운 레코드 추가
			StringBuilder sb = new StringBuilder();
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			sb.append("insert into book_use_status(book_seq, user_id, borrow_start, borrow_end, return_date) ");
			sb.append("values (?, ?, ?, ?, ?)");
			PreparedStatement pstmt2 = con.prepareStatement(sb.toString());
			pstmt2.setInt(1, bookSeq);
			pstmt2.setString(2, userId);
			pstmt2.setTimestamp(3, ts); // 오늘
			pstmt2.setTimestamp(4, DateCalculation.getEndnDate(ts, 13)); // 14일 후 반납 예정일
			pstmt2.setTimestamp(5, null);
			int rows2 = pstmt2.executeUpdate();
			if (rows2 == 0) throw new Exception("book_use_status 업데이트 실패");
			pstmt2.close();


			// book_user 테이블 대출가능권수 업데이트
			String sql2 = "update book_user set max_book = max_book-1 where user_id=? AND max_book > 0";
			PreparedStatement pstmt3 = con.prepareStatement(sql2);
			pstmt3.setString(1, userId);
			int rows3 = pstmt3.executeUpdate();
			if (rows3 == 0) throw new Exception("book_user 대출가능권수 업데이트 실패");
			pstmt3.close();

			con.commit();
			System.out.println("대출 성공");
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
			}
			System.out.println("대출 실패");
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}
		
}
