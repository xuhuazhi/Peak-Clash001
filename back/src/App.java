import com.xiguazhi.ui.FightingGame;
import com.xiguazhi.ui.Login;
public class App {
    public static void main(String[] args) {
       //启动类
        //这个类只负责启动整个程序，里面不写任何的业务逻辑
        //启动登录注册的页面
        Login l = new Login(); // Changed from Login l = new Login(); to Login l = new Login();
        l.start();
    }
}
