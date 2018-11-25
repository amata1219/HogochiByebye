package amata1219.hogochi.byebye;

public enum IdType {

	ADMIN("admin_"),
	USER("user_"),
	ROAD("mainflatroad_");

	private final String id;

	private IdType(String id){
		this.id = id;
	}

	public String getString(){
		return id;
	}

}
