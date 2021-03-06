package info.iconmaster.typhon.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import info.iconmaster.typhon.model.libs.CorePackage;
import info.iconmaster.typhon.types.TemplateType;
import info.iconmaster.typhon.types.TypeRef;

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
	 * @return The parent of this member. May be null.
	 */
	public MemberAccess getMemberParent();
	
	/**
	 * @return A list of all members that can be looked up from this entity.
	 * Behavior may change based on if linking, type resolution, or compiling has been done.
	 */
	public default List<MemberAccess> getMembers(Map<TemplateType, TypeRef> templateMap) {
		return Arrays.asList();
	}
	
	/**
	 * @param name The name to look up.
	 * @return A list of all members that can be looked up from this entity with the given name.
	 * Behavior may change based on if linking, type resolution, or compiling has been done.
	 */
	public default List<MemberAccess> getMembers(String name, Map<TemplateType, TypeRef> templateMap) {
		return getMembers(templateMap).stream().filter((e)->name.equals(e.getName())).collect(Collectors.toList());
	}
	
	public default Map<TemplateType, TypeRef> getTemplateMap(Map<TemplateType, TypeRef> templateMap) {
		return null;
	}
	
	/**
	 * @return A string, suitable for output to the user, that can be reconstructed to find this object's location.
	 */
	public default String getPathString() {
		StringBuilder sb = new StringBuilder();
		
		MemberAccess base = getMemberParent();
		while (base != null) {
			if (!(base instanceof CorePackage)) {
				String name = base.getName();
				if (name != null) {
					sb.append(name);
					sb.append('.');
				}
			}
			
			base = base.getMemberParent();
		}
		
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
}
