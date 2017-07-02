package info.iconmaster.typhon.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import info.iconmaster.typhon.types.TemplateType;

/**
 * This represents an entity that can be obtained somehow through member lookup,
 * and has named entities as members.
 * 
 * @author iconmaster
 *
 */
public interface MemberAccess {
	/**
	 * @return The name of the member. Must be a valid Typhon identifier. May be null.
	 */
	public String getName();
	
	/**
	 * @return The template specification for this member.
	 */
	public default List<TemplateType> getMemberTemplate() {
		return Arrays.asList();
	}
	
	/**
	 * @return A list of all members that can be looked up from this entity.
	 * Behavior may change based on if linking, type resolution, or compiling has been done.
	 */
	public default List<MemberAccess> getMembers() {
		return Arrays.asList();
	}
	
	/**
	 * @param name The name to look up.
	 * @return A list of all members that can be looked up from this entity with the given name.
	 * Behavior may change based on if linking, type resolution, or compiling has been done.
	 */
	public default List<MemberAccess> getMembers(String name) {
		return getMembers().stream().filter((e)->name.equals(e.getName())).collect(Collectors.toList());
	}
}