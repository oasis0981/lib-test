package bitedu.bipa.quiz.vo;

/**
 * 상단 유저 요약 정보를 나타내기 위한 VO
 */
public class UserBookStatusVO {

	// book_user 테이블: 회원의 대출 상태를 표시
	private String userId;
	private String userState;
	private String stopDate;
	private int availableBook;

	// 회원 대출 정보를 표시하기 위한 추가 정보
	private int totalUsingBook;
	private int returnedBook;
	private int notReturnedBook;
	private int expectingReturnBook;

	public UserBookStatusVO() {}
	public UserBookStatusVO(String userId, String userState, String stopDate, int totalUsingBook, int returnedBook,
			int notReturnedBook, int expectingReturnBook, int availableBook) {
		super();
		this.userId = userId;
		this.userState = userState;
		this.stopDate = stopDate;
		this.totalUsingBook = totalUsingBook;
		this.returnedBook = returnedBook;
		this.notReturnedBook = notReturnedBook;
		this.expectingReturnBook = expectingReturnBook;
		this.availableBook = availableBook;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserState() {
		return userState;
	}
	public void setUserState(String userState) {
		this.userState = userState;
	}
	public String getStopDate() {
		return stopDate;
	}
	public void setStopDate(String stopDate) {
		this.stopDate = stopDate;
	}
	public int getTotalUsingBook() {
		return totalUsingBook;
	}
	public void setTotalUsingBook(int totalUsingBook) {
		this.totalUsingBook = totalUsingBook;
	}
	public int getReturnedBook() {
		return returnedBook;
	}
	public void setReturnedBook(int returnedBook) {
		this.returnedBook = returnedBook;
	}
	public int getNotReturnedBook() {
		return notReturnedBook;
	}
	public void setNotReturnedBook(int notReturnedBook) {
		this.notReturnedBook = notReturnedBook;
	}
	public int getExpectingReturnBook() {
		return expectingReturnBook;
	}
	public void setExpectingReturnBook(int expectingReturnBook) {
		this.expectingReturnBook = expectingReturnBook;
	}
	public int getAvailableBook() {
		return availableBook;
	}
	public void setAvailableBook(int availableBook) {
		this.availableBook = availableBook;
	}
	@Override
	public String toString() {
		return "{\"userId\":\"" + userId + "\", \"userState\":\"" + userState + "\", \"stopDate\":\"" + stopDate
				+ "\", \"totalUsingBook\":\"" + totalUsingBook + "\", \"returnedBook\":\"" + returnedBook + "\", \"notReturnedBook\":\""
				+ notReturnedBook + "\", \"expectingReturnBook\":\"" + expectingReturnBook + "\", \"availableBook\":\"" + availableBook
				+ "\"}";
	}
	
	
}
