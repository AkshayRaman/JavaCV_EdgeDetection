import java.io.IOException;


public class ProcessRunner {
	
	public static void runProcess(String Executable)
	{
		if(Executable.trim().length()>0){
			try {
				Runtime.getRuntime().exec("cmd /c start "+"\""+Executable+"\"");
			} catch (IOException e) {
				System.out.println("ERROR: ProcessRunner "+e.getMessage());
				e.printStackTrace();
			}
	}

}
}
