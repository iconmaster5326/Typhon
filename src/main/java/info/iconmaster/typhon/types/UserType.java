package info.iconmaster.typhon.types;

public class UserType extends Type {
	Type parent;
	String name;

	public UserType(String name, Type parent) {
		this.name = name;
		this.parent = parent;
	}
}