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
import bitedu.bipa.quiz.vo.UserBookStatusVO;

public class BorrowServlet extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("a");
		String bookSeq = req.getParameter("bookSeq");
		String userId = req.getParameter("userId");
		System.out.println("bookSeq: "+bookSeq);
		
		// 대출 실행
		this.borrow(Integer.valueOf(bookSeq), userId);
		
		// 유저 정보 갱신 JSON 받아오기
		String data = this.getData(userId);
		
		System.out.println("working");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.print(data);
		out.close();
		
	}
	
	private String getData(String userId) {
		String result = null;
		LibraryBookService lbs = new LibraryBookService();
		HashMap<String, Object> data = lbs.getUserStatus(userId, "6"); // todo : startMonth 는 뭘까..(쓰이지 않음)
		HashMap<String,UserBookStatusVO>  map1 = (HashMap<String,UserBookStatusVO>)data.get("userInfo");
		HashMap<String,ArrayList<BookUseStatusVO>>  map2 = (HashMap<String,ArrayList<BookUseStatusVO>>)data.get("bookInfo");
		
		UserBookStatusVO status = map1.get("user");
		ArrayList<BookUseStatusVO> list = map2.get("total");
		ArrayList<BookUseStatusVO> allReturned = map2.get("allReturned");
		ArrayList<BookUseStatusVO> notReturned = map2.get("notReturned");
		ArrayList<BookUseStatusVO> expectingReturn = map2.get("expectingReturn");
		

		JSONObject json = new JSONObject();
		JSONObject info = new JSONObject();
		JSONObject user = new JSONObject();
		JSONObject book = new JSONObject();
		
		JSONArray array1 = new JSONArray();
		JSONArray array2 = new JSONArray();
		JSONArray array3 = new JSONArray();
		JSONArray array4 = new JSONArray();
		
		JSONArray array5 = new JSONArray();
		array5.add(status);
		
		user.put("user", array5);
		
		for(BookUseStatusVO vo : list) {
			array1.add(vo);
		}
		book.put("list", array1);
		
		for(BookUseStatusVO vo : allReturned) {
			array2.add(vo);
		}
		book.put("returned", array2);
		
		for(BookUseStatusVO vo : notReturned) {
			array3.add(vo);
		}
		book.put("notReturned", array3);
		
		for(BookUseStatusVO vo : expectingReturn) {
			array4.add(vo);
			//System.out.println(vo);
		}
		book.put("expectingReturn", array4);
		
		
		info.put("userInfo", user);
		info.put("bookInfo", book);
		
		json.put("data", info);

		System.out.println(json.toJSONString());
		
		result = json.toJSONString();
		return result;
	}
	
	private void borrow(int bookSeq, String userId){
		LibraryBookService lbs = new LibraryBookService();
		lbs.borrowBooks(bookSeq, userId);
	}
}
