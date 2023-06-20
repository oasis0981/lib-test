package bitedu.bipa.quiz.util;

public class CM {

    // 싱글톤: 객체가 하나만 있다느 것을 보장
    private static CM cm;


    private void CM(){
    };

    public static CM getInstance(){
        if(cm == null){
            cm = new CM();
        }
        return cm;
    }

}
