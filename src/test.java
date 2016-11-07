
public class test {

	
	public static void main(String[] args) {
		String name = "test-test-1.2.4.jar";
		String bathName = name.substring(0,name.lastIndexOf("."));
		String version = "";
		if(bathName.matches("(.*)(\\d*)")){
			//System.out.println("hahahah");
			version = bathName.substring(bathName.lastIndexOf("-")+1);
			System.out.println(version);
		}
		bathName = bathName.replaceAll("\\d|\\.|(-)$", "");
		System.out.println(bathName);
		
		if(bathName.matches("(.*)(-)")){
			
			bathName = bathName.substring(0,bathName.length() - 1);
		}
		/*if(bathName.contains("\\d")){
			System.out.println(bathName.substring(0, bathName.lastIndexOf("\\d")));
		}*/
		 
		System.out.println(bathName);
	}
}
