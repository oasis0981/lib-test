package bitedu.bipa.quiz.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import bitedu.bipa.quiz.dao.LibraryDAO;
import bitedu.bipa.quiz.util.DateCalculation;
import bitedu.bipa.quiz.vo.BookUseStatusVO;
import bitedu.bipa.quiz.vo.BookVO;
import bitedu.bipa.quiz.vo.UserBookStatusVO;
import bitedu.bipa.quiz.vo.UserVO;

public class LibraryBookService {
	
	private LibraryDAO dao;
	public LibraryBookService() {
		dao = new LibraryDAO();
	}
	//select * from book_use_status where user_id = 'user1' and borrow_start between '2023-6-1' and '2023-6-30';
	// select * from book_user

	/**
	 * 연체도서를 확인하고 미반납도서 목록에 추가한다.
	 * 연체날짜를 계산한다.
	 * 대출정지기간을 DB에 업데이트하는 dao를 실행한다.
	 * 미반납도서 목록을 리턴한다.
	 */
	private ArrayList<BookUseStatusVO> getNotReturnedBooks(ArrayList<BookUseStatusVO> bookList, Calendar criteriaDate){
		ArrayList<BookUseStatusVO> result = new ArrayList<>();
		Timestamp stopServiceDate = null;
		//미반납도서 - 반납기간이 지나고 반납날짜가 비어있는 도서
		String userId = null;
		for(BookUseStatusVO book : bookList) {
			if( book.getBorrowEnd().getTime() <  criteriaDate.getTimeInMillis() && book.getReturnDate()==null) {
				userId = book.getUserId();
				result.add(book);
				//System.out.println(book);
				Timestamp temp = DateCalculation.calcuStopDate(new Timestamp(criteriaDate.getTimeInMillis()), book.getBorrowEnd());
				if(stopServiceDate!=null) {
					stopServiceDate = temp.getTime()-stopServiceDate.getTime()>0?temp:stopServiceDate;
				} else {
					stopServiceDate = temp;
				}
			}
		}
		
		if(stopServiceDate!=null) {
			//DB user 정보 update
			try {
				dao.updateUserStopStatus(userId, stopServiceDate);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
		return result;
	}

	/**
	 * 반납된 도서를 확인하고 목록에 추가한다.
	 * 반납도서 목록을 리턴한다.
	 */
	private ArrayList<BookUseStatusVO> getReturnedBooks(ArrayList<BookUseStatusVO> bookList){
		ArrayList<BookUseStatusVO> result = new ArrayList<>();
		for(BookUseStatusVO book : bookList) {
			if(book.getReturnDate()!=null&& book.getBorrowEnd().getTime() >= book.getReturnDate().getTime()) {
				result.add(book);
			}
		}
		return result;
	}

	/**
	 * 반납 예정 도서를 확인하고 목록에 추가한다.
	 * 반납예정도서 목록을 리턴한다.
	 */
	private ArrayList<BookUseStatusVO> getExpectingReturnBooks(ArrayList<BookUseStatusVO> bookList, Calendar criteriaDate){
		ArrayList<BookUseStatusVO> result = null;
		//반납예정도서
		result = new ArrayList<BookUseStatusVO>();
		for(BookUseStatusVO book : bookList) {
			if(book.getReturnDate()==null&&book.getBorrowEnd().getTime() >= criteriaDate.getTimeInMillis()) {
				result.add(book);
				//System.out.println(book);
			}
		}
		return result;
	}

	/**
	 * 화면에 표시될 유저 정보와 대출 정보를 해쉬맵으로 만들어 리턴하는 메소드
	 */
	public HashMap<String, Object> getUserStatus(String userId,String startMonth){
		HashMap<String, Object> result = new HashMap<>();
		
		UserVO user = null;
		ArrayList<BookUseStatusVO> list = null;
	
		try {
			list = dao.selectBookInfoByUser(userId, startMonth);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Calendar criteriaDate = Calendar.getInstance();
		criteriaDate.set(2023, 5, 16); // todo: 하드코딩 -> 오늘 날짜로 수정
		ArrayList<BookUseStatusVO> allReturned = this.getReturnedBooks(list);
		ArrayList<BookUseStatusVO> notReturned = this.getNotReturnedBooks(list, criteriaDate);
		ArrayList<BookUseStatusVO> expectingReturn = this.getExpectingReturnBooks(list,criteriaDate);
		
		try {
			user = dao.selectUser(userId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HashMap<String, ArrayList<BookUseStatusVO>> bookInfo = new HashMap<>();
		bookInfo.put("total", list);
		bookInfo.put("allReturned", allReturned);
		bookInfo.put("notReturned", notReturned);
		bookInfo.put("expectingReturn", expectingReturn);
		
		UserBookStatusVO userState = new UserBookStatusVO();
		userState.setUserId(userId);
		userState.setAvailableBook(user.getAvailableBook());
		userState.setExpectingReturnBook(expectingReturn.size());
		userState.setTotalUsingBook(list.size());
		userState.setReturnedBook(allReturned.size());
		userState.setNotReturnedBook(notReturned.size());
		

		if(notReturned.size()>0) {
			userState.setUserState("대출정지");
			userState.setStopDate(DateCalculation.getDate(user.getServiceStop()));
		} else {
			userState.setUserState("정상");
			userState.setStopDate("-");
		}
		HashMap<String, UserBookStatusVO> userInfo = new HashMap<>();
		
		userInfo.put("user", userState);
		result.put("userInfo", userInfo);
		result.put("bookInfo", bookInfo);
		
		return result;
	}

	public void borrowBooks(int bookSeq, String userId){
		dao.updateBorrow(bookSeq, userId);
	}
}
