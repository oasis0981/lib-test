package bitedu.bipa.quiz.vo;

public class BookVO {
	private int bookSeq;
	private String bookPosition;
	private String bookStatus;
	private String bookIsbn;
	
	public BookVO(int bookSeq, String bookPosition, String bookStatus, String bookIsbn) {
		// book_copy 테이블. 개별 책 한권에 대한 위치 정보와 상태 정보를 포함
		super();
		this.bookSeq = bookSeq;
		this.bookPosition = bookPosition;
		this.bookStatus = bookStatus;
		this.bookIsbn = bookIsbn;
	}
	
	public int getBookSeq() {
		return bookSeq;
	}
	public void setBookSeq(int bookSeq) {
		this.bookSeq = bookSeq;
	}
	public String getBookPosition() {
		return bookPosition;
	}
	public void setBookPosition(String bookPosition) {
		this.bookPosition = bookPosition;
	}
	public String getBookStatus() {
		return bookStatus;
	}
	public void setBookStatus(String bookStatus) {
		this.bookStatus = bookStatus;
	}
	public String getBookIsbn() {
		return bookIsbn;
	}
	public void setBookIsbn(String bookIsbn) {
		this.bookIsbn = bookIsbn;
	}
	
	@Override
	public String toString() {
		return "{\"bookSeq\":\"" + bookSeq + "\", \"bookPosition\":\"" + bookPosition + "\", \"bookStatus\":\"" + bookStatus
				+ "\", \"bookIsbn\":\"" + bookIsbn + "\"}";
	}
	

}
