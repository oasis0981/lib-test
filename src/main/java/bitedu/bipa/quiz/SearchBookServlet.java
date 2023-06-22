package bitedu.bipa.quiz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import bitedu.bipa.quiz.service.LibraryBookService;
import bitedu.bipa.quiz.vo.BookUseStatusVO;
import bitedu.bipa.quiz.vo.BookVO;
import bitedu.bipa.quiz.vo.UserBookStatusVO;

public class SearchBookServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String bookSeq = req.getParameter("bookSeq");
		System.out.println("bookSeq: "+bookSeq);
		
		// 유저 정보 갱신 JSON 받아오기
		String data = this.getData(Integer.valueOf(bookSeq));
		
		System.out.println("working");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.print(data);
		out.close();
		
	}
	
	private String getData(int bookSeq) {
		String result = null;
		LibraryBookService lbs = new LibraryBookService();
		System.out.println(bookSeq);
		BookVO book = this.searchBook(bookSeq);
		String title = lbs.searchTitle(book.getBookIsbn());
		
		JSONObject bookInfo = new JSONObject();
		JSONObject json = new JSONObject();
		bookInfo.put("bookSeq", book.getBookSeq());
		bookInfo.put("location", book.getBookPosition());
		bookInfo.put("bookStatus", book.getBookStatus());
		bookInfo.put("isbn", book.getBookIsbn());
		bookInfo.put("title", title);
		json.put("bookInfo", bookInfo);
		
		System.out.println(bookInfo.toJSONString());
		
		result = json.toJSONString();
		return result;
	}
	
	private BookVO searchBook(int bookSeq){
		LibraryBookService lbs = new LibraryBookService();
		BookVO book = lbs.searchBook(bookSeq);
		return book;
	}
}
